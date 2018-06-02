package pit;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Getter
public class Player {
    private String name;
    private int score;
    private boolean connected;

    public Player(String name){
        this.name = name.toUpperCase();
    }

    public Player(String name, int score, boolean connected) {
        this.name = name.toUpperCase();
        this.score = score;
        this.connected = connected;
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
