package pit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import pit.bank.Bank;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        GameErrors actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameErrors.ACCEPTED, actualResponse);
        assertEquals(1, testObject.getBids().size());
        assertEquals(requester.getName(), testObject.getBids().get(0).getRequester().getName());
        assertEquals(2, testObject.getBids().get(0).getAmount());
    }

    @Test
    public void playerCanRemoveTheirOwnBid() {
        testObject.submitBid(goodBid);
        assertEquals(1, testObject.getBids().size());

        GameErrors actualResponse = testObject.removeBid(goodBid);
        assertEquals(GameErrors.REMOVED, actualResponse);
        assertEquals(0, testObject.getBids().size());
    }

    @Test
    public void bidsShouldContainAllTheSameCommodity() {
        // API does not allow otherwise - this should be tested at the service level.

        GameErrors actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameErrors.ACCEPTED, actualResponse);
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
        GameErrors actualResponse = testObject.acceptBid(goodBid, Commodity.CATTLE);
        assertEquals(GameErrors.ACCEPTED, actualResponse);
    }

    @Test
    public void playerNeedsToOwnAmountOfCommodityInBidBeforeAccepting() {
        Bid badBidTooManyRequested = new Bid(requester, owner, 4, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_BID);

        testObject.acceptBid(badBidTooManyRequested, Commodity.CATTLE);
    }

    @Test
    public void playerCannotActionBidsIfMarketIsClosed() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.CLOSED);
        try {
            testObject.submitBid(goodBid);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
            return;
        }
        try {
            testObject.removeBid(goodBid);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
            return;
        }
        try {
            testObject.acceptBid(goodBid, Commodity.GOLD);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
            return;
        }
        fail();
    }
}