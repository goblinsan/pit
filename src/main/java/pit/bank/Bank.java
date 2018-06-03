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
                Integer total = selectedCommodities.stream().filter(c -> c.equals(distinctSelection)).collect(Collectors.toList()).size();
                portfolio.put(distinctSelection, total);
            }
            holdings.put(player, portfolio);
        }
    }

    public void updateHoldings(Bid bid, Commodity commodity) {
        EnumMap<Commodity, Integer> requesterHolding = holdings.get(bid.getRequester());
        EnumMap<Commodity, Integer> ownerHolding = holdings.get(bid.getOwner());

        Integer updatedRequesterInboundValue = bid.getAmount();
        if (requesterHolding.containsKey(commodity)) {
            updatedRequesterInboundValue = requesterHolding.get(commodity) + bid.getAmount();
        }
        Integer updatedRequesterOutboundValue = requesterHolding.get(bid.getCommodity()) - bid.getAmount();

        requesterHolding.put(commodity, updatedRequesterInboundValue);
        requesterHolding.put(bid.getCommodity(), updatedRequesterOutboundValue);
        holdings.put(bid.getRequester(), requesterHolding);

        Integer updatedOwnerInboundValue = bid.getAmount();
        if (ownerHolding.containsKey(bid.getCommodity())) {
            updatedOwnerInboundValue = ownerHolding.get(bid.getCommodity()) + bid.getAmount();
        }
        Integer updatedOwnerOutboundValue = ownerHolding.get(commodity) - bid.getAmount();

        ownerHolding.put(commodity, updatedOwnerOutboundValue);
        ownerHolding.put(bid.getCommodity(), updatedOwnerInboundValue);
        holdings.put(bid.getOwner(), ownerHolding);
    }

    public Map<Player, EnumMap<Commodity, Integer>> getHoldings() {
        return holdings;
    }

    EnumMap<Commodity, Integer> getPortfolio(Player player1) {
        return holdings.get(player1);
    }
}
