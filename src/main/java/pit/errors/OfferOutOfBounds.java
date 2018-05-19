package pit.errors;

import pit.GameErrors;

public class OfferOutOfBounds extends GameError {
    public OfferOutOfBounds(GameErrors status, String message) {
        super(status, message);
    }
}
