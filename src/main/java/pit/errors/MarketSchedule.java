package pit.errors;

import pit.GameMessage;

public class MarketSchedule extends GameError {
    public MarketSchedule(GameMessage status, String message) {
        super(status, message);
    }
}
