package game;

import dice.Die;
import players.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static dice.DieFace.*;

public class GameOperations {

    public static Player getRandomPlayer(List<Player> players) {
        final int randomIndex = ThreadLocalRandom.current().nextInt(players.size());
        return players.get(randomIndex);
    }

    public static GameState drawAndRollDiceFromCup(GameState gameState, int numberToDrawAndRoll) {
        if (gameState.diceInCup().size() == numberToDrawAndRoll) {
            // Exact number of required dice in cup - just roll these
            return rollDiceToTable(gameState, gameState.diceInCup());
        } else if (gameState.diceInCup().size() > numberToDrawAndRoll) {
            // More than sufficient dice in cup - select from them randomly
            final List<Die> diceToDraw = getRandomDice(gameState.diceInCup(), numberToDrawAndRoll);
            return rollDiceToTable(gameState, diceToDraw);
        } else {
            // Insufficient dice left in cup - first move brains from table to cup
            final List<Die> brainsOnTable = gameState.diceOnTable().stream()
                    .filter((die) -> die.getCurrentFace().isPresent() && die.getCurrentFace().get() == BRAIN)
                    .toList();
            final GameState withBrainsInCup = moveDiceToCup(gameState, brainsOnTable);
            // Now roll dice randomly from the cup
            final List<Die> diceToDraw = getRandomDice(withBrainsInCup.diceInCup(), numberToDrawAndRoll);
            return rollDiceToTable(withBrainsInCup, diceToDraw);
        }
    }

    public static GameState rollFootprintsFromTable(GameState gameState, int numberToRoll) {
        final List<Die> footprintsOnTable = gameState.diceOnTable().stream()
                .filter((die) -> die.getCurrentFace().isPresent() && die.getCurrentFace().get() == FOOTSTEPS).limit(numberToRoll)
                .toList();
        return rollDiceOnTable(gameState, footprintsOnTable);
    }

    public static GameState moveDiceToCup(GameState gameState, List<Die> diceToMove) {
        List<Die> tableDiceList = new ArrayList<>(gameState.diceOnTable());
        List<Die> cupDiceList = new ArrayList<>(gameState.diceInCup());

        for (Die die : diceToMove) {
            if (!tableDiceList.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not on the table.");
            }
            cupDiceList.add(die);
        }

        return gameState
                .withDiceOnTable(tableDiceList)
                .withDiceInCup(cupDiceList);
    }

    public static GameState rollDiceToTable(GameState gameState, List<Die> diceToMove) {
        List<Die> newDiceOnTable = new ArrayList<>(gameState.diceOnTable());
        List<Die> newDiceInCup = new ArrayList<>(gameState.diceInCup());

        AtomicInteger brainsRolled = new AtomicInteger();
        AtomicInteger blastsRolled = new AtomicInteger();
        for (Die die : diceToMove) {
            if (!newDiceInCup.remove(die)) {
                throw new IllegalArgumentException("Die " + die + " is not in the cup.");
            }
            Die rolledDie = die.rolled();
            if (rolledDie.getCurrentFace().equals(Optional.of(BRAIN))) brainsRolled.incrementAndGet();
            if (rolledDie.getCurrentFace().equals(Optional.of(BLAST))) blastsRolled.incrementAndGet();
            newDiceOnTable.add(rolledDie);
        }

        return gameState
                .withDiceOnTable(newDiceOnTable)
                .withDiceInCup(newDiceInCup)
                .withBrainsThisTurn(gameState.brainsThisTurn() + brainsRolled.get())
                .withBlastsThisTurn(gameState.blastsThisTurn() + blastsRolled.get());
    }

    public static GameState rollDiceOnTable(GameState gameState, List<Die> diceToRoll) {
        // Check if all of diceToRoll are on the table
        for (Die die : diceToRoll) {
            if (!gameState.diceOnTable().contains(die)) {
                throw new IllegalArgumentException("Attempted to roll a die that is not on the table.");
            }
        }

        AtomicInteger brainsRolled = new AtomicInteger();
        AtomicInteger blastsRolled = new AtomicInteger();
        List<Die> newDiceOnTable = gameState.diceOnTable().stream().map((die) -> {
            if (!diceToRoll.contains(die)) {
                return die; // Not rolling this one
            }
            Die rolledDie = die.rolled();
            if (rolledDie.getCurrentFace().equals(Optional.of(BRAIN))) brainsRolled.incrementAndGet();
            if (rolledDie.getCurrentFace().equals(Optional.of(BLAST))) blastsRolled.incrementAndGet();
            return rolledDie;
        }).toList();

        return gameState
                .withDiceOnTable(newDiceOnTable)
                .withBrainsThisTurn(gameState.brainsThisTurn() + brainsRolled.get())
                .withBlastsThisTurn(gameState.blastsThisTurn() + blastsRolled.get());
    }

    private static List<Die> getRandomDice(List<Die> dice, int numberToGet) {
        if (numberToGet > dice.size())
            throw new IllegalArgumentException("Cannot get " + numberToGet + " dice from " + dice.size() + " dice");

        final ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Die> shuffledDice = (new ArrayList<>(dice));
        Collections.shuffle(shuffledDice, random);

        return shuffledDice.subList(0, numberToGet);
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
