package pit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import pit.bank.Bank;
import pit.errors.ErrorMessages;
import pit.errors.MarketSchedule;
import pit.errors.OfferOutOfBounds;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;

public class OfferTest {

    private Game testObject;
    private Player player1 = new Player("player 1");
    private Player player2 = new Player("player 2");
    private List<Offer> expectedOffers = new ArrayList<>();
    private Offer expectedOffer;
    private Offer expectedOffer2;
    private Map<Player, EnumMap<Commodity, Integer>> mockHoldings;
    private Bank mockBank;
    private Market mockMarket;
    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());


    @Before
    public void setUp() {
        expectedOffer = new Offer(player1, 3);
        expectedOffer2 = new Offer(player2, 4);
        expectedOffers.add(expectedOffer);
        expectedOffers.add(expectedOffer2);
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
        mockHoldings.put(player1, player1Holding);
        mockHoldings.put(player2, player2Holding);

        mockBank = Mockito.mock(Bank.class);
        mockMarket = Mockito.mock(Market.class);
        TradeValidation tradeValidation = new TradeValidation(mockBank);
        testObject = new Game(mockBank, tradeValidation, mockMarket, clock);

        Mockito.when(mockBank.getHoldings()).thenReturn(mockHoldings);
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.OPEN);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getOffers() {
        testObject.submitOffer(expectedOffer);
        testObject.submitOffer(expectedOffer2);

        assertNotNull(testObject.getOffers());
        assertEquals(expectedOffers.get(0).getPlayer(), testObject.getOffers().get(0).getPlayer());
        assertEquals(expectedOffers.get(0).getAmount(), testObject.getOffers().get(0).getAmount());
        assertEquals(expectedOffers.get(1).getPlayer(), testObject.getOffers().get(1).getPlayer());
        assertEquals(expectedOffers.get(1).getAmount(), testObject.getOffers().get(1).getAmount());
    }

    @Test
    public void offerShouldBeGreaterThanZero() {
        Offer badOffer = new Offer(player1, -1);

        thrown.expect(OfferOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.OFFER_LESS_THAN_ZERO);

        testObject.submitOffer(badOffer);
    }

    @Test
    public void playerNeedsToOwnAmountInOffer() {
        Offer badOffer = new Offer(player1, 20);

        thrown.expect(OfferOutOfBounds.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_SATISFY_OFFER);

        testObject.submitOffer(badOffer);
    }

    @Test
    public void playerCanOnlyHaveOneOffer() {
        testObject.submitOffer(expectedOffer);
        testObject.submitOffer(expectedOffer2);

        expectedOffers.remove(0);
        expectedOffer = new Offer(player1, 1);
        expectedOffers.add(expectedOffer);

        GameErrors actualResponse = testObject.submitOffer(expectedOffer);

        assertEquals(GameErrors.ACCEPTED, actualResponse);
        Offer emptyOffer = new Offer(new Player("empty"),0);
        Offer actualOffer = testObject.getOffers().stream().filter(o -> o.getPlayer().equals(player1)).findFirst().orElse(emptyOffer);
        assertEquals(expectedOffers.get(1).getPlayer().getName(), actualOffer.getPlayer().getName());
        assertEquals(expectedOffers.get(1).getAmount(), actualOffer.getAmount());
    }

    @Test
    public void playerCanRemoveTheirOwnOffer() {
        testObject.submitOffer(expectedOffer);
        assertEquals(expectedOffers.get(0).getPlayer(), testObject.getOffers().get(0).getPlayer());
        assertEquals(expectedOffers.get(0).getAmount(), testObject.getOffers().get(0).getAmount());

        GameErrors actualResponse = testObject.removeOffer(expectedOffer);
        assertEquals(GameErrors.REMOVED, actualResponse);
        assertEquals(0, testObject.getOffers().size());
    }

    @Test
    public void playerCannotActionOffersIfMarketIsClosed() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.CLOSED);
        try {
            testObject.submitOffer(expectedOffer);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
        }
        try {
            testObject.removeOffer(expectedOffer);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.CLOSED, e.getStatus());
            assertEquals(ErrorMessages.MARKET_NOT_OPEN, e.getMessage());
            return;
        }
        fail();
    }

}