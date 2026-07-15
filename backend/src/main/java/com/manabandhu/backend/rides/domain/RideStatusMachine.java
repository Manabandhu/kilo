package com.manabandhu.backend.rides.domain;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.rides.infrastructure.RideEntity.RideStatus;
import java.util.Set;

/** Explicit ride status transition rules. Invalid transitions throw BusinessException. */
public final class RideStatusMachine {

    private RideStatusMachine() {
    }

    private static final Set<RideStatus> ACTIVE = Set.of(RideStatus.published, RideStatus.full, RideStatus.in_progress);

    public static void assertTransition(RideStatus from, RideStatus to) {
        if (from == to) {
            return;
        }
        if (from == RideStatus.cancelled || from == RideStatus.completed) {
            throw BusinessException.businessRule("Cannot change a " + from + " ride");
        }
        if (to == RideStatus.cancelled) {
            if (!ACTIVE.contains(from) && from != RideStatus.draft) {
                throw BusinessException.businessRule("Only active rides can be cancelled");
            }
            return;
        }
        if (to == RideStatus.published && from != RideStatus.draft) {
            throw BusinessException.businessRule("Ride can only be published from draft");
        }
        if (to == RideStatus.full && from != RideStatus.published) {
            throw BusinessException.businessRule("Ride can only become full from published");
        }
        if (to == RideStatus.in_progress && from != RideStatus.published && from != RideStatus.full) {
            throw BusinessException.businessRule("Ride must be published/full before in_progress");
        }
        if (to == RideStatus.completed && from != RideStatus.in_progress) {
            throw BusinessException.businessRule("Ride can only be completed from in_progress");
        }
    }

    public static boolean isActive(RideStatus s) {
        return ACTIVE.contains(s);
    }
}
