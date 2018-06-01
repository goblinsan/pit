package pit;

import org.springframework.stereotype.Component;
import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class Market {
    private static final LocalDateTime timeInit = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
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
        } else if (time.isAfter(marketEnd) && time.isBefore(enrollmentStart)){
            return MarketState.CLOSED;
        } else {
            return MarketState.UNSCHEDULED;
        }
    }

    public Map<String, LocalDateTime> getSchedule() {
        Map<String, LocalDateTime> schedule = new LinkedHashMap<>();
        schedule.put("enrollmentStart", enrollmentStart);
        schedule.put("enrollmentEnd", enrollmentEnd);
        schedule.put("marketStart", marketStart);
        schedule.put("marketEnd", marketEnd);
        return schedule;
    }

    public GameResponse scheduleEnrollment(LocalDateTime startTime) {
        enrollmentStart = startTime;
        enrollmentEnd = startTime.plusMinutes(1);
        marketStart = startTime.plusMinutes(2);
        marketEnd = startTime.plusMinutes(7);
        return GameResponse.SCHEDULED;
    }

    public GameResponse scheduleMarketOpen(LocalDateTime startTime) {
        enrollmentStart = timeInit;
        enrollmentEnd = timeInit;
        marketStart = startTime;
        marketEnd = startTime.plusMinutes(5);
        return GameResponse.SCHEDULED;
    }

    public GameResponse scheduleMarketClose(LocalDateTime endTime) {
        enrollmentStart = timeInit;
        enrollmentEnd = timeInit;
        marketStart = timeInit;
        marketEnd = endTime;
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
