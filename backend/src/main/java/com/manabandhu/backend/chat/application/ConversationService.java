package com.manabandhu.backend.chat.application;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.chat.domain.Conversation;
import com.manabandhu.backend.chat.domain.ConversationMember;
import com.manabandhu.backend.chat.domain.ConversationMemberId;
import com.manabandhu.backend.chat.domain.Report;
import com.manabandhu.backend.chat.infrastructure.ConversationRepository;
import com.manabandhu.backend.chat.infrastructure.ConversationMemberRepository;
import com.manabandhu.backend.chat.infrastructure.MessageRepository;
import com.manabandhu.backend.chat.infrastructure.DtoMapper;
import com.manabandhu.backend.chat.infrastructure.ReportRepository;
import com.manabandhu.backend.chat.api.dto.ConversationDto;
import com.manabandhu.backend.chat.api.dto.MessageSummaryDto;
import com.manabandhu.backend.chat.api.request.CreateConversationRequest;
import com.manabandhu.backend.chat.api.request.ReportRequest;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final ReportRepository reportRepository;

    public ConversationService(ConversationRepository conversationRepository, ConversationMemberRepository memberRepository, MessageRepository messageRepository, ReportRepository reportRepository) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.reportRepository = reportRepository;
    }

    public Page<ConversationDto> listConversations(CurrentUser user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Conversation> conversations = conversationRepository.findByMember(user.userId(), pageable);

        return conversations.map(conv -> {
            List<ConversationMember> members = memberRepository.findById_ConversationId(conv.getId());
            List<UUID> memberIds = members.stream().map(m -> m.getId().getUserId()).toList();
            boolean muted = members.stream().anyMatch(m -> m.getId().getUserId().equals(user.userId()) && m.isMuted());
            long unreadCount = messageRepository.countUnread(conv.getId(), user.userId());
            var lastMessageOpt = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conv.getId());
            MessageSummaryDto lastMessage = lastMessageOpt.map(m -> DtoMapper.toSummaryDto(m)).orElse(null);
            return DtoMapper.toDto(conv, memberIds, lastMessage, unreadCount, muted);
        });
    }

    public ConversationDto getConversation(UUID id, CurrentUser user) {
        Conversation conv = conversationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Conversation not found"));

        boolean isMember = memberRepository.existsById_ConversationIdAndId_UserId(id, user.userId());
        if (!isMember) {
            throw BusinessException.forbidden("You are not a member of this conversation");
        }

        List<ConversationMember> members = memberRepository.findById_ConversationId(id);
        List<UUID> memberIds = members.stream().map(m -> m.getId().getUserId()).toList();
        boolean muted = members.stream().anyMatch(m -> m.getId().getUserId().equals(user.userId()) && m.isMuted());
        return DtoMapper.toDto(conv, memberIds, null, 0, muted);
    }

    public ConversationDto createConversation(CreateConversationRequest req, CurrentUser user) {
        if (!"direct".equals(req.kind()) && !"group".equals(req.kind())) {
            throw BusinessException.validation("Invalid conversation kind", Map.of("kind", "must be direct or group"));
        }

        if ("direct".equals(req.kind())) {
            if (req.memberIds() == null || req.memberIds().size() != 1) {
                throw BusinessException.validation("Direct conversations require exactly one other member", Map.of("memberIds", "must contain exactly one member"));
            }
        }

        if ("group".equals(req.kind())) {
            if (req.title() == null || req.title().isBlank()) {
                throw BusinessException.validation("Group conversations require a title", Map.of("title", "must not be blank"));
            }
            if (req.memberIds() == null || req.memberIds().isEmpty()) {
                throw BusinessException.validation("Group conversations require members", Map.of("memberIds", "must not be empty"));
            }
        }

        Conversation conv = new Conversation();
        conv.setKind(req.kind());
        conv.setTitle(req.title());
        conv.setUpdatedAt(Instant.now());
        conversationRepository.save(conv);

        ConversationMember owner = new ConversationMember(new ConversationMemberId(conv.getId(), user.userId()), "member", false, Instant.now());
        memberRepository.save(owner);

        if (req.memberIds() != null) {
            for (UUID memberId : req.memberIds()) {
                if (memberId.equals(user.userId())) continue;
                ConversationMember cm = new ConversationMember(new ConversationMemberId(conv.getId(), memberId), "member", false, Instant.now());
                memberRepository.save(cm);
            }
        }

        List<ConversationMember> members = memberRepository.findById_ConversationId(conv.getId());
        List<UUID> memberIds = members.stream().map(m -> m.getId().getUserId()).toList();
        return DtoMapper.toDto(conv, memberIds, null, 0, false);
    }

    public void muteConversation(UUID id, CurrentUser user) {
        ConversationMember member = memberRepository.findById_ConversationIdAndId_UserId(id, user.userId())
                .orElseThrow(() -> BusinessException.forbidden("You are not a member of this conversation"));

        member.setMuted(!member.isMuted());
        memberRepository.save(member);
    }

    public void reportConversation(UUID id, ReportRequest req, CurrentUser user) {
        Conversation conv = conversationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Conversation not found"));

        boolean isMember = memberRepository.existsById_ConversationIdAndId_UserId(id, user.userId());
        if (!isMember) {
            throw BusinessException.forbidden("You are not a member of this conversation");
        }

        Report report = new Report();
        report.setReporterId(user.userId());
        report.setTargetType("conversation");
        report.setTargetId(id);
        report.setReason(req.reason());
        report.setDetail(req.detail());
        report.setStatus("open");
        report.setUpdatedAt(Instant.now());
        reportRepository.save(report);
    }
}
