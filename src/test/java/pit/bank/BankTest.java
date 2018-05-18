package pit.bank;

import org.junit.Test;
import pit.Bid;
import pit.Commodity;
import pit.Player;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BankTest {
    private Player player1 = new Player("player 1");
    private Player player2 = new Player("player 2");

    @Test
    public void gameAssignsInitialHoldings() {
        //Total commodities per player must equal 9
        //Total type of commodities assigned must equal number of players

        Bank testObject = new Bank();

        Player player3 = new Player("player 3");
        Player player4 = new Player("player 4");
        Player player5 = new Player("player 5");

        List<Player> players = Arrays.asList(player1,player2,player3,player4,player5);
        testObject.initializeHoldings(players);

        Map<Player, EnumMap<Commodity, Integer>> actualHoldings = testObject.getHoldings();
        EnumMap<Commodity, Integer> totalCommodityCount = new EnumMap<>(Commodity.class);
        Arrays.stream(Commodity.values()).forEach(commodity -> totalCommodityCount.put(commodity, 0));
        actualHoldings.forEach((player, playerHoldings) -> {
            assertEquals(9, playerHoldings.values().stream().mapToInt(Integer::intValue).sum());
            assertEquals(playerHoldings.size(), players.size());
            playerHoldings.forEach((c,i)-> totalCommodityCount.put(c, totalCommodityCount.get(c)+i));
        });
        // Check that each distributed commodity count equals 9
        totalCommodityCount.values().stream().filter(i -> i > 0).forEach(i -> assertEquals(9, (int) i));
    }

    @Test
    public void acceptBidUpdatesGameState() {
        /*
            -- Beginning holdings:
            --- player 1 : 5 gold
            --- player 2 : 4 oil, 0 Gold

            -- Trade (Player 1 bids the 2 commodities that player 2 is offering)
            --- p1 -> p2 : 2 Gold
            --- p2 -> p1 : 2 Oil

            -- Resulting holdings:
            --- player 1 : 3 Gold, 2 Oil
            --- player 2 : 2 Oil, 2 Gold

         */

        Bid bid = new Bid(player1, player2, 2, Commodity.GOLD);
        EnumMap<Commodity, Integer> player1Holding = new EnumMap<>(Commodity.class);
        player1Holding.put(Commodity.GOLD, 5);
        player1Holding.put(Commodity.OIL, 0);
        EnumMap<Commodity, Integer> player2Holding = new EnumMap<>(Commodity.class);
        player2Holding.put(Commodity.OIL, 4);
        player2Holding.put(Commodity.GOLD, 0);
        Map<Player, EnumMap<Commodity, Integer>> holdings = new HashMap<>();
        holdings.put(player1, player1Holding);
        holdings.put(player2, player2Holding);

        holdings.put(player1, player1Holding);
        holdings.put(player2, player2Holding);

        Bank testObject = new Bank();
        testObject.holdings = holdings;

        testObject.updateHoldings(bid, Commodity.OIL);

        // Assert end state of holdings
        //player one post
        assertEquals(3, testObject.getHoldings().get(player1).get(Commodity.GOLD).intValue());
        assertEquals(2, testObject.getHoldings().get(player1).get(Commodity.OIL).intValue());
        //player 2 post
        assertEquals(2, testObject.getHoldings().get(player2).get(Commodity.OIL).intValue());
        assertEquals(2, testObject.getHoldings().get(player2).get(Commodity.GOLD).intValue());

    }
}