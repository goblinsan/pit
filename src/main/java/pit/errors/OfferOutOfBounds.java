package pit.errors;

import pit.GameResponse;

public class OfferOutOfBounds extends GameError {
    public OfferOutOfBounds(GameResponse status, String message) {
        super(status, message);
    }
}
