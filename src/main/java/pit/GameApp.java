package pit;


import asg.cliche.Command;
import asg.cliche.ShellFactory;
import pit.bank.Bank;
import pit.errors.GameError;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

public class GameApp {

    private static Bank bank = new Bank();
    private static TradeValidation tradeValidation;
    private static Market market = new Market();
    private static Clock gameClock = Clock.systemDefaultZone();
    private static Game game;

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
        return game.getClockTime().toLocalTime().toString();
    }

    @Command
    public String schedule() {
        return game.getMarket().getSchedule().toString();
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

    public static void main(String[] args) throws IOException {
        tradeValidation = new TradeValidation(bank);
        game = new Game(bank, tradeValidation, market, gameClock);
        ShellFactory.createConsoleShell("pit console", "", new GameApp()).commandLoop();
    }
}
