package pit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pit.bank.Bank;
import pit.config.GameSettings;
import pit.errors.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Game {
    private Set<Offer> offerSet = new HashSet<>();
    private Set<Bid> bids = new HashSet<>();
    private List<Trade> trades = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private final TradeValidation tradeValidation;
    private final Bank bank;
    private final Market market;
    private final Clock clock;
    private final Map<String, Player> playerMap = new HashMap<>();

    @Autowired
    public Game(Bank bank, TradeValidation tradeValidation, Market market, Clock clock) {
        this.bank = bank;
        this.tradeValidation = tradeValidation;
        this.market = market;
        this.clock = clock;

        playerMap.put("JAMES", new Player("JAMES"));
        playerMap.put("LUKE", new Player("LUKE"));
        playerMap.put("MASON", new Player("MASON"));
        playerMap.put("DANI", new Player("DANI"));
        playerMap.put("WILL", new Player("WILL"));
        playerMap.put("KIMI", new Player("KIMI"));
        playerMap.put("CHICO", new Player("CHICO"));
        playerMap.put("DEBBIE", new Player("DEBBIE"));
        playerMap.put("OWEN", new Player("OWEN"));
    }

    public LocalDateTime getClockTime() {
        return LocalDateTime.now(clock);
    }

    public GameMessage createPlayer(String username) {
        return GameResponse.CREATED;
    }

    public synchronized GameMessage connect(String name) {
        if (!market.getState(LocalDateTime.now(clock)).equals(MarketState.ENROLLMENT_OPEN)) {
            throw new MarketSchedule(MarketState.ENROLLMENT_CLOSED, ErrorMessages.ENROLLMENT_NOT_OPEN);
        } else {
            Player existingPlayer = playerMap.get(name.toUpperCase());
            playerMap.put(name.toUpperCase(), new Player(name, existingPlayer.getScore(), true));
            dealCards();
            return GameResponse.CONNECTED;
        }
    }

    public GameMessage join(Player player) {
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

    public synchronized GameMessage submitOffer(Offer offer) {
        isMarketClosed();
        tradeValidation.isValidOffer(offer);
        List<Offer> referenceList = new ArrayList<>(offerSet);
        referenceList.stream().filter(o -> offer.getPlayer().equals(o.getPlayer())).findAny().map(o -> offerSet.remove(o));
        offerSet.add(offer);
        return GameResponse.ACCEPTED;
    }

    public synchronized GameMessage removeOffer(Offer offer) {
        isMarketClosed();

        offerSet.remove(offer);
        return GameResponse.REMOVED;
    }

    public synchronized GameMessage submitBid(Bid bid) {
        isMarketClosed();
        tradeValidation.isValidBid(bid);
        bids.add(bid);
        return GameResponse.ACCEPTED;
    }

    public synchronized GameMessage removeBid(Bid bid) {
        isMarketClosed();
        bids.remove(bid);
        return GameResponse.REMOVED;
    }

    public synchronized GameMessage acceptBid(BidView bidView, Commodity commodity) {
        isMarketClosed();
        Bid bid = getBidFromView(bidView);
        boolean ownerSatisfiesTrade = tradeValidation.playerCanSatisfyTrade(bidView.getOwner(), bidView.getAmount(), commodity);
        boolean requesterSatisfiesTrade = tradeValidation.playerCanSatisfyTrade(bidView.getRequester(), bidView.getAmount(), bid.getCommodity());
        if (ownerSatisfiesTrade && requesterSatisfiesTrade) {
            List<Offer> referenceList = new ArrayList<>(offerSet);
            for (Offer o : referenceList) {
                if (bidView.getRequester().getName().equals(o.getPlayer().getName()) || bidView.getOwner().getName().equals(o.getPlayer().getName())) {
                    offerSet.remove(o);
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

    public synchronized GameMessage cornerMarket(Player player, Commodity commodity) {
        if (bank.getHoldings().get(player).get(commodity).equals(GameSettings.TOTAL_PER_COMMODITY)){
            Player existingPlayer = playerMap.get(player.getName());
            playerMap.put(player.getName(), new Player(player.getName(), Integer.sum(existingPlayer.getScore(), 1), true));
            offerSet = new HashSet<>();
            bids = new HashSet<>();
            trades = new ArrayList<>();
            dealCards();
            return GameResponse.ACCEPTED;
        }
        return GameResponse.REJECTED;
    }

    public synchronized Set<Offer> getOffers() {
        return offerSet;
    }

    synchronized Set<Bid> getBids() {
        return bids;
    }

    public synchronized List<BidView> getBidViews() {
        return bids.stream().map(Bid::getView).collect(Collectors.toList());
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getPlayerMapAsList(){
        return new ArrayList<>(playerMap.values());
    }

    public Market getMarket() {
        return market;
    }

    public Bank getBank() {
        return bank;
    }

    private void isMarketClosed() {
        if(!market.getState(LocalDateTime.now(clock)).equals(MarketState.OPEN)){
            throw new MarketSchedule(MarketState.CLOSED, ErrorMessages.MARKET_NOT_OPEN);
        }
    }

    public void disconnectPlayers() {
        playerMap.values().forEach(player -> player.setConnected(false));
    }

    private void dealCards() {
        offerSet.clear();
        bids.clear();
        trades.clear();
        bank.initializeHoldings(playerMap.values().stream().filter(Player::isConnected).collect(Collectors.toList()));
    }
}