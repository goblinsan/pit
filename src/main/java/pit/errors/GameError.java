package pit.errors;

import pit.GameMessage;

public class GameError extends RuntimeException {
    private GameMessage status;

    public GameError(GameMessage status, String message) {
        super(message);
        this.status = status;
    }

    public GameMessage getStatus() {
        return status;
    }
}
