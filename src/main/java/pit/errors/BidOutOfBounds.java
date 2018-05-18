package pit.errors;

import pit.GameResponse;

public class BidOutOfBounds extends GameError {
    public BidOutOfBounds(GameResponse status, String message) {
        super(status, message);
    }
}
