package pit.errors;

import pit.GameResponse;

public class GameError extends RuntimeException {
    GameResponse status;

    public GameError(GameResponse status, String message) {
        super(message);
        this.status = status;
    }

    public GameResponse getStatus() {
        return status;
    }
}
