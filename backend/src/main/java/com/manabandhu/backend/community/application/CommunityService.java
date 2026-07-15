package com.manabandhu.backend.community.application;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.community.domain.Community;
import com.manabandhu.backend.community.domain.CommunityMembership;
import com.manabandhu.backend.community.infrastructure.CommunityMembershipRepository;
import com.manabandhu.backend.community.infrastructure.CommunityRepository;
import com.manabandhu.backend.community.infrastructure.DtoMapper;
import com.manabandhu.backend.community.api.dto.CommunityDto;
import com.manabandhu.backend.community.api.dto.MembershipDto;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;

    public CommunityService(CommunityRepository communityRepository, CommunityMembershipRepository membershipRepository) {
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
    }

    public Page<CommunityDto> listCommunities(String search, String kind, String category, String city, String state, int page, int size, String sort) {
        Specification<Community> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(kind)) {
                predicates.add(cb.equal(root.get("kind"), kind));
            }
            if (StringUtils.hasText(category)) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (StringUtils.hasText(city)) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (StringUtils.hasText(state)) {
                predicates.add(cb.equal(cb.lower(root.get("state")), state.toLowerCase()));
            }
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sortOrder = Sort.by("createdAt").descending();
        if (StringUtils.hasText(sort)) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                sortOrder = Sort.by(parts[0]).descending();
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Community> communities = communityRepository.findAll(spec, pageable);
        return communities.map(DtoMapper::toDto);
    }

    public CommunityDto getCommunity(UUID id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));
        return DtoMapper.toDto(community);
    }

    public MembershipDto joinCommunity(UUID communityId, CurrentUser user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));

        CommunityMembershipId id = new com.manabandhu.backend.community.domain.CommunityMembershipId(communityId, user.userId());
        if (membershipRepository.existsById_CommunityIdAndId_UserId(communityId, user.userId())) {
            throw BusinessException.conflict("Already a member of this community");
        }

        CommunityMembership membership = new CommunityMembership(id, "member", java.time.Instant.now());
        membershipRepository.save(membership);
        community.setMemberCount(community.getMemberCount() + 1);
        communityRepository.save(community);
        return DtoMapper.toDto(membership);
    }

    public void leaveCommunity(UUID communityId, CurrentUser user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));

        CommunityMembershipId id = new com.manabandhu.backend.community.domain.CommunityMembershipId(communityId, user.userId());
        CommunityMembership membership = membershipRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Not a member of this community"));

        membershipRepository.delete(membership);
        if (community.getMemberCount() > 0) {
            community.setMemberCount(community.getMemberCount() - 1);
            communityRepository.save(community);
        }
    }
}
