package pit;


import asg.cliche.Command;
import asg.cliche.ShellFactory;
import pit.bank.Bank;
import pit.errors.GameError;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

public class GameApp {

    private static Bank bank = new Bank();
    private static TradeValidation tradeValidation;
    private static Market market = new Market();
    private static Clock gameClock = Clock.systemDefaultZone();
    private static Game game;

    @Command
    public String createPlayer(String name) {
        return game.createPlayer(name).toString();
    }

    @Command
    public String joinGame(String name) {
        try {
            return game.join(new Player(name)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String startGame(){
        try {
            return game.getMarket().scheduleEnrollment(LocalDateTime.now(gameClock)).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    @Command
    public String getPortfolio(String name){
        try {
            Player player = new Player(name);
            return game.getBank().getHoldings().get(player).toString();
        } catch (GameError e) {
            return e.getMessage();
        }
    }

    public static void main(String[] args) throws IOException {
        tradeValidation = new TradeValidation(bank);
        game = new Game(bank, tradeValidation, market, gameClock);
        ShellFactory.createConsoleShell("pit console", "", new GameApp()).commandLoop();
    }
}
