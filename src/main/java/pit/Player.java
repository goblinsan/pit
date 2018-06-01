package pit;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Player {
    private String name;
    private int score;
    private boolean connected;

    public Player(String name){
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
