package pit.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pit.Game;
import pit.errors.GameError;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final Game game;

    @Autowired
    public AdminController(Game game) {
        this.game = game;
    }

    @RequestMapping("start")
    public String start() {
        try {
            return game.getMarket().scheduleEnrollment(game.getClockTime()).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }
}
