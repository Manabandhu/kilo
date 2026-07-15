package com.manabandhu.backend.rides.api;

import com.manabandhu.backend.rides.infrastructure.RideEntity;
import com.manabandhu.backend.rides.infrastructure.RideRequestEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class RideApi {

    private RideApi() {
    }

    public record RideResponse(
            UUID id, String type, String frequency, String origin, String originLocation, String destination,
            String destinationLocation, String stops, LocalDate date, String time, String timeFlexibility,
            int availableSeats, String suggestedContribution, String luggageInfo, String driverNotes,
            String status, UUID driverId, String createdAt, String updatedAt) {
    }

    public record CreateRideRequest(
            String type, String frequency, String origin, String originLocation, String destination,
            String destinationLocation, String stops, LocalDate date, String time, String timeFlexibility,
            int availableSeats, String suggestedContribution, String luggageInfo, String driverNotes) {
    }

    public record SeatRequestResponse(
            UUID id, UUID rideId, UUID riderId, int seatsRequested, String riderNotes, String status,
            String createdAt, String updatedAt) {
    }

    public record CreateSeatRequest(int seatsRequested, String riderNotes) {
    }

    public record RateRequest(int score, String comment, UUID toUserId) {
    }

    public static RideResponse toResponse(RideEntity e) {
        return new RideResponse(e.getId(), e.getType(), e.getFrequency(), e.getOrigin(), e.getOriginLocation(),
                e.getDestination(), e.getDestinationLocation(), e.getStops(), e.getDate(), e.getTime(),
                e.getTimeFlexibility(), e.getAvailableSeats(),
                e.getSuggestedContribution() == null ? null : e.getSuggestedContribution().toPlainString(),
                e.getLuggageInfo(), e.getDriverNotes(), e.getStatus().name(), e.getDriverId(),
                e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }

    public static SeatRequestResponse toResponse(RideRequestEntity e) {
        return new SeatRequestResponse(e.getId(), e.getRideId(), e.getRiderId(), e.getSeatsRequested(),
                e.getRiderNotes(), e.getStatus().name(), e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }

    public static RideEntity toEntity(CreateRideRequest r, UUID driverId) {
        RideEntity e = new RideEntity();
        e.setType(r.type());
        e.setFrequency(r.frequency());
        e.setOrigin(r.origin());
        e.setOriginLocation(r.originLocation());
        e.setDestination(r.destination());
        e.setDestinationLocation(r.destinationLocation());
        e.setStops(r.stops() == null ? "[]" : r.stops());
        e.setDate(r.date());
        e.setTime(r.time());
        e.setTimeFlexibility(r.timeFlexibility());
        e.setAvailableSeats(r.availableSeats());
        e.setSuggestedContribution(r.suggestedContribution() == null ? null : new BigDecimal(r.suggestedContribution()));
        e.setLuggageInfo(r.luggageInfo());
        e.setDriverNotes(r.driverNotes());
        e.setDriverId(driverId);
        e.setStatus(RideEntity.RideStatus.published);
        return e;
    }
}
