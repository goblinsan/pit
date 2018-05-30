package pit;

public enum MarketState implements GameMessage {
    UNSCHEDULED,
    CLOSED,
    ENROLLMENT_OPEN,
    ENROLLMENT_CLOSED,
    OPEN
}
