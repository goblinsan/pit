package pit;

public enum GameResponse implements GameMessage {
    JOINED,
    CONNECTED,
    ACCEPTED,
    REJECTED,
    INVALID,
    REMOVED,
    SCHEDULED,
    UNSCHEDULED,
    CREATED
}
