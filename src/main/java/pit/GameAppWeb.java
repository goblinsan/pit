package pit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pit.bank.Bank;
import pit.errors.GameError;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class GameAppWeb {

    private static Bank bank = new Bank();
    private static TradeValidation tradeValidation;
    private static Market market = new Market();
    private static Clock gameClock = Clock.systemDefaultZone();
    private static Game game;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Welcome to Pit!";
    }

    @RequestMapping("/createPlayer/{name}")
    @ResponseBody
    public String createPlayer(@PathVariable String name) {
        return game.createPlayer(name).toString();
    }

    @RequestMapping("/join/{name}")
    @ResponseBody
    public String join(@PathVariable String name) {
        try {
            return game.join(new Player(name)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/start")
    @ResponseBody
    public String start() {
        try {
            return game.getMarket().scheduleEnrollment(LocalDateTime.now(gameClock)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/hand/{name}")
    @ResponseBody
    public String hand(@PathVariable String name) {
        try {
            Player player = new Player(name);
            return game.getBank().getHoldings().get(player).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/time")
    @ResponseBody
    public String time() {
        return game.getClockTime().toLocalTime().format(formatter);
    }

    @RequestMapping("/schedule")
    @ResponseBody
    public String schedule() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();

        return "Enrollment Opens : "+ schedule.get("enrollmentStart").toLocalTime().format(formatter) + "\n" +
                "Enrollment Ends : "+ schedule.get("enrollmentEnd").toLocalTime().format(formatter) + "\n" +
                "Market Opens : "+ schedule.get("marketStart").toLocalTime().format(formatter) + "\n" +
                "Market Closes : "+ schedule.get("marketEnd").toLocalTime().format(formatter);
    }

    @RequestMapping("/offer/{name}/{amount}")
    @ResponseBody
    public String offer(@PathVariable String name, @PathVariable int amount) {
        try {
            Player player = new Player(name);
            Offer offer = new Offer(player, amount);
            return game.submitOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/remove-offer/{name}/{amount}")
    @ResponseBody
    public String removeOffer(@PathVariable String name, @PathVariable int amount) {
        try {
            Player player = new Player(name);
            Offer offer = new Offer(player, amount);
            return game.removeOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String submitBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.submitBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/remove-bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String removeBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.removeBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/accept-bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String acceptBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name);
            Player owner = new Player(ownerName);
            BidView bidView = new BidView(player, owner, amount);
            return game.acceptBid(bidView, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/corner-market/{name}/{commodity}")
    @ResponseBody
    public String cornerMarket(@PathVariable String name, @PathVariable String commodity) {
        try {
            Player player = new Player(name);
            return game.cornerMarket(player, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/offers")
    @ResponseBody
    public String offers() {
        List<Offer> offers = game.getOffers();
        return offers.stream().map(o -> o.getPlayer().getName() + " : " + o.getAmount() + "\n").reduce(String::concat).orElse("");
    }

    @RequestMapping("/bids")
    @ResponseBody
    public String bids() {
        List<BidView> bids = game.getBidViews();
        return bids.stream().map(b -> "Requester: " + b.getRequester().getName() +  " | Owner: "  + b.getOwner().getName() + " | Amount: " + b.getAmount() + "\n").reduce(String::concat).orElse("");
    }

    @RequestMapping("/trades")
    @ResponseBody
    public String trades() {
        List<Trade> trades = game.getTrades();
        return trades.stream().map(t -> t.requester.getName() + " <-> " + t.owner.getName() + " | Amount: " + t.amount  + "\n").reduce(String::concat).orElse("");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GameAppWeb.class, args);
        tradeValidation = new TradeValidation(bank);
        game = new Game(bank, tradeValidation, market, gameClock);
    }
}
