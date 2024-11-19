package game;

import dice.Die;
import players.Player;

import java.util.*;

public record GameState(Die[] diceInCup, Die[] diceOnTable, Map<Player, Integer> playerScores, Player currentPlayer) {

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

    public GameState withDiceMovedToTable(Die[] diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(Arrays.asList(diceOnTable));
        List<Die> cupDiceList = new ArrayList<>(Arrays.asList(diceInCup));

        for (Die die : diceToMove) {
            if (!cupDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not in the cup.");
            }
            tableDiceList.add(die);
        }

        return new GameState(
            cupDiceList.toArray(new Die[0]),
            tableDiceList.toArray(new Die[0]),
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

}
