package com.manabandhu.backend.profiles.application;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.profiles.api.ProfileApi;
import com.manabandhu.backend.profiles.api.ProfileApi.ProfileUpdateRequest;
import com.manabandhu.backend.profiles.infrastructure.BlockedUserEntity;
import com.manabandhu.backend.profiles.infrastructure.BlockedUserRepository;
import com.manabandhu.backend.profiles.infrastructure.ProfileEntity;
import com.manabandhu.backend.profiles.infrastructure.ProfileRepository;
import com.manabandhu.backend.profiles.infrastructure.SavedItemEntity;
import com.manabandhu.backend.profiles.infrastructure.SavedItemRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final SavedItemRepository savedItemRepository;

    public ProfileService(ProfileRepository profileRepository,
                          BlockedUserRepository blockedUserRepository,
                          SavedItemRepository savedItemRepository) {
        this.profileRepository = profileRepository;
        this.blockedUserRepository = blockedUserRepository;
        this.savedItemRepository = savedItemRepository;
    }

    public ProfileApi.ProfileResponse getMe(CurrentUser user) {
        return ProfileApi.toResponse(requireProfile(user.userId()));
    }

    public ProfileApi.PublicProfileSummary getPublic(UUID id) {
        return ProfileApi.toSummary(requireProfile(id));
    }

    @Transactional
    public ProfileApi.ProfileResponse update(CurrentUser user, ProfileUpdateRequest req) {
        ProfileEntity e = requireProfile(user.userId());
        if (req.displayName() != null) e.setDisplayName(req.displayName());
        if (req.bio() != null) e.setBio(req.bio());
        if (req.city() != null) e.setCity(req.city());
        if (req.state() != null) e.setState(req.state());
        if (req.languages() != null) e.setLanguages(req.languages());
        if (req.occupation() != null) e.setOccupation(req.occupation());
        if (req.archetype() != null) e.setArchetype(safeArchetype(req.archetype()));
        if (req.university() != null) e.setUniversity(req.university());
        if (req.employer() != null) e.setEmployer(req.employer());
        if (req.interests() != null) e.setInterests(req.interests());
        e.setProfileCompletionScore(computeCompletion(e));
        e.setOnboardingCompleted(e.getProfileCompletionScore() >= 60);
        return ProfileApi.toResponse(profileRepository.save(e));
    }

    @Transactional
    public void block(CurrentUser user, UUID blockedUserId) {
        if (user.userId().equals(blockedUserId)) {
            throw BusinessException.validation("Cannot block yourself", null);
        }
        if (!blockedUserRepository.existsByUserIdAndBlockedUserId(user.userId(), blockedUserId)) {
            BlockedUserEntity b = new BlockedUserEntity();
            b.setUserId(user.userId());
            b.setBlockedUserId(blockedUserId);
            blockedUserRepository.save(b);
        }
    }

    @Transactional
    public void unblock(CurrentUser user, UUID blockedUserId) {
        blockedUserRepository.deleteByUserIdAndBlockedUserId(user.userId(), blockedUserId);
    }

    public List<ProfileApi.PublicProfileSummary> blocked(CurrentUser user) {
        return blockedUserRepository.findAll().stream()
                .filter(b -> b.getUserId().equals(user.userId()))
                .map(b -> ProfileApi.toSummary(requireProfile(b.getBlockedUserId())))
                .toList();
    }

    @Transactional
    public void saveItem(CurrentUser user, String itemType, UUID itemId) {
        if (!savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), itemType, itemId)) {
            SavedItemEntity s = new SavedItemEntity();
            s.setUserId(user.userId());
            s.setItemType(itemType);
            s.setItemId(itemId);
            savedItemRepository.save(s);
        }
    }

    @Transactional
    public void unsaveItem(CurrentUser user, String itemType, UUID itemId) {
        savedItemRepository.deleteByUserIdAndItemTypeAndItemId(user.userId(), itemType, itemId);
    }

    public ProfileApi.DataExportResponse requestExport(CurrentUser user) {
        // In production this triggers an async export job; here we acknowledge.
        return new ProfileApi.DataExportResponse(user.userId(), "queued",
                "Your data export has been requested and will be emailed when ready.");
    }

    @Transactional
    public void requestDeletion(CurrentUser user, ProfileApi.DeletionRequest req) {
        if (!req.confirmation()) {
            throw BusinessException.validation("Deletion requires confirmation", null);
        }
        ProfileEntity e = requireProfile(user.userId());
        e.setAccountStatus(ProfileEntity.AccountStatus.deleted);
        profileRepository.save(e);
    }

    private int computeCompletion(ProfileEntity e) {
        int score = 0;
        if (e.getDisplayName() != null && !e.getDisplayName().isBlank()) score += 20;
        if (e.getAvatarUrl() != null && !e.getAvatarUrl().isBlank()) score += 10;
        if (e.getCity() != null && !e.getCity().isBlank()) score += 10;
        if (e.getState() != null && !e.getState().isBlank()) score += 10;
        if (e.getLanguages() != null && e.getLanguages().length > 0) score += 10;
        if (e.getOccupation() != null && !e.getOccupation().isBlank()) score += 10;
        if (e.getArchetype() != null) score += 10;
        if (e.getInterests() != null && e.getInterests().length > 0) score += 10;
        if (e.getBio() != null && !e.getBio().isBlank()) score += 10;
        return Math.min(score, 100);
    }

    private ProfileEntity.Archetype safeArchetype(String v) {
        if (v == null) return null;
        try {
            return ProfileEntity.Archetype.valueOf(v);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private ProfileEntity requireProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Profile not found"));
    }
}
