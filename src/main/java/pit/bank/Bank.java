package pit.bank;

import org.springframework.stereotype.Component;
import pit.Bid;
import pit.Commodity;
import pit.Player;
import pit.config.GameSettings;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class Bank {
    Map<Player, EnumMap<Commodity, Integer>> holdings = new HashMap<>();

    public void initializeHoldings(List<Player> players) {
        holdings = new HashMap<>();
        EnumMap<Commodity, Integer> availableCommodities = new EnumMap<>(Commodity.class);
        for (int i = 0; i < players.size(); i++) {
            availableCommodities.put(Commodity.values()[i],GameSettings.TOTAL_PER_COMMODITY);
        }

        for (Player player : players) {
            EnumMap<Commodity, Integer> portfolio = new EnumMap<>(Commodity.class);
            availableCommodities.keySet().forEach(commodity -> portfolio.put(commodity, 0));
            List<Commodity> selectedCommodities = new ArrayList<>();
            for (int i = 0; i < GameSettings.TOTAL_PER_COMMODITY; i++) {
                boolean selected = false;
                while (!selected){
                    int randomNum = ThreadLocalRandom.current().nextInt(0, players.size());
                    Integer numRemaining = availableCommodities.get(Commodity.values()[randomNum]);
                    if (numRemaining > 0) {
                        selectedCommodities.add(Commodity.values()[randomNum]);
                        availableCommodities.put(Commodity.values()[randomNum], numRemaining - 1);
                        selected = true;
                    }
                }
            }
            List<Commodity> distinctSelections = selectedCommodities.stream().distinct().collect(Collectors.toList());
            for (Commodity distinctSelection : distinctSelections) {
                Integer total = (int) selectedCommodities.stream().filter(c -> c.equals(distinctSelection)).count();
                portfolio.put(distinctSelection, total);
            }
            holdings.put(player, portfolio);
        }
    }

    public void updateHoldings(Bid bid, Commodity offerCommodity) {
        addTraderHoldings(bid.getRequester(), offerCommodity, bid.getAmount());
        addTraderHoldings(bid.getOwner(), bid.getCommodity(), bid.getAmount());
        subtractTraderHoldings(bid.getRequester(), bid.getCommodity(), bid.getAmount());
        subtractTraderHoldings(bid.getOwner(), offerCommodity, bid.getAmount());
    }

    private void addTraderHoldings(Player player, Commodity commodity, int amount) {
        int total = holdings.get(player).get(commodity) + amount;
        holdings.get(player).put(commodity, total);
    }

    private void subtractTraderHoldings(Player player, Commodity commodity, int amount) {
        int total = holdings.get(player).get(commodity) - amount;
        holdings.get(player).put(commodity, total);
    }

    public Map<Player, EnumMap<Commodity, Integer>> getHoldings() {
        return holdings;
    }

    EnumMap<Commodity, Integer> getPortfolio(Player player1) {
        return holdings.get(player1);
    }
}
