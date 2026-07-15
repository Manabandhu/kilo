package com.manabandhu.backend.admin.api;

import com.manabandhu.backend.admin.api.AdminApi.DashboardMetrics;
import com.manabandhu.backend.admin.api.AdminApi.RoleRequest;
import com.manabandhu.backend.admin.api.AdminApi.UserSummary;
import com.manabandhu.backend.admin.application.AdminService;
import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.moderation.infrastructure.AuditLogEntity;
import com.manabandhu.backend.moderation.infrastructure.AuditLogRepository;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogRepository auditLogRepository;

    public AdminController(AdminService adminService, AuditLogRepository auditLogRepository) {
        this.adminService = adminService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/metrics")
    public DashboardMetrics metrics() {
        return adminService.metrics();
    }

    @GetMapping("/users")
    public List<UserSummary> users(@RequestParam(required = false) String query,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return adminService.searchUsers(query, page, size);
    }

    @GetMapping("/users/{id}")
    public UserSummary user(@PathVariable UUID id) {
        return adminService.user(id);
    }

    @PostMapping("/users/{id}/role")
    public UserSummary role(@AuthenticatedUser CurrentUser admin, @PathVariable UUID id, @Valid @RequestBody RoleRequest req) {
        return adminService.setRole(admin, id, req);
    }

    @GetMapping("/audit-logs")
    public PagedResult<AuditLogView> audit(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogEntity> p = auditLogRepository.recent(PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(a -> new AuditLogView(
                a.getId(), a.getActorId(), a.getAction(), a.getEntityType(), a.getEntityId(),
                a.getCreatedAt().toString())).toList(), page, size, p.getTotalElements());
    }

    public record AuditLogView(UUID id, UUID actorId, String action, String entityType, UUID entityId, String createdAt) {
    }
}
