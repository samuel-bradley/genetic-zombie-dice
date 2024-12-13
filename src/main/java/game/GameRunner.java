package game;

import dice.Die;
import players.Player;
import players.RandomPlayer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static game.GameOperations.resetForNextTurn;

public class GameRunner {

    private final List<Player> players;
    private final List<Die> dice;
    private final TurnRunner turnRunner;

    GameRunner(List<Player> players, List<Die> dice) {
        if (players.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 players to run a game, got " + players.size());
        }
        if (dice.size() < 2) {
            throw new IllegalArgumentException("Need at least 3 dice to run a game, got " + dice.size());
        }
        this.players = players;
        this.dice = dice;
        this.turnRunner = new TurnRunner();
    }

    public GameState runGame() {
        GameState gameState = GameState.makeInitialState(players, dice);
        do {
            gameState = turnRunner.runTurn(gameState);
            if (gameState.winner().isEmpty()) gameState = resetForNextTurn(gameState);
        }
        while (gameState.winner().isEmpty());
        return gameState;
    }

    public static void main(String[] args) {
        List<Player> players = List.of(new RandomPlayer(), new RandomPlayer());
        GameRunner runner = new GameRunner(players, Die.STANDARD_SET);
        int gamesRun = 0;
        var time1 = LocalTime.now();
        while (gamesRun < 1000000) {
            GameState wonGameState = runner.runGame();
            System.out.println(wonGameState.toString());
            gamesRun++;
        }
        var time2 = LocalTime.now();
        System.out.println(time1.until(time2, ChronoUnit.MILLIS));
    }

}
