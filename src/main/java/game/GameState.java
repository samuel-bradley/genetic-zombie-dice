package game;

import dice.Die;
import players.Player;

import java.util.*;
import java.util.stream.Collectors;

import static game.GameOperations.getRandomPlayer;

public record GameState(Die[] diceInCup, Die[] diceOnTable, Map<Player, Integer> playerScores, Player currentPlayer, int blastsThisTurn, int brainsThisTurn) {

    public static GameState makeInitialState(Player[] players, Die[] dice) {
        // Initialise player scores to zero
        Map<Player, Integer> playersAndScores = Arrays.stream(players)
                .collect(Collectors.toMap(p -> p, p -> 0));
        return new GameState(dice, new Die[0], playersAndScores, getRandomPlayer(players), 0, 0);
    }

    public GameState withDiceInCup(Die[] dice) {
        return new GameState(
                dice,
                diceOnTable,
                playerScores,
                currentPlayer,
                blastsThisTurn,
                brainsThisTurn
        );
    }

    public GameState withDiceOnTable(Die[] dice) {
        return new GameState(
                diceInCup,
                dice,
                playerScores,
                currentPlayer,
                blastsThisTurn,
                brainsThisTurn
        );
    }

    public GameState withPlayerScores(Map<Player, Integer> scores) {
        return new GameState(
                diceInCup,
                diceOnTable,
                scores,
                currentPlayer,
                blastsThisTurn,
                brainsThisTurn
        );
    }

    public GameState withCurrentPlayer(Player newCurrentPlayer) {
        if (!playerScores.containsKey(newCurrentPlayer)) {
            throw new IllegalArgumentException("Player " + newCurrentPlayer + " is not in the current game state.");
        }

        return new GameState(
            diceInCup,
            diceOnTable,
            playerScores,
            newCurrentPlayer,
            blastsThisTurn,
            brainsThisTurn
        );
    }

    public GameState withBlastsThisTurn(int newBlastsThisTurn) {
        return new GameState(diceInCup, diceOnTable, playerScores, currentPlayer, newBlastsThisTurn, brainsThisTurn);
    }

    public GameState withBrainsThisTurn(int newBrainsThisTurn) {
        return new GameState(diceInCup, diceOnTable, playerScores, currentPlayer, blastsThisTurn, newBrainsThisTurn);
    }

    public Optional<Player> winner() {
        return playerScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= 13)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public boolean turnIsOver() {
        int currentPlayerScore = playerScores.get(currentPlayer);
        return blastsThisTurn >= 3 || currentPlayerScore + brainsThisTurn >= 13;
    }

    @Override
    public String toString() {
        String diceInCupString = Arrays.stream(diceInCup()).map(Die::toString).collect(Collectors.joining(", "));
        String diceOnTableString = Arrays.stream(diceOnTable()).map(Die::toString).collect(Collectors.joining(", "));
        String playerScoresString = playerScores.entrySet().stream()
                .map((entry) -> entry.getKey().toString() + ": " + entry.getValue().toString())
                .collect(Collectors.joining(", "));
        String currentPlayerString = currentPlayer.toString();
        return "diceInCup: " + diceInCupString + "\ndiceOnTable: " + diceOnTableString + "\nplayerScores: " + playerScoresString + "\ncurrentPlayer: " + currentPlayerString;
    }
}
