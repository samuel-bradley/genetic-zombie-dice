package game;

import dice.Die;
import players.Player;

import java.util.*;
import java.util.stream.Collectors;

import static game.GameOperations.getRandomPlayer;

public record GameState(Die[] diceInCup, Die[] diceOnTable, Map<Player, Integer> playerScores, Player currentPlayer) {

    public static GameState makeInitialState(Player[] players, Die[] dice) {
        // Initialise player scores to zero
        Map<Player, Integer> playersAndScores = Arrays.stream(players)
                .collect(Collectors.toMap(p -> p, p -> 0));
        return new GameState(dice, new Die[0], playersAndScores, getRandomPlayer(players));
    }

    public GameState withDiceMovedToCup(Die[] diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(Arrays.asList(diceOnTable));
        List<Die> cupDiceList = new ArrayList<>(Arrays.asList(diceInCup));

        for (Die die : diceToMove) {
            if (!tableDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not on the table.");
            }
            cupDiceList.add(die);
        }

        return new GameState(
            cupDiceList.toArray(new Die[0]),
            tableDiceList.toArray(new Die[0]),
            playerScores,
            currentPlayer
        );
    }

    public GameState withDiceRolledToTable(Die[] diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(Arrays.asList(diceOnTable));
        List<Die> cupDiceList = new ArrayList<>(Arrays.asList(diceInCup));

        for (Die die : diceToMove) {
            if (!cupDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not in the cup.");
            }
            tableDiceList.add(die.rolled());
        }

        return new GameState(
            cupDiceList.toArray(new Die[0]),
            tableDiceList.toArray(new Die[0]),
            playerScores,
            currentPlayer
        );
    }

    public GameState withDiceOnTableRolled(Die[] diceToRoll) {
        // Check if all of diceToRoll are on the table
        List<Die> diceOnTableList = Arrays.asList(diceOnTable);
        for (Die die : diceToRoll) {
            if (!diceOnTableList.contains(die)) {
                throw new IllegalArgumentException("Attempted to roll a die that is not on the table.");
            }
        }

        Die[] newDiceOnTable = Arrays.stream(diceOnTable).map((die) -> {
            if (!Arrays.asList(diceToRoll).contains(die)) {
                // Not rolling this one
                return die;
            }
            return die.rolled();
        }).toArray(Die[]::new);

        return new GameState(
                diceInCup,
                newDiceOnTable,
                playerScores,
                currentPlayer
        );
    }

    public GameState withPlayerScoreAdded(Player player, int scoreToAdd) {
        if (!playerScores.containsKey(player)) {
            throw new IllegalArgumentException("Player " + player + " is not in the current game state.");
        }

        Map<Player, Integer> newScores = new HashMap<>(playerScores);

        newScores.put(player, newScores.getOrDefault(player, 0) + scoreToAdd);

        return new GameState(
            diceInCup,
            diceOnTable,
            newScores,
            currentPlayer
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
            newCurrentPlayer
        );
    }

    public Optional<Player> winner() {
        return playerScores.entrySet().stream()
            .filter(entry -> entry.getValue() >= 13)
            .map(Map.Entry::getKey)
            .findFirst();
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
