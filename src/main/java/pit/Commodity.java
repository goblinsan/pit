package pit;

public enum Commodity {
    COCOA	    (100),
    PLATINUM	(85),
    GOLD	    (80),
    CATTLE	    (75),
    OIL	        (65),
    RICE	    (60),
    SILVER      (55),
    GAS	        (50);

    private final int marketValue;

    Commodity(int marketValue) {
        this.marketValue = marketValue;
    }

    public int getMarketValue() {
        return marketValue;
    }
}
