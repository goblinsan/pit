package pit;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Component
public class GameClock extends Clock {

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(Instant.now(), zone);
    }

    @Override
    public Instant instant() {
        return Instant.now();
    }
}
