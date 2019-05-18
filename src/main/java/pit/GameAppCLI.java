package pit;


import asg.cliche.Command;
import asg.cliche.ShellFactory;
import pit.bank.Bank;
import pit.errors.GameError;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameAppCLI {

    private static Bank bank = new Bank();
    private static TradeValidation tradeValidation;
    private static Market market = new Market();
    private static Clock gameClock = Clock.systemDefaultZone();
    private static Game game;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Command
    public String createPlayer(String name) {
        return game.createPlayer(name).toString();
    }

    @Command
    public String join(String name) {
        try {
            return game.join(new Player(name)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String start() {
        try {
            return game.getMarket().scheduleEnrollment(LocalDateTime.now(gameClock)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String hand(String name) {
        try {
            Player player = new Player(name);
            return game.getBank().getHoldings().get(player).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String time() {
        return game.getClockTime().toLocalTime().format(formatter);
    }

    @Command
    public String schedule() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();

        return "Enrollment Opens : "+ schedule.get("enrollmentStart").toLocalTime().format(formatter) + "\n" +
                "Enrollment Ends : "+ schedule.get("enrollmentEnd").toLocalTime().format(formatter) + "\n" +
                "Market Opens : "+ schedule.get("marketStart").toLocalTime().format(formatter) + "\n" +
                "Market Closes : "+ schedule.get("marketEnd").toLocalTime().format(formatter);
    }

    @Command
    public String offer(String name, int amount) {
        try {
            Player player = new Player(name);
            Offer offer = new Offer(player, amount);
            return game.submitOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String removeOffer(String name, int amount) {
        try {
            Player player = new Player(name);
            Offer offer = new Offer(player, amount);
            return game.removeOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String submitBid(String name, String ownerName, int amount, String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.submitBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String removeBid(String name, String ownerName, int amount, String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.removeBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String acceptBid(String name, String ownerName, int amount, String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            BidView bidView = new BidView(player, owner, amount);
            return game.acceptBid(bidView, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String cornerMarket(String name, String commodity) {
        try {
            Player player = new Player(name);
            return game.cornerMarket(player, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String offers() {
        Set<Offer> offers = game.getOffers();
        return offers.stream().map(o -> o.getPlayer().getName() + " : " + o.getAmount() + "\n").reduce(String::concat).orElse("");
    }

    @Command
    public String bids() {
        List<BidView> bids = game.getBidViews();
        return bids.stream().map(b -> "Requester: " + b.getRequester().getName() +  " | Owner: "  + b.getOwner().getName() + " | Amount: " + b.getAmount() + "\n").reduce(String::concat).orElse("");
    }

    @Command
    public String trades() {
        List<Trade> trades = game.getTrades();
        return trades.stream().map(t -> t.getRequester().getName() + " <-> " + t.getOwner().getName() + " | Amount: " + t.getOwner()  + "\n").reduce(String::concat).orElse("");
    }

    public static void main(String[] args) throws IOException {
        tradeValidation = new TradeValidation(bank);
        game = new Game(bank, tradeValidation, market, gameClock);
        ShellFactory.createConsoleShell("pit console", "", new GameAppCLI()).commandLoop();
    }
}
