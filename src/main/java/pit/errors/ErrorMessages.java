package pit.errors;

public class ErrorMessages {
    public static final String PLAYER_CANNOT_SATISFY_BID = "Player does not own enough matching commodities to satisfy bid.";
    public static final String BID_LESS_THAN_ZERO = "Bid amount must be greater than zero.";
    public static final String BID_NOT_FOUND = "Bid not currently found in the market.";
    public static final String OFFER_LESS_THAN_ZERO = "Offer amount must be greater than zero.";
    public static final String PLAYER_CANNOT_SATISFY_OFFER = "Player does not own enough matching commodities to satisfy offer.";
    public static final String PLAYER_CANNOT_JOIN_MORE_THAN_ONCE = "Players can only join one time.";
    public static final String MARKET_NOT_SCHEDULED = "No schedule is posted for the next market open.";
    public static final String ENROLLMENT_NOT_OPEN = "Enrollment is not currently open.";
    public static final String MARKET_NOT_OPEN = "Market is not currently open.";
}
