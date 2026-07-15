package com.manabandhu.backend.moderation.application;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.moderation.api.ModerationApi.ActionRequest;
import com.manabandhu.backend.moderation.api.ModerationApi.ActionResponse;
import com.manabandhu.backend.moderation.api.ModerationApi.NoteRequest;
import com.manabandhu.backend.moderation.api.ModerationApi.NoteResponse;
import com.manabandhu.backend.moderation.infrastructure.AuditLogEntity;
import com.manabandhu.backend.moderation.infrastructure.AuditLogRepository;
import com.manabandhu.backend.moderation.infrastructure.ModerationActionEntity;
import com.manabandhu.backend.moderation.infrastructure.ModerationActionRepository;
import com.manabandhu.backend.moderation.infrastructure.ModerationNoteEntity;
import com.manabandhu.backend.moderation.infrastructure.ModerationNoteRepository;
import com.manabandhu.backend.profiles.infrastructure.ProfileEntity;
import com.manabandhu.backend.profiles.infrastructure.ProfileRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModerationService {

    private final ModerationActionRepository actionRepository;
    private final ModerationNoteRepository noteRepository;
    private final AuditLogRepository auditLogRepository;
    private final ProfileRepository profileRepository;

    public ModerationService(ModerationActionRepository actionRepository, ModerationNoteRepository noteRepository,
                             AuditLogRepository auditLogRepository, ProfileRepository profileRepository) {
        this.actionRepository = actionRepository;
        this.noteRepository = noteRepository;
        this.auditLogRepository = auditLogRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ActionResponse action(CurrentUser staff, ActionRequest req) {
        Authorization.requireStaff(staff);
        ModerationActionEntity a = new ModerationActionEntity();
        a.setType(req.type());
        a.setModeratorId(staff.userId());
        a.setTargetUserId(req.targetUserId());
        a.setReportId(req.reportId());
        a.setTargetType(req.targetType());
        a.setTargetId(req.targetId());
        a.setNote(req.note());
        actionRepository.save(a);

        applyStatusChange(req.type(), req.targetUserId());
        recordAudit(staff.userId(), "moderation." + req.type(), "user", req.targetUserId(), req.note());
        return ModerationApi.toResponse(a);
    }

    @Transactional
    public NoteResponse note(CurrentUser staff, NoteRequest req) {
        Authorization.requireStaff(staff);
        ModerationNoteEntity n = new ModerationNoteEntity();
        n.setModeratorId(staff.userId());
        n.setTargetUserId(req.targetUserId());
        n.setBody(req.body());
        return ModerationApi.toResponse(noteRepository.save(n));
    }

    public List<NoteResponse> notes(UUID targetUserId) {
        return noteRepository.findByTargetUserId(targetUserId).stream().map(ModerationApi::toResponse).toList();
    }

    private void applyStatusChange(String type, UUID targetUserId) {
        if (targetUserId == null) return;
        ProfileEntity p = profileRepository.findById(targetUserId).orElse(null);
        if (p == null) return;
        switch (type) {
            case "user_suspended" -> p.setAccountStatus(ProfileEntity.AccountStatus.suspended);
            case "user_banned" -> p.setAccountStatus(ProfileEntity.AccountStatus.banned);
            case "user_reinstated" -> p.setAccountStatus(ProfileEntity.AccountStatus.active);
            default -> { /* note, warning, content_removed, report_* */ }
        }
        profileRepository.save(p);
    }

    private void recordAudit(UUID actorId, String action, String entityType, UUID entityId, String metadata) {
        AuditLogEntity log = new AuditLogEntity();
        log.setActorId(actorId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMetadata(metadata == null ? null : "{\"note\":\"" + metadata.replace("\"", "'") + "\"}");
        auditLogRepository.save(log);
    }
}
