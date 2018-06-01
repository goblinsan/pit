package pit.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pit.*;
import pit.errors.GameError;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/player")
public class PlayerController {

    private final Game game;

    @Autowired
    public PlayerController(Game game) {
        this.game = game;
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("hand/{name}")
    @ResponseBody
    public String hand(@PathVariable String name) {
        try {
            Player player = new Player(name.toUpperCase());

            return game.getBank().getHoldings().get(player).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("offer/{name}/{amount}")
    @ResponseBody
    public String offer(@PathVariable String name, @PathVariable int amount) {
        try {
            Player player = new Player(name.toUpperCase());
            Offer offer = new Offer(player, amount);
            return game.submitOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("remove-offer/{name}/{amount}")
    @ResponseBody
    public String removeOffer(@PathVariable String name, @PathVariable int amount) {
        try {
            Player player = new Player(name.toUpperCase());
            Offer offer = new Offer(player, amount);
            return game.removeOffer(offer).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String submitBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name.toUpperCase());
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.submitBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("remove-bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String removeBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name.toUpperCase());
            Player owner = new Player(ownerName);
            Bid bid = new Bid(player, owner, amount, Commodity.valueOf(commodity));
            return game.removeBid(bid).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#ownerName.toUpperCase() == authentication.name")
    @RequestMapping("accept-bid/{name}/{ownerName}/{amount}/{commodity}")
    @ResponseBody
    public String acceptBid(@PathVariable String name,
                            @PathVariable String ownerName,
                            @PathVariable int amount,
                            @PathVariable String commodity) {
        try {
            Player player = new Player(name.toUpperCase());
            Player owner = new Player(ownerName);
            BidView bidView = new BidView(player, owner, amount);
            return game.acceptBid(bidView, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @PreAuthorize("#name.toUpperCase() == authentication.name")
    @RequestMapping("corner-market/{name}/{commodity}")
    @ResponseBody
    public String cornerMarket(@PathVariable String name, @PathVariable String commodity) {
        try {
            Player player = new Player(name.toUpperCase());
            return game.cornerMarket(player, Commodity.valueOf(commodity)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }
}
