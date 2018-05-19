package pit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import pit.bank.Bank;
import pit.config.GameSettings;
import pit.errors.ErrorMessages;
import pit.errors.GameError;
import pit.errors.MarketSchedule;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GameTest {

    private Game testObject;
    private Player player1 = new Player("player 1");

    private Bank mockBank;
    private TradeValidation mockTradeValidation;
    private Market mockMarket;
    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        mockBank = Mockito.mock(Bank.class);
        mockTradeValidation = Mockito.mock(TradeValidation.class);
        mockMarket = Mockito.mock(Market.class);
        testObject = new Game(mockBank, mockTradeValidation, mockMarket, clock);
    }

    @Test
    public void canCreatePlayer() {
        String username = "username";
        Player expectedPlayer = new Player(username);
        Player actualPlayer = testObject.createPlayer(username);

        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    public void canPlayerCanJoin() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.ENROLLMENT_OPEN);
        GameResponse expectedResponse = GameResponse.JOINED;
        GameMessage actualResponse = testObject.join(player1);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(1, testObject.getPlayers().size());
        assertThat(player1, is(testObject.getPlayers().get(0)));
    }

    @Test
    public void playerCantJoinTwice() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.ENROLLMENT_OPEN);
        testObject.join(player1);
        thrown.expect(GameError.class);
        thrown.expectMessage(ErrorMessages.PLAYER_CANNOT_JOIN_MORE_THAN_ONCE);
        testObject.join(player1);
    }

    @Test
    public void playerCantJoinIfEnrollmentIsClosed() {
        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.CLOSED);
        try {
            testObject.join(player1);
        } catch (MarketSchedule e) {
            assertEquals(MarketState.ENROLLMENT_CLOSED, e.getStatus());
            assertEquals(ErrorMessages.ENROLLMENT_NOT_OPEN, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void acceptBidUpdatesGameState() {
        /*
            When the bid is accepted, the system should update the game state:
            - Remove outstanding offers for each player
            - Remove bids
            - Call Bank to update player holdings
            - Add the executed trade to the list
         */

        Mockito.when(mockMarket.getState(Mockito.any(LocalDateTime.class))).thenReturn(MarketState.OPEN);
        Mockito.when(mockTradeValidation.isValidOffer(Mockito.any(Offer.class))).thenReturn(true);
        Mockito.when(mockTradeValidation.isValidBid(Mockito.any(Bid.class))).thenReturn(true);
        Mockito.when(mockTradeValidation.playerCanSatisfyTrade(Mockito.any(Player.class),Mockito.any(Integer.class),Mockito.any(Commodity.class))).thenReturn(true);

        Player player2 = new Player("player 2");

        Offer offer = new Offer(player1, 3);
        Offer offer2 = new Offer(player2, 2);

        Bid bid = new Bid(player1, player2, 2, Commodity.OIL);

        testObject.submitOffer(offer);
        testObject.submitOffer(offer2);
        testObject.submitBid(bid);

        // assert pre-trade state
        assertEquals(2, testObject.getOffers().size());
        assertEquals(1, testObject.getBids().size());
        assertEquals(0, testObject.getTrades().size());

        testObject.acceptBid(bid, Commodity.GOLD);

        // assert post-trade state
        assertEquals(0, testObject.getOffers().size());
        assertEquals(0, testObject.getBids().size());

        Trade expectedTrade = new Trade(player1, player2, bid.getAmount());
        List<Trade> expectedTrades = Collections.singletonList(expectedTrade);

        List<Trade> actualTrades = testObject.getTrades();
        assertEquals(1, actualTrades.size());
        assertThat(expectedTrades, is(actualTrades));
    }

    @Test
    public void playerWins() {
        EnumMap<Commodity, Integer> player1Holding = new EnumMap<>(Commodity.class);
        player1Holding.put(Commodity.GOLD, GameSettings.TOTAL_PER_COMMODITY);
        Map<Player, EnumMap<Commodity, Integer>> holdings = new HashMap<>();
        holdings.put(player1, player1Holding);
        Mockito.when(mockBank.getHoldings()).thenReturn(holdings);
        GameMessage actualMessage = testObject.cornerMarket(player1, Commodity.GOLD);

        assertEquals(GameResponse.ACCEPTED, actualMessage);
    }

    @Test
    public void playerDoesNotWinWithoutHoldingAll() {
        EnumMap<Commodity, Integer> player1Holding = new EnumMap<>(Commodity.class);
        player1Holding.put(Commodity.GOLD, 0);
        Map<Player, EnumMap<Commodity, Integer>> holdings = new HashMap<>();
        holdings.put(player1, player1Holding);
        Mockito.when(mockBank.getHoldings()).thenReturn(holdings);
        GameMessage actualMessage = testObject.cornerMarket(player1, Commodity.GOLD);

        assertEquals(GameResponse.REJECTED, actualMessage);
    }
}