package pit.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pit.Game;
import pit.GameResponse;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final Game game;

    @Autowired
    public AdminController(Game game) {
        this.game = game;
    }

    @RequestMapping("schedule/{state}")
    public ResponseEntity<String> schedule(@PathVariable("state") String state) {
        if (state.equalsIgnoreCase("start")) {
            game.disconnectPlayers();
            return ResponseEntity.ok(game.getMarket().scheduleEnrollment(game.getClockTime()).toString());
        } else if (state.equalsIgnoreCase("open")) {
            return ResponseEntity.ok(game.getMarket().scheduleMarketOpen(game.getClockTime()).toString());
        } else if (state.equalsIgnoreCase("close")) {
            return ResponseEntity.ok(game.getMarket().scheduleMarketClose(game.getClockTime()).toString());
        }
        return ResponseEntity.ok(GameResponse.INVALID.toString());
    }

}
