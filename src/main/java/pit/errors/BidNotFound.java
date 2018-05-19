package pit.errors;

import pit.GameMessage;

public class BidNotFound extends GameError {
    public BidNotFound(GameMessage status, String message) {
        super(status, message);
    }
}
