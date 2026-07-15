package com.manabandhu.backend.rooms.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.rooms.api.RoomApi;
import com.manabandhu.backend.rooms.api.RoomApi.CreateRoomRequest;
import com.manabandhu.backend.rooms.api.RoomApi.RoomResponse;
import com.manabandhu.backend.rooms.api.RoomApi.UpdateRoomRequest;
import com.manabandhu.backend.rooms.infrastructure.RoomListingEntity;
import com.manabandhu.backend.rooms.infrastructure.RoomListingEntity.RoomStatus;
import com.manabandhu.backend.rooms.infrastructure.RoomRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public PagedResult<RoomResponse> search(String city, String state, String roomType, String propertyType,
                                            BigDecimal minRent, BigDecimal maxRent, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomListingEntity> p = repository.search(city, state, roomType, propertyType, minRent, maxRent, query, pageable);
        return PagedResult.of(p.getContent().stream().map(e -> RoomApi.toResponse(e, false)).toList(),
                page, size, p.getTotalElements());
    }

    public RoomResponse get(UUID id, CurrentUser user) {
        RoomListingEntity e = require(id);
        boolean hide = !e.getPosterId().equals(user.userId());
        return RoomApi.toResponse(e, hide);
    }

    @Transactional
    public RoomResponse create(CurrentUser user, CreateRoomRequest req) {
        RoomListingEntity e = RoomApi.toEntity(req, user.userId());
        return RoomApi.toResponse(repository.save(e), false);
    }

    @Transactional
    public RoomResponse update(CurrentUser user, UUID id, UpdateRoomRequest req) {
        RoomListingEntity e = require(id);
        Authorization.requireOwner(user, e.getPosterId());
        RoomApi.applyUpdate(e, req);
        return RoomApi.toResponse(repository.save(e), false);
    }

    @Transactional
    public void delete(CurrentUser user, UUID id) {
        RoomListingEntity e = require(id);
        Authorization.requireOwner(user, e.getPosterId());
        e.setStatus(RoomStatus.deleted);
        e.setDeletedAt(LocalDate.now());
        repository.save(e);
    }

    public PagedResult<RoomResponse> similar(UUID id, int page, int size) {
        RoomListingEntity base = require(id);
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomListingEntity> p = repository.search(base.getCity(), base.getState(), null, null,
                null, null, null, pageable);
        return PagedResult.of(p.getContent().stream()
                .filter(e -> !e.getId().equals(id))
                .map(e -> RoomApi.toResponse(e, true)).toList(), page, size, p.getTotalElements());
    }

    private RoomListingEntity require(UUID id) {
        return repository.findById(id).filter(e -> e.getStatus() != RoomStatus.deleted)
                .orElseThrow(() -> BusinessException.notFound("Room listing not found"));
    }
}
