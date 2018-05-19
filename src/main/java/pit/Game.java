package pit;

import pit.bank.Bank;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;
import pit.errors.GameError;
import pit.errors.MarketSchedule;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Game {

    private List<Offer> offerList = new ArrayList<>();
    private List<Bid> bids = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private TradeValidation tradeValidation;
    private Bank bank;
    private Market market;
    private Clock clock;

    Game(Bank bank, TradeValidation tradeValidation, Market market, Clock clock) {
        this.bank = bank;
        this.tradeValidation = tradeValidation;
        this.market = market;
        this.clock = clock;
    }

    Player createPlayer(String username) {
        return new Player(username);
    }

    GameErrors join(Player player) {
        if (players.contains(player)) {
            throw new GameError(GameErrors.INVALID, ErrorMessages.PLAYER_CANNOT_JOIN_MORE_THAN_ONCE);
        } else if (!market.getState(LocalDateTime.now(clock)).equals(MarketState.ENROLLMENT_OPEN)) {
            throw new MarketSchedule(MarketState.ENROLLMENT_CLOSED, ErrorMessages.ENROLLMENT_NOT_OPEN);
        } else {
            players.add(player);
            return GameErrors.JOINED;
        }
    }

    GameErrors submitOffer(Offer offer) {
        isMarketClosed();
        tradeValidation.isValidOffer(offer);
        List<Offer> referenceList = new ArrayList<>(offerList);
        referenceList.stream().filter(o -> offer.getPlayer().equals(o.getPlayer())).findAny().map(o -> offerList.remove(o));
        offerList.add(offer);
        return GameErrors.ACCEPTED;
    }

    GameErrors removeOffer(Offer offer) {
        isMarketClosed();

        offerList.remove(offer);
        return GameErrors.REMOVED;
    }

    GameErrors submitBid(Bid bid) {
        isMarketClosed();
        tradeValidation.isValidBid(bid);
        bids.add(bid);
        return GameErrors.ACCEPTED;
    }

    GameErrors removeBid(Bid bid) {
        isMarketClosed();
        bids.remove(bid);
        return GameErrors.REMOVED;
    }

    GameErrors acceptBid(Bid bid, Commodity commodity) {
        isMarketClosed();
        if (tradeValidation.playerCanSatisfyTrade(bid.getOwner(), bid.getAmount(), commodity)) {
            List<Offer> referenceList = new ArrayList<>(offerList);
            for (Offer o : referenceList) {
                if (bid.getRequester().getName().equals(o.getPlayer().getName()) || bid.getOwner().getName().equals(o.getPlayer().getName())) {
                    offerList.remove(o);
                }
                bids.remove(bid);
            }
            bank.updateHoldings(bid, commodity);
            trades.add(new Trade(bid.getRequester(), bid.getOwner(), bid.getAmount()));
            return GameErrors.ACCEPTED;
        }

        throw new BidOutOfBounds(GameErrors.INVALID, ErrorMessages.PLAYER_CANNOT_SATISFY_BID);
    }

    List<Offer> getOffers() {
        return offerList;
    }

    List<Bid> getBids() {
        return bids;
    }

    List<Trade> getTrades() {
        return trades;
    }

    List<Player> getPlayers() {
        return players;
    }

    private void isMarketClosed() {
        if(!market.getState(LocalDateTime.now(clock)).equals(MarketState.OPEN)){
            throw new MarketSchedule(MarketState.CLOSED, ErrorMessages.MARKET_NOT_OPEN);
        }
    }

}