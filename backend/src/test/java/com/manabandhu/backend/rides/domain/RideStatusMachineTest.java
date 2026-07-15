package com.manabandhu.backend.rides.domain;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.rides.infrastructure.RideEntity.RideStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideStatusMachineTest {

    @Test
    void draft_to_published_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.draft, RideStatus.published));
    }

    @Test
    void published_to_full_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.published, RideStatus.full));
    }

    @Test
    void published_or_full_to_in_progress_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.published, RideStatus.in_progress));
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.full, RideStatus.in_progress));
    }

    @Test
    void in_progress_to_completed_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.in_progress, RideStatus.completed));
    }

    @Test
    void active_to_cancelled_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.published, RideStatus.cancelled));
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.full, RideStatus.cancelled));
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.in_progress, RideStatus.cancelled));
    }

    @Test
    void draft_to_cancelled_allowed() {
        assertDoesNotThrow(() -> RideStatusMachine.assertTransition(RideStatus.draft, RideStatus.cancelled));
    }

    @Test
    void completed_cannot_transition() {
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.completed, RideStatus.cancelled));
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.completed, RideStatus.in_progress));
    }

    @Test
    void cancelled_cannot_transition() {
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.cancelled, RideStatus.published));
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.cancelled, RideStatus.completed));
    }

    @Test
    void invalid_transitions_rejected() {
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.published, RideStatus.draft));
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.in_progress, RideStatus.full));
        assertThrows(BusinessException.class, () -> RideStatusMachine.assertTransition(RideStatus.completed, RideStatus.published));
    }

    @Test
    void isActive_returnsTrueForActiveStates() {
        assertTrue(RideStatusMachine.isActive(RideStatus.published));
        assertTrue(RideStatusMachine.isActive(RideStatus.full));
        assertTrue(RideStatusMachine.isActive(RideStatus.in_progress));
        assertFalse(RideStatusMachine.isActive(RideStatus.draft));
        assertFalse(RideStatusMachine.isActive(RideStatus.completed));
        assertFalse(RideStatusMachine.isActive(RideStatus.cancelled));
    }
}