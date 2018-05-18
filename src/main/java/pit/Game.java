package pit;

import lombok.EqualsAndHashCode;
import pit.bank.Bank;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;
import pit.errors.GameError;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
class Game {

    private List<Offer> offerList = new ArrayList<>();
    private List<Bid> bids = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private TradeValidation tradeValidation;
    private Bank bank;

    Game(Bank bank, TradeValidation tradeValidation) {
        this.bank = bank;
        this.tradeValidation = tradeValidation;
    }

    Player createPlayer(String username) {
        return new Player(username);
    }

    GameResponse join(Player player) {
        if (players.contains(player)) {
            throw new GameError(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_JOIN_MORE_THAN_ONCE);
        } else {
            players.add(player);
            return GameResponse.JOINED;
        }
    }

    GameResponse submitOffer(Offer offer) {
        tradeValidation.isValidOffer(offer);
        List<Offer> referenceList = new ArrayList<>(offerList);
        referenceList.stream().filter(o -> offer.getPlayer().equals(o.getPlayer())).findAny().map(o -> offerList.remove(o));
        offerList.add(offer);
        return GameResponse.ACCEPTED;
    }

    GameResponse removeOffer(Offer offer) {
        offerList.remove(offer);
        return GameResponse.REMOVED;
    }

    GameResponse submitBid(Bid bid) {
        tradeValidation.isValidBid(bid);
        bids.add(bid);
        return GameResponse.ACCEPTED;
    }

    GameResponse removeBid(Bid bid) {
        bids.remove(bid);
        return GameResponse.REMOVED;
    }

    GameResponse acceptBid(Bid bid, Commodity commodity) {
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
            return GameResponse.ACCEPTED;
        }

        throw new BidOutOfBounds(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_SATISFY_BID);
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

}