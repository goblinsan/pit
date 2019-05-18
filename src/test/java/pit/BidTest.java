package pit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import pit.bank.Bank;
import pit.errors.BidNotFound;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BidTest {

    private Game testObject;
    private Player requester = new Player("player 1");
    private Player owner = new Player("player 2");
    private Bid goodBid;
    private Map<Player, EnumMap<Commodity, Integer>> mockHoldings;
    private Bank mockBank;
    private Market mockMarket;
    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        goodBid = new Bid(requester, owner, 2, Commodity.GOLD);
        EnumMap<Commodity, Integer> player1Holding = new EnumMap<>(Commodity.class);
        player1Holding.put(Commodity.CATTLE, 4);
        player1Holding.put(Commodity.GOLD, 5);
        player1Holding.put(Commodity.OIL, 0);
        EnumMap<Commodity, Integer> player2Holding = new EnumMap<>(Commodity.class);
        player2Holding.put(Commodity.OIL, 4);
        player2Holding.put(Commodity.CATTLE, 2);
        player2Holding.put(Commodity.COCOA, 3);
        player2Holding.put(Commodity.GOLD, 0);
        mockHoldings = new HashMap<>();
        mockHoldings.put(requester, player1Holding);
        mockHoldings.put(owner, player2Holding);

        mockBank = Mockito.mock(Bank.class);
        mockMarket = Mockito.mock(Market.class);
        TradeValidation tradeValidation = new TradeValidation(mockBank);
        testObject = new Game(mockBank, tradeValidation, mockMarket, clock);
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.OPEN);
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
    }

    @Test
    public void submitAndGetBid() {
        GameMessage actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameResponse.ACCEPTED, actualResponse);
        assertEquals(1, testObject.getBids().size());
        assertTrue(testObject.getBids().contains(goodBid));
        assertEquals(1, testObject.getBids().size());
    }

    @Test
    public void provideViewOfBidsWithoutCommodity() {
        List<BidView> expectedList = Collections.singletonList(goodBid.getView());
        testObject.submitBid(goodBid);

        assertThat(testObject.getBidViews(), is(expectedList));
    }

    @Test
    public void canGetBidFromView() {
        testObject.submitBid(goodBid);
        assertEquals(goodBid, testObject.getBidFromView(goodBid.getView()));
    }

    @Test
    public void throwErrorWhenBidNotFound() {
        try {
            testObject.acceptBid(goodBid.getView(), Commodity.GOLD);
        } catch (BidNotFound e) {
            assertEquals(GameResponse.INVALID, e.getStatus());
            assertEquals(ErrorMessages.BID_NOT_FOUND, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void playerCanRemoveTheirOwnBid() {
        testObject.submitBid(goodBid);
        assertEquals(1, testObject.getBids().size());

        GameMessage actualResponse = testObject.removeBid(goodBid);
        assertEquals(GameResponse.REMOVED, actualResponse);
        assertEquals(0, testObject.getBids().size());
    }

    @Test
    public void bidsShouldContainAllTheSameCommodity() {
        // API does not allow otherwise - this should be tested at the service level.

        GameMessage actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameResponse.ACCEPTED, actualResponse);
    }

    @Test
    public void bidShouldBeGreaterThanZero() {
        Bid badBid = new Bid(requester, owner, -1, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.BID_LESS_THAN_ZERO);

        testObject.submitBid(badBid);
    }

    @Test
    public void playerNeedsToOwnAmountInBid() {
        Bid badBid = new Bid(requester, owner, 20, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_BID);

        testObject.submitBid(badBid);
    }

    @Test
    public void ownerCanAcceptBid() {
        testObject.submitBid(goodBid);
        GameMessage actualResponse = testObject.acceptBid(goodBid.getView(), Commodity.CATTLE);
        assertEquals(GameResponse.ACCEPTED, actualResponse);
    }

    @Test
    public void playerNeedsToOwnAmountOfCommodityInBidBeforeAccepting() {
        Bid badBidTooManyRequested = new Bid(requester, owner, 4, Commodity.GOLD);
        testObject.submitBid(badBidTooManyRequested);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_BID);

        testObject.acceptBid(badBidTooManyRequested.getView(), Commodity.CATTLE);
    }

    @Test
    public void playerCannotActionBidsIfMarketIsClosed() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.CLOSED);
        try {
            testObject.submitBid(goodBid);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
        }
        try {
            testObject.removeBid(goodBid);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
        }
        try {
            testObject.acceptBid(goodBid.getView(), Commodity.GOLD);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
            return;
        }
        fail();
    }
}