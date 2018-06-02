package pit.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pit.*;
import pit.errors.GameError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UnprotectedController {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final Game game;

    @Autowired
    public UnprotectedController(Game game) {
        this.game = game;
    }

    @RequestMapping("createPlayer/{name}")
    public String createPlayer(@PathVariable String name) {
        return game.createPlayer(name).toString();
    }

    @RequestMapping("connect/{name}")
    public String connect(@PathVariable String name) {
        try {
            return game.connect(name).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("join/{name}")
    public String join(@PathVariable String name) {
        try {
            return game.join(new Player(name, 0, true)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @RequestMapping("time")
    public String time() {
        return game.getClockTime().toLocalTime().format(formatter);
    }

    @RequestMapping("schedule")
    public Map<String, LocalDateTime> schedule() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();

        return schedule;
    }

    @RequestMapping("scheduleStrings")
    public Map<String, String> scheduleStrings() {
        Map<String, LocalDateTime> schedule = game.getMarket().getSchedule();
        Map<String, String> formatted = new LinkedHashMap<>();
        schedule.forEach((s, localDateTime) -> formatted.put(s, localDateTime.format(formatter)));

        return formatted;
    }

    @RequestMapping("players")
    public List<Player> players() {
        return game.getPlayerMapAsList();
    }

    @RequestMapping("offers")
    public List<Offer> offers() {
        return game.getOffers();
    }

    @RequestMapping("bids")
    public List<BidView> bids() {
        return game.getBidViews();
    }

    @RequestMapping("trades")
    public List<Trade> trades() {
        return game.getTrades();
    }
}
