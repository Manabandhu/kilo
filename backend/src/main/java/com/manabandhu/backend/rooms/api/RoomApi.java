package com.manabandhu.backend.rooms.api;

import com.manabandhu.backend.rooms.infrastructure.RoomListingEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class RoomApi {

    private RoomApi() {
    }

    public record RoomResponse(
            UUID id, String title, String description, String monthlyRent, String securityDeposit,
            String city, String state, String zip, String approximateLocation, boolean exactAddressShared,
            String roomType, String propertyType, String furnished, LocalDate availableDate, Integer leaseDurationMonths,
            boolean utilitiesIncluded, String[] amenities, boolean parking, String petPolicy, String smokingPolicy,
            int occupancy, String genderPreference, String contactPreference, String[] images, String status,
            UUID posterId, String createdAt, String updatedAt) {
    }

    public record CreateRoomRequest(
            String title, String description, String monthlyRent, String securityDeposit,
            String city, String state, String zip, String approximateLocation, Boolean exactAddressShared,
            String roomType, String propertyType, String furnished, LocalDate availableDate, Integer leaseDurationMonths,
            boolean utilitiesIncluded, String[] amenities, boolean parking, String petPolicy, String smokingPolicy,
            int occupancy, String genderPreference, String contactPreference, String[] images) {
    }

    public record UpdateRoomRequest(
            String title, String description, String monthlyRent, String securityDeposit,
            String city, String state, String zip, Boolean exactAddressShared,
            String roomType, String propertyType, String furnished, LocalDate availableDate, Integer leaseDurationMonths,
            Boolean utilitiesIncluded, String[] amenities, Boolean parking, String petPolicy, String smokingPolicy,
            Integer occupancy, String genderPreference, String contactPreference, String[] images, String status) {
    }

    public record SimilarRoom(UUID id, String title, String city, String state, String monthlyRent, String[] images) {
    }

    public static RoomResponse toResponse(RoomListingEntity e, boolean hideExact) {
        String loc = (hideExact || !e.isExactAddressShared()) ? null : e.getApproximateLocation();
        return new RoomResponse(
                e.getId(), e.getTitle(), e.getDescription(), e.getMonthlyRent().toPlainString(),
                e.getSecurityDeposit() == null ? null : e.getSecurityDeposit().toPlainString(),
                e.getCity(), e.getState(), hideExact ? null : e.getZip(), loc, e.isExactAddressShared(),
                e.getRoomType(), e.getPropertyType(), e.getFurnished(), e.getAvailableDate(), e.getLeaseDurationMonths(),
                e.isUtilitiesIncluded(), e.getAmenities(), e.isParking(), e.getPetPolicy(), e.getSmokingPolicy(),
                e.getOccupancy(), e.getGenderPreference(), e.getContactPreference(), e.getImages(),
                e.getStatus().name(), e.getPosterId(),
                e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }

    public static RoomListingEntity toEntity(CreateRoomRequest r, UUID posterId) {
        RoomListingEntity e = new RoomListingEntity();
        applyCommon(e, r.title(), r.description(), r.monthlyRent(), r.securityDeposit(), r.city(), r.state(),
                r.zip(), r.approximateLocation(), r.exactAddressShared(), r.roomType(), r.propertyType(),
                r.furnished(), r.availableDate(), r.leaseDurationMonths(), r.utilitiesIncluded(), r.amenities(),
                r.parking(), r.petPolicy(), r.smokingPolicy(), r.occupancy(), r.genderPreference(),
                r.contactPreference(), r.images());
        e.setPosterId(posterId);
        e.setStatus(RoomListingEntity.RoomStatus.active);
        return e;
    }

    public static void applyUpdate(RoomListingEntity e, UpdateRoomRequest r) {
        if (r.title() != null) e.setTitle(r.title());
        if (r.description() != null) e.setDescription(r.description());
        if (r.monthlyRent() != null) e.setMonthlyRent(new BigDecimal(r.monthlyRent()));
        if (r.securityDeposit() != null) e.setSecurityDeposit(new BigDecimal(r.securityDeposit()));
        if (r.city() != null) e.setCity(r.city());
        if (r.state() != null) e.setState(r.state());
        if (r.zip() != null) e.setZip(r.zip());
        if (r.exactAddressShared() != null) e.setExactAddressShared(r.exactAddressShared());
        if (r.roomType() != null) e.setRoomType(r.roomType());
        if (r.propertyType() != null) e.setPropertyType(r.propertyType());
        if (r.furnished() != null) e.setFurnished(r.furnished());
        if (r.availableDate() != null) e.setAvailableDate(r.availableDate());
        if (r.leaseDurationMonths() != null) e.setLeaseDurationMonths(r.leaseDurationMonths());
        if (r.utilitiesIncluded() != null) e.setUtilitiesIncluded(r.utilitiesIncluded());
        if (r.amenities() != null) e.setAmenities(r.amenities());
        if (r.parking() != null) e.setParking(r.parking());
        if (r.petPolicy() != null) e.setPetPolicy(r.petPolicy());
        if (r.smokingPolicy() != null) e.setSmokingPolicy(r.smokingPolicy());
        if (r.occupancy() != null) e.setOccupancy(r.occupancy());
        if (r.genderPreference() != null) e.setGenderPreference(r.genderPreference());
        if (r.contactPreference() != null) e.setContactPreference(r.contactPreference());
        if (r.images() != null) e.setImages(r.images());
        if (r.status() != null) e.setStatus(RoomListingEntity.RoomStatus.valueOf(r.status()));
    }

    private static void applyCommon(RoomListingEntity e, String title, String description, String rent,
            String deposit, String city, String state, String zip, String loc, Boolean exact, String roomType,
            String propertyType, String furnished, LocalDate avail, Integer lease, boolean utils, String[] amenities,
            boolean parking, String pet, String smoking, int occupancy, String gender, String contact, String[] images) {
        e.setTitle(title);
        e.setDescription(description);
        e.setMonthlyRent(new BigDecimal(rent));
        e.setSecurityDeposit(deposit == null ? null : new BigDecimal(deposit));
        e.setCity(city);
        e.setState(state);
        e.setZip(zip);
        e.setApproximateLocation(loc);
        e.setExactAddressShared(Boolean.TRUE.equals(exact));
        e.setRoomType(roomType);
        e.setPropertyType(propertyType);
        e.setFurnished(furnished);
        e.setAvailableDate(avail);
        e.setLeaseDurationMonths(lease);
        e.setUtilitiesIncluded(utils);
        e.setAmenities(amenities == null ? new String[0] : amenities);
        e.setParking(parking);
        e.setPetPolicy(pet);
        e.setSmokingPolicy(smoking);
        e.setOccupancy(occupancy);
        e.setGenderPreference(gender);
        e.setContactPreference(contact);
        e.setImages(images == null ? new String[0] : images);
    }
}
