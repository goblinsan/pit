package pit.errors;

import pit.GameErrors;

public class BidOutOfBounds extends GameError {
    public BidOutOfBounds(GameErrors status, String message) {
        super(status, message);
    }
}
