package pit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pit.bank.Bank;
import pit.errors.GameError;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@SpringBootApplication
public class GameAppWeb {

    private static Bank bank = new Bank();
    private static TradeValidation tradeValidation;
    private static Market market = new Market();
    private static Clock gameClock = Clock.systemDefaultZone();
    private static Game game;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @RequestMapping("createPlayer/{name}")
    @ResponseBody
    public String createPlayer(@PathVariable String name) {
        return game.createPlayer(name).toString();
    }

    @RequestMapping("join/{name}")
    @ResponseBody
    public String join(@PathVariable String name) {
        try {
            return game.join(new Player(name,0,true)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("start")
    public String start() {
        try {
            return game.getMarket().scheduleEnrollment(LocalDateTime.now(gameClock)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("hand/{name}")
    @ResponseBody
    public String hand(@PathVariable String name) {
        try {
            Player player = new Player(name);
            return game.getBank().getHoldings().get(player).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("time")
    @ResponseBody
    public String time() {
        return game.getClockTime().toLocalTime().format(formatter);
    }

    @RequestMapping("schedule")
    @ResponseBody
    public Map<String, LocalDateTime> schedule() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();

        return schedule;
    }

    @RequestMapping("scheduleStrings")
    @ResponseBody
    public Map<String, String> scheduleStrings() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();
        Map<String, String> formatted = new LinkedHashMap<>();
        schedule.forEach((s, localDateTime) -> formatted.put(s, localDateTime.format(formatter)));

        return formatted;
    }

    @RequestMapping("players")
    @ResponseBody
    public List<Player> players() {
        return game.getPlayers();
    }

    @RequestMapping("offer/{name}/{amount}")
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

    @RequestMapping("remove-offer/{name}/{amount}")
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

    @RequestMapping("bid/{name}/{ownerName}/{amount}/{commodity}")
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

    @RequestMapping("remove-bid/{name}/{ownerName}/{amount}/{commodity}")
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

    @RequestMapping("accept-bid/{name}/{ownerName}/{amount}/{commodity}")
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

    @RequestMapping("corner-market/{name}/{commodity}")
    @ResponseBody
    public String cornerMarket(@PathVariable String name, @PathVariable String commodity) {
        try {
            Player player = new Player(name);
            return game.cornerMarket(player, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("offers")
    @ResponseBody
    public List<Offer> offers() {
        return game.getOffers();
    }

    @RequestMapping("bids")
    @ResponseBody
    public List<BidView> bids() {
        return game.getBidViews();
    }

    @RequestMapping("trades")
    @ResponseBody
    public List<Trade> trades() {
        return game.getTrades();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GameAppWeb.class, args);
        tradeValidation = new TradeValidation(bank);
        game = new Game(bank, tradeValidation, market, gameClock);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer () {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");
            }
        };
    }
}
