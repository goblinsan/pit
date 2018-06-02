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
    private final LocalDateTime timeInit =LocalDateTime.ofEpochSecond(0,0,ZoneOffset.UTC);

    @Before
    public void setUp() {
        testObject = new Market();
    }


    @Test
    public void initMarketStateIsUnscheduled() {
        assertEquals(MarketState.UNSCHEDULED, testObject.getState(LocalDateTime.now()));
    }

    @Test
    public void canGetAllScheduledTimesAsMap() {
        Map<String, LocalDateTime> expectedSchedule = new HashMap<>();
        expectedSchedule.put("enrollmentStart", timeInit);
        expectedSchedule.put("enrollmentEnd", timeInit);
        expectedSchedule.put("marketStart", timeInit);
        expectedSchedule.put("marketEnd", timeInit);

        assertThat(testObject.getSchedule(), is(expectedSchedule));
    }

    @Test
    public void initialStateIsMarketUnscheduled() {
        assertEquals(MarketState.UNSCHEDULED, testObject.getState(LocalDateTime.now()));
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

        assertEquals(MarketState.UNSCHEDULED, testObject.getState(LocalDateTime.now()));
        assertEquals(MarketState.ENROLLMENT_OPEN, testObject.getState(LocalDateTime.now().plusMinutes(5)));
        assertEquals(MarketState.ENROLLMENT_CLOSED, testObject.getState(LocalDateTime.now().plusMinutes(6)));
        assertEquals(MarketState.OPEN, testObject.getState(LocalDateTime.now().plusMinutes(7)));
        assertEquals(MarketState.CLOSED, testObject.getState(LocalDateTime.now().plusMinutes(12)));
    }

    @Test
    public void canManuallyOpenMarket() {
        LocalDateTime startTime = LocalDateTime.now();

        //create a new schedule first.
        testObject.scheduleEnrollment(startTime);
        //assert start and end time of enrollment
        Map<String, LocalDateTime> initSchedule = testObject.getSchedule();
        assertEquals(startTime, initSchedule.get("enrollmentStart"));
        assertEquals(startTime.plusMinutes(1), initSchedule.get("enrollmentEnd"));


        //now force an update of market open
        assertEquals(GameResponse.SCHEDULED, testObject.scheduleMarketOpen(startTime));

        Map<String, LocalDateTime> updatedSchedule = testObject.getSchedule();
        assertEquals(startTime, updatedSchedule.get("marketStart"));
        assertEquals(startTime.plusMinutes(5), testObject.getMarketEnd());

        //assert previous states have been unset
        assertEquals(timeInit, updatedSchedule.get("enrollmentStart"));
        assertEquals(timeInit, updatedSchedule.get("enrollmentEnd"));
    }

    @Test
    public void canManuallyCloseMarket() {
        LocalDateTime endTime = LocalDateTime.now();
        assertEquals(GameResponse.SCHEDULED, testObject.scheduleMarketClose(endTime));

        Map<String, LocalDateTime> updatedSchedule = testObject.getSchedule();

        assertEquals(timeInit, updatedSchedule.get("enrollmentStart"));
        assertEquals(timeInit, updatedSchedule.get("enrollmentEnd"));
        assertEquals(timeInit, updatedSchedule.get("marketStart"));
        assertEquals(endTime, updatedSchedule.get("marketEnd"));
    }
}