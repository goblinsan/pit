package pit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
class BidView {
    private Player requester;
    private Player owner;
    private int amount;
}
