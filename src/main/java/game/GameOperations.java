package game;

import dice.Die;
import players.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static dice.DieFace.BRAIN;
import static dice.DieFace.FOOTSTEPS;

public class GameOperations {

    public static Player getRandomPlayer(Player[] players) {
        final int randomIndex = ThreadLocalRandom.current().nextInt(players.length);
        return players[randomIndex];
    }

    public static GameState drawAndRollDiceFromCup(GameState gameState, int numberToDrawAndRoll) {
        if (gameState.diceInCup().length == numberToDrawAndRoll) {
            // Exact number of required dice in cup - just roll these
            return rollDiceToTable(gameState, gameState.diceInCup());
        } else if (gameState.diceInCup().length > numberToDrawAndRoll) {
            // More than sufficient dice in cup - select from them randomly
            final Die[] diceToDraw = getRandomDice(gameState.diceInCup(), numberToDrawAndRoll);
            return rollDiceToTable(gameState, diceToDraw);
        } else {
            // Insufficient dice left in cup - first move brains from table to cup
            final Die[] brainsOnTable = Arrays.stream(gameState.diceOnTable())
                    .filter((die) -> die.getCurrentFace().isPresent() && die.getCurrentFace().get() == BRAIN)
                    .toArray(Die[]::new);
            final GameState withBrainsInCup = moveDiceToCup(gameState, brainsOnTable);
            // Now roll dice randomly from the cup
            final Die[] diceToDraw = getRandomDice(withBrainsInCup.diceInCup(), numberToDrawAndRoll);
            return rollDiceToTable(withBrainsInCup, diceToDraw);
        }
    }

    public static GameState rollFootprintsFromTable(GameState gameState, int numberToRoll) {
        final Die[] footprintsOnTable = Arrays.stream(gameState.diceOnTable())
                .filter((die) -> die.getCurrentFace().isPresent() && die.getCurrentFace().get() == FOOTSTEPS).limit(numberToRoll)
                .toArray(Die[]::new);
        return rollDiceOnTable(gameState, footprintsOnTable);
    }

    public static GameState moveDiceToCup(GameState gameState, Die[] diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(Arrays.asList(gameState.diceOnTable()));
        List<Die> cupDiceList = new ArrayList<>(Arrays.asList(gameState.diceInCup()));

        for (Die die : diceToMove) {
            if (!tableDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not on the table.");
            }
            cupDiceList.add(die);
        }

        return gameState
                .withDiceOnTable(tableDiceList.toArray(Die[]::new))
                .withDiceInCup(cupDiceList.toArray(Die[]::new));
    }

    public static GameState rollDiceToTable(GameState gameState, Die[] diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(Arrays.asList(gameState.diceOnTable()));
        List<Die> cupDiceList = new ArrayList<>(Arrays.asList(gameState.diceInCup()));

        for (Die die : diceToMove) {
            if (!cupDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not in the cup.");
            }
            tableDiceList.add(die.rolled());
        }

        return gameState
                .withDiceOnTable(tableDiceList.toArray(Die[]::new))
                .withDiceInCup(cupDiceList.toArray(Die[]::new));
    }

    public static GameState rollDiceOnTable(GameState gameState, Die[] diceToRoll) {
        // Check if all of diceToRoll are on the table
        List<Die> diceOnTableList = Arrays.asList(gameState.diceOnTable());
        for (Die die : diceToRoll) {
            if (!diceOnTableList.contains(die)) {
                throw new IllegalArgumentException("Attempted to roll a die that is not on the table.");
            }
        }

        Die[] newDiceOnTable = Arrays.stream(gameState.diceOnTable()).map((die) -> {
            if (!Arrays.asList(diceToRoll).contains(die)) {
                return die; // Not rolling this one
            }
            return die.rolled();
        }).toArray(Die[]::new);

        return gameState.withDiceOnTable(newDiceOnTable);
    }

    private static Die[] getRandomDice(Die[] dice, int numberToGet) {
        if (numberToGet > dice.length)
            throw new IllegalArgumentException("Cannot get " + numberToGet + " dice from " + dice.length + " dice");

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final Set<Integer> selectedIndices = new HashSet<>();

        while (selectedIndices.size() < numberToGet) {
            int index = random.nextInt(dice.length);
            // Track selected dice; adding to HashSet ensures uniqueness
            selectedIndices.add(index);
        }

        Die[] result = new Die[numberToGet];
        int i = 0;
        for (int index : selectedIndices) {
            result[i++] = dice[index];
        }

        return result;
    }

    public static GameState addPlayerScore(GameState gameState, Player player, int scoreToAdd) {
        if (!gameState.playerScores().containsKey(player)) {
            throw new IllegalArgumentException("Player " + player + " is not in the current game state.");
        }
        Map<Player, Integer> newScores = new HashMap<>(gameState.playerScores());
        newScores.put(player, newScores.getOrDefault(player, 0) + scoreToAdd);
        return gameState.withPlayerScores(newScores);
    }

    public static GameState resetForNextTurn(GameState gameState) {
        final Player nextPlayer = getNextPlayer(gameState);
        final GameState withDiceMovedToCup = moveDiceToCup(gameState, gameState.diceOnTable());
        return withDiceMovedToCup.withCurrentPlayer(nextPlayer)
                .withBrainsThisTurn(0)
                .withBlastsThisTurn(0);
    }

    private static Player getNextPlayer(GameState gameState) {
        int currentPlayerIndex = getPlayerIndex(gameState, gameState.currentPlayer());
        int nextPlayerIndex = (currentPlayerIndex + 1) % gameState.playerScores().size();
        return (Player) gameState.playerScores().keySet().toArray()[nextPlayerIndex];
    }

    private static int getPlayerIndex(GameState gameState, Player player) {
        final Player[] players = gameState.playerScores().keySet().toArray(Player[]::new);
        for (int i = 0; i < players.length; i++)
            if (players[i].equals(player)) return i;
        throw new IllegalArgumentException("Asked to find a player not in the players array");
    }

}
