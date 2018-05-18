package pit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import pit.bank.Bank;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BidTest {

    private Game testObject;
    private Player requester = new Player("player 1");
    private Player owner = new Player("player 2");

    @Rule public ExpectedException thrown =  ExpectedException.none();
    private Bid goodBid;
    private Map<Player, EnumMap<Commodity, Integer>> mockHoldings;
    private Bank mockBank;



    @Before
    public void setUp() {
        goodBid = new Bid(requester,owner,2, Commodity.GOLD);
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
        TradeValidation tradeValidation = new TradeValidation(mockBank);
        testObject = new Game(mockBank, tradeValidation);
    }

    @Test
    public void submitAndGetBid() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        GameResponse actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameResponse.ACCEPTED, actualResponse);
        assertEquals(1, testObject.getBids().size());
        assertEquals(requester.getName(), testObject.getBids().get(0).getRequester().getName());
        assertEquals(2, testObject.getBids().get(0).getAmount());
    }

    @Test
    public void playerCanRemoveTheirOwnBid() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        testObject.submitBid(goodBid);
        assertEquals(1, testObject.getBids().size());

        GameResponse actualResponse = testObject.removeBid(goodBid);
        assertEquals(GameResponse.REMOVED, actualResponse);
        assertEquals(0, testObject.getBids().size());
    }

    @Test
    public void bidsShouldContainAllTheSameCommodity() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        // API does not allow otherwise - this should be tested at the service level.

        GameResponse actualResponse = testObject.submitBid(goodBid);

        assertEquals(GameResponse.ACCEPTED, actualResponse);
    }

    @Test
    public void bidShouldBeGreaterThanZero() {
        Bid badBid = new Bid(requester,owner,-1, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.BID_LESS_THAN_ZERO);

        testObject.submitBid(badBid);
    }

    @Test
    public void playerNeedsToOwnAmountInBid() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        Bid badBid = new Bid(requester,owner,20, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_BID);

        testObject.submitBid(badBid);
    }

    @Test
    public void ownerCanAcceptBid() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        GameResponse actualResponse = testObject.acceptBid(goodBid, Commodity.CATTLE);
        assertEquals(GameResponse.ACCEPTED, actualResponse);
    }

    @Test
    public void playerNeedsToOwnAmountOfCommodityInBidBeforeAccepting() {
        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        Bid badBidTooManyRequested = new Bid(requester,owner,4, Commodity.GOLD);

        thrown.expect(BidOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_BID);

        testObject.acceptBid(badBidTooManyRequested, Commodity.CATTLE);
    }
}