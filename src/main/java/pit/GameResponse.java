package pit;

public enum GameResponse implements GameMessage {
    JOINED,
    ACCEPTED,
    REJECTED,
    INVALID,
    REMOVED,
    SCHEDULED,
    UNSCHEDULED,
    CREATED
}
