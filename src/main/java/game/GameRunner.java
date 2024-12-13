package game;

import dice.Die;
import players.Player;
import players.RandomPlayer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static game.GameOperations.resetForNextTurn;

public class GameRunner {

    private final Player[] players;
    private final Die[] dice;
    private final TurnRunner turnRunner;

    GameRunner(Player[] players, Die[] dice) {
        if (players.length < 2) {
            throw new IllegalArgumentException("Need at least 2 players to run a game, got " + players.length);
        }
        if (dice.length < 2) {
            throw new IllegalArgumentException("Need at least 3 dice to run a game, got " + dice.length);
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
        Player[] players = {new RandomPlayer(), new RandomPlayer()};
        GameRunner runner = new GameRunner(players, Die.STANDARD_SET);
        int gamesRun = 0;
        var time1 = LocalTime.now();
        while (gamesRun < 50000) {
            GameState wonGameState = runner.runGame();
            System.out.println(wonGameState.toString());
            gamesRun++;
        }
        var time2 = LocalTime.now();
        System.out.println(time1.until(time2, ChronoUnit.MILLIS));
    }

}
