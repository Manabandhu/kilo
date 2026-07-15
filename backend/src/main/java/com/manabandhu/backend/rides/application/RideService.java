package com.manabandhu.backend.rides.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.rides.api.RideApi;
import com.manabandhu.backend.rides.api.RideApi.CreateRideRequest;
import com.manabandhu.backend.rides.api.RideApi.CreateSeatRequest;
import com.manabandhu.backend.rides.api.RideApi.RateRequest;
import com.manabandhu.backend.rides.api.RideApi.RideResponse;
import com.manabandhu.backend.rides.api.RideApi.SeatRequestResponse;
import com.manabandhu.backend.rides.domain.RideStatusMachine;
import com.manabandhu.backend.rides.infrastructure.RideEntity;
import com.manabandhu.backend.rides.infrastructure.RideEntity.RideStatus;
import com.manabandhu.backend.rides.infrastructure.RideRatingEntity;
import com.manabandhu.backend.rides.infrastructure.RideRatingRepository;
import com.manabandhu.backend.rides.infrastructure.RideRequestEntity;
import com.manabandhu.backend.rides.infrastructure.RideRequestEntity.SeatRequestStatus;
import com.manabandhu.backend.rides.infrastructure.RideRepository;
import com.manabandhu.backend.rides.infrastructure.RideRequestRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideRequestRepository requestRepository;
    private final RideRatingRepository ratingRepository;

    public RideService(RideRepository rideRepository, RideRequestRepository requestRepository,
                       RideRatingRepository ratingRepository) {
        this.rideRepository = rideRepository;
        this.requestRepository = requestRepository;
        this.ratingRepository = ratingRepository;
    }

    public PagedResult<RideResponse> search(String type, String state, String query, CurrentUser user, int page, int size) {
        Page<RideEntity> p = rideRepository.search(type, state, query, user.userId(), PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(RideApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    public RideResponse get(UUID id) {
        return RideApi.toResponse(require(id));
    }

    @Transactional
    public RideResponse create(CurrentUser user, CreateRideRequest req) {
        return RideApi.toResponse(rideRepository.save(RideApi.toEntity(req, user.userId())));
    }

    @Transactional
    public RideResponse updateStatus(CurrentUser user, UUID id, String status) {
        RideEntity e = require(id);
        Authorization.requireOwner(user, e.getDriverId());
        RideStatus to = RideStatus.valueOf(status);
        RideStatusMachine.assertTransition(e.getStatus(), to);
        e.setStatus(to);
        return RideApi.toResponse(rideRepository.save(e));
    }

    @Transactional
    public SeatRequestResponse requestSeat(CurrentUser user, UUID id, CreateSeatRequest req) {
        RideEntity ride = require(id);
        if (!RideStatusMachine.isActive(ride.getStatus())) {
            throw BusinessException.businessRule("Ride is not accepting requests");
        }
        if (ride.getAvailableSeats() < req.seatsRequested()) {
            throw BusinessException.businessRule("Not enough available seats");
        }
        RideRequestEntity r = new RideRequestEntity();
        r.setRideId(id);
        r.setRiderId(user.userId());
        r.setSeatsRequested(req.seatsRequested());
        r.setRiderNotes(req.riderNotes());
        r.setStatus(SeatRequestStatus.pending);
        return RideApi.toResponse(requestRepository.save(r));
    }

    @Transactional
    public SeatRequestResponse decideSeat(CurrentUser user, UUID id, UUID reqId, String decision) {
        RideEntity ride = require(id);
        Authorization.requireOwner(user, ride.getDriverId());
        RideRequestEntity r = requestRepository.findById(reqId)
                .orElseThrow(() -> BusinessException.notFound("Seat request not found"));
        if (r.getStatus() != SeatRequestStatus.pending) {
            throw BusinessException.businessRule("Seat request already decided");
        }
        if ("approved".equals(decision)) {
            r.setStatus(SeatRequestStatus.approved);
            int remaining = ride.getAvailableSeats() - r.getSeatsRequested();
            ride.setAvailableSeats(Math.max(remaining, 0));
            if (ride.getAvailableSeats() == 0) {
                ride.setStatus(RideStatus.full);
            }
            rideRepository.save(ride);
        } else {
            r.setStatus(SeatRequestStatus.rejected);
        }
        return RideApi.toResponse(requestRepository.save(r));
    }

    @Transactional
    public void rate(CurrentUser user, UUID id, RateRequest req) {
        RideEntity ride = require(id);
        if (ride.getStatus() != RideStatus.completed) {
            throw BusinessException.businessRule("Can only rate completed rides");
        }
        if (ride.getDriverId().equals(user.userId()) && req.toUserId().equals(user.userId())) {
            throw BusinessException.businessRule("Cannot rate yourself");
        }
        RideRatingEntity rating = new RideRatingEntity();
        rating.setRideId(id);
        rating.setFromUserId(user.userId());
        rating.setToUserId(req.toUserId());
        rating.setScore(req.score());
        rating.setComment(req.comment());
        ratingRepository.save(rating);
    }

    private RideEntity require(UUID id) {
        return rideRepository.findById(id).filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> BusinessException.notFound("Ride not found"));
    }
}
