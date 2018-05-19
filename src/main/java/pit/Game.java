package pit;

import pit.bank.Bank;
import pit.config.GameSettings;
import pit.errors.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    LocalDateTime getClockTime() {
        return LocalDateTime.now(clock);
    }

    GameMessage createPlayer(String username) {
        return GameResponse.CREATED;
    }

    GameMessage join(Player player) {
        if (players.contains(player)) {
            throw new GameError(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_JOIN_MORE_THAN_ONCE);
        } else if (!market.getState(LocalDateTime.now(clock)).equals(MarketState.ENROLLMENT_OPEN)) {
            throw new MarketSchedule(MarketState.ENROLLMENT_CLOSED, ErrorMessages.ENROLLMENT_NOT_OPEN);
        } else {
            players.add(player);
            bank.initializeHoldings(players);
            return GameResponse.JOINED;
        }
    }

    GameMessage submitOffer(Offer offer) {
        isMarketClosed();
        tradeValidation.isValidOffer(offer);
        List<Offer> referenceList = new ArrayList<>(offerList);
        referenceList.stream().filter(o -> offer.getPlayer().equals(o.getPlayer())).findAny().map(o -> offerList.remove(o));
        offerList.add(offer);
        return GameResponse.ACCEPTED;
    }

    GameMessage removeOffer(Offer offer) {
        isMarketClosed();

        offerList.remove(offer);
        return GameResponse.REMOVED;
    }

    GameMessage submitBid(Bid bid) {
        isMarketClosed();
        tradeValidation.isValidBid(bid);
        bids.add(bid);
        return GameResponse.ACCEPTED;
    }

    GameMessage removeBid(Bid bid) {
        isMarketClosed();
        bids.remove(bid);
        return GameResponse.REMOVED;
    }

    GameMessage acceptBid(BidView bidView, Commodity commodity) {
        isMarketClosed();
        Bid bid = getBidFromView(bidView);
        if (tradeValidation.playerCanSatisfyTrade(bidView.getOwner(), bidView.getAmount(), commodity)) {
            List<Offer> referenceList = new ArrayList<>(offerList);
            for (Offer o : referenceList) {
                if (bidView.getRequester().getName().equals(o.getPlayer().getName()) || bidView.getOwner().getName().equals(o.getPlayer().getName())) {
                    offerList.remove(o);
                }
                bids.remove(bid);
            }
            bank.updateHoldings(bid, commodity);
            trades.add(new Trade(bidView.getRequester(), bidView.getOwner(), bidView.getAmount()));
            return GameResponse.ACCEPTED;
        }

        throw new BidOutOfBounds(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_SATISFY_BID);
    }

    Bid getBidFromView(BidView bidView) {
        return getBids().stream()
                .filter(b -> isViewEqualBid(bidView, b))
                .findFirst().orElseThrow(() -> new BidNotFound(GameResponse.INVALID, ErrorMessages.BID_NOT_FOUND));
    }

    private boolean isViewEqualBid(BidView bidView, Bid bid) {
        return (bid.getRequester().equals(bidView.getRequester())
                && bid.getOwner().equals(bidView.getOwner())
                && bid.getAmount() == bidView.getAmount()
        );
    }

    GameMessage cornerMarket(Player player, Commodity commodity) {
        if (bank.getHoldings().get(player).get(commodity).equals(GameSettings.TOTAL_PER_COMMODITY)){
            return GameResponse.ACCEPTED;
        }
        return GameResponse.REJECTED;
    }

    List<Offer> getOffers() {
        return offerList;
    }

    List<Bid> getBids() {
        return bids;
    }

    List<BidView> getBidViews() {
        return bids.stream().map(Bid::getView).collect(Collectors.toList());
    }

    List<Trade> getTrades() {
        return trades;
    }

    List<Player> getPlayers() {
        return players;
    }

    Market getMarket() {
        return market;
    }

    Bank getBank() {
        return bank;
    }

    private void isMarketClosed() {
        if(!market.getState(LocalDateTime.now(clock)).equals(MarketState.OPEN)){
            throw new MarketSchedule(MarketState.CLOSED, ErrorMessages.MARKET_NOT_OPEN);
        }
    }
}