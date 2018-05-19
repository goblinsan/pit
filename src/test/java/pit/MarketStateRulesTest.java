package pit;

import org.junit.Before;
import org.junit.Test;
import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MarketStateRulesTest {

    private Market testObject;

    @Before
    public void setUp() {
        testObject = new Market();
    }

    @Test
    public void canGetAllScheduledTimesAsMap() {
        LocalDateTime timeInit = LocalDateTime.ofEpochSecond(0,0,ZoneOffset.UTC);
        Map<String, LocalDateTime> expectedSchedule = new HashMap<>();
        expectedSchedule.put("enrollmentStart", timeInit);
        expectedSchedule.put("enrollmentEnd", timeInit);
        expectedSchedule.put("marketStart", timeInit);
        expectedSchedule.put("marketEnd", timeInit);

        assertThat(testObject.getSchedule(), is(expectedSchedule));
    }

    @Test
    public void initialStateIsMarketClosed() {
        assertEquals(MarketState.CLOSED, testObject.getState(LocalDateTime.now()));
    }

    @Test
    public void initialEnrollmentNotOpen() {
        try {
            testObject.getEnrollmentOpen();
        } catch (MarketSchedule e) {
            assertEquals(GameResponse.UNSCHEDULED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_SCHEDULED, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void canScheduleEnrollmentWhenMarketIsClosed() {
        LocalDateTime expectedStartTime = LocalDateTime.now().plusMinutes(5);
        assertEquals(GameResponse.SCHEDULED, testObject.scheduleEnrollment(expectedStartTime));
        assertEquals(expectedStartTime, testObject.getEnrollmentOpen());
    }

    @Test
    public void cantScheduleEnrollmentWhenClosed() {
        // TODO: need open logic first
    }

    @Test
    public void defaultEnrollmentWindowIsOneMinute() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
        testObject.scheduleEnrollment(startTime);
        LocalDateTime expectedEndTime = startTime.plusMinutes(1);
        assertEquals(expectedEndTime, testObject.getEnrollmentEnd());
    }

    @Test
    public void defaultMarketOpenOneMinutedAfterEnrollmentClose() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
        testObject.scheduleEnrollment(startTime);
        LocalDateTime expectedEndTime = startTime.plusMinutes(2);
        assertEquals(expectedEndTime, testObject.getMarketOpen());
    }

    @Test
    public void defaultMarketCloseFiveMinutesAfterOpen() {
        LocalDateTime startTime = LocalDateTime.now();
        testObject.scheduleEnrollment(startTime);
        LocalDateTime expectedEndTime = startTime.plusMinutes(7);
        assertEquals(expectedEndTime, testObject.getMarketEnd());
    }

    @Test
    public void getMarketStateAtGivenTime() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5).minusSeconds(30);
        testObject.scheduleEnrollment(startTime);

        assertEquals(MarketState.CLOSED, testObject.getState(LocalDateTime.now()));
        assertEquals(MarketState.ENROLLMENT_OPEN, testObject.getState(LocalDateTime.now().plusMinutes(5)));
        assertEquals(MarketState.ENROLLMENT_CLOSED, testObject.getState(LocalDateTime.now().plusMinutes(6)));
        assertEquals(MarketState.OPEN, testObject.getState(LocalDateTime.now().plusMinutes(7)));
        assertEquals(MarketState.CLOSED, testObject.getState(LocalDateTime.now().plusMinutes(12)));
    }
}