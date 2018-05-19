package pit;

import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

class Market {
    private static final LocalDateTime timeInit = LocalDateTime.ofEpochSecond(0,0,ZoneOffset.UTC);
    private LocalDateTime enrollmentStart = timeInit;
    private LocalDateTime enrollmentEnd = timeInit;
    private LocalDateTime marketStart = timeInit;
    private LocalDateTime marketEnd = timeInit;

    MarketState getState(LocalDateTime time) {
        if (time.isAfter(enrollmentStart) && time.isBefore(enrollmentEnd)) {
            return MarketState.ENROLLMENT_OPEN;
        } else if (time.isAfter(enrollmentEnd) && time.isBefore(marketStart)) {
            return MarketState.ENROLLMENT_CLOSED;
        } else if (time.isAfter(marketStart) && time.isBefore(marketEnd)) {
            return MarketState.OPEN;
        } else {
            return MarketState.CLOSED;
        }
    }

    GameResponse scheduleEnrollment(LocalDateTime startTime) {
        enrollmentStart = startTime;
        enrollmentEnd = startTime.plusMinutes(1);
        marketStart = startTime.plusMinutes(2);
        marketEnd = startTime.plusMinutes(7);
        return GameResponse.SCHEDULED;
    }

    LocalDateTime getEnrollmentOpen() {
        isEnrollmentUnscheduled();
        return enrollmentStart;
    }

    LocalDateTime getEnrollmentEnd() {
        isEnrollmentUnscheduled();
        return enrollmentEnd;
    }

    LocalDateTime getMarketOpen() {
        return marketStart;
    }

    LocalDateTime getMarketEnd() {
        return marketEnd;
    }

    private void isEnrollmentUnscheduled() {
        if (enrollmentStart.isEqual(timeInit) || enrollmentEnd.isEqual(timeInit)) {
            throw new MarketSchedule(GameResponse.UNSCHEDULED, ErrorMessages.MARKET_NOT_SCHEDULED);
        }
    }
}
