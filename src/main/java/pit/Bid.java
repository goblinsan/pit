package pit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Bid {
    private Player requester;
    private Player owner;
    private int amount;
    private Commodity commodity;
}
