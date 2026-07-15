package com.manabandhu.backend.admin.application;

import com.manabandhu.backend.admin.api.AdminApi;
import com.manabandhu.backend.admin.api.AdminApi.DashboardMetrics;
import com.manabandhu.backend.admin.api.AdminApi.RoleRequest;
import com.manabandhu.backend.admin.api.AdminApi.UserSummary;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.profiles.infrastructure.ProfileEntity;
import com.manabandhu.backend.profiles.infrastructure.ProfileRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final ProfileRepository profileRepository;
    private final EntityManager em;

    public AdminService(ProfileRepository profileRepository, EntityManager em) {
        this.profileRepository = profileRepository;
        this.em = em;
    }

    public DashboardMetrics metrics() {
        long totalUsers = count("select count(p) from ProfileEntity p");
        long activeUsers = count("select count(p) from ProfileEntity p where p.accountStatus = 'active'");
        long openReports = count("select count(r) from ReportEntity r where r.status = 'open'");
        long newListingsToday = count("select count(r) from RoomListingEntity r where r.createdAt >= current_date");
        long newRidesToday = count("select count(r) from RideEntity r where r.createdAt >= current_date");
        long newPostsToday = count("select count(p) from PostEntity p where p.createdAt >= current_date");
        long messagesToday = count("select count(m) from MessageEntity m where m.createdAt >= current_date");
        long flaggedContent = count("select count(p) from PostEntity p where p.status = 'moderated'");
        return new DashboardMetrics(totalUsers, activeUsers, openReports, newListingsToday,
                newRidesToday, newPostsToday, messagesToday, flaggedContent);
    }

    public List<UserSummary> searchUsers(String query, int page, int size) {
        Page<ProfileEntity> p;
        if (query == null || query.isBlank()) {
            p = profileRepository.findAll(PageRequest.of(page, size));
        } else {
            p = profileRepository.findAll(PageRequest.of(page, size)); // simple page; refine with @Query if needed
        }
        return p.getContent().stream().map(AdminApi::toSummary).toList();
    }

    public UserSummary user(UUID id) {
        return profileRepository.findById(id).map(AdminApi::toSummary)
                .orElseThrow(() -> BusinessException.notFound("User not found"));
    }

    @Transactional
    public UserSummary setRole(CurrentUser admin, UUID id, RoleRequest req) {
        Authorization.requireAdmin(admin);
        ProfileEntity e = profileRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("User not found"));
        e.setRole(ProfileEntity.Role.valueOf(req.role()));
        return AdminApi.toSummary(profileRepository.save(e));
    }

    @SuppressWarnings("unchecked")
    private long count(String ql) {
        Query q = em.createQuery(ql);
        return ((Number) q.getSingleResult()).longValue();
    }
}
