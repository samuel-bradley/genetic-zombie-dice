package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    Die die1 = new Die(DieColour.GREEN, Optional.empty());
    Die die2 = new Die(DieColour.YELLOW, Optional.empty());
    Die die3 = new Die(DieColour.RED, Optional.empty());
    Die die4 = new Die(DieColour.RED, Optional.empty());
    Player player1 = new RandomPlayer();
    Player player2 = new RandomPlayer();

    @Test
    void makesInitialState() {
        GameState state = GameState.makeInitialState(new Player[]{player1, player2}, new Die[]{die1, die2, die3, die4});
        assertArrayEquals(new Die[]{die1, die2, die3, die4}, state.diceInCup());
        assertArrayEquals(new Die[]{}, state.diceOnTable());
        assertEquals(Map.of(player1, 0, player2, 0), state.playerScores());
    }

    @Test
    void movesDiceToCup() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        GameState updatedGameState = initialGameState.withDiceMovedToCup(new Die[]{die3});

        assertArrayEquals(new Die[]{die1, die2, die3}, updatedGameState.diceInCup());
        assertArrayEquals(new Die[]{die4}, updatedGameState.diceOnTable());
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
    }

    @Test
    void throwsExceptionIfMovingNonExistentDiceFromCup() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        assertThrows(IllegalArgumentException.class, () -> gameState.withDiceMovedToCup(new Die[]{die2}));
    }

    @Test
    void rollsDiceToTable() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        GameState updatedGameState = initialGameState.withDiceRolledToTable(new Die[]{die2});
        DieColour[] updatedDieColoursOnTable = Arrays.stream(updatedGameState.diceOnTable()).map(Die::getColour).toArray(DieColour[]::new);

        assertArrayEquals(new Die[]{die1}, updatedGameState.diceInCup());
        // Can't assert exact die on table, since it's been rolled
        assertEquals(3, updatedGameState.diceOnTable().length);
        assertArrayEquals(new DieColour[]{die3.getColour(), die4.getColour(), die2.getColour()}, updatedDieColoursOnTable);
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
    }

    @Test
    void throwsExceptionIfDieRolledToTableIsNotInCup() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        assertThrows(IllegalArgumentException.class, () -> gameState.withDiceRolledToTable(new Die[]{die3}));
    }

    @Test
    void rollsDiceOnTable() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        GameState updatedGameState = initialGameState.withDiceOnTableRolled(new Die[]{die3});
        DieColour[] initialDieColoursOnTable = Arrays.stream(initialGameState.diceOnTable()).map(Die::getColour).toArray(DieColour[]::new);
        DieColour[] updatedDieColoursOnTable = Arrays.stream(updatedGameState.diceOnTable()).map(Die::getColour).toArray(DieColour[]::new);

        // Can't assert exact dice on table, since one's been rolled
        assertEquals(2, updatedGameState.diceOnTable().length);
        assertArrayEquals(initialDieColoursOnTable, updatedDieColoursOnTable);
        assertTrue(Arrays.asList(updatedGameState.diceOnTable()).contains(die4));
        // Dice in cup stay the same
        assertArrayEquals(new Die[]{die1, die2}, updatedGameState.diceInCup());
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
    }

    @Test
    void throwsExceptionIfDieRolledOnTableIsNotOnTable() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        assertThrows(IllegalArgumentException.class, () -> gameState.withDiceOnTableRolled(new Die[]{die2}));
    }

    @Test
    void addsPlayerScore() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        GameState updatedGameState = initialGameState.withPlayerScoreAdded(player1, 5);

        assertEquals(15, updatedGameState.playerScores().get(player1));
        // Dice in cup stay the same
        assertArrayEquals(new Die[]{die1, die2}, updatedGameState.diceInCup());
        // Dice on table stay the same
        assertArrayEquals(new Die[]{die3, die4}, updatedGameState.diceOnTable());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
    }

    @Test
    void addingPlayerScoreThrowsExceptionIfPlayerNotInGame() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(),
                player1
        );

        assertThrows(IllegalArgumentException.class, () -> gameState.withPlayerScoreAdded(player1, 5));
    }

    @Test
    void updatesCurrentPlayer() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10, player2, 5),
                player1
        );

        GameState updatedGameState = initialGameState.withCurrentPlayer(player2);

        assertEquals(player2, updatedGameState.currentPlayer());
        // Dice in cup stay the same
        assertArrayEquals(new Die[]{die1, die2}, updatedGameState.diceInCup());
        // Dice on table stay the same
        assertArrayEquals(new Die[]{die3, die4}, updatedGameState.diceOnTable());
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
    }

    @Test
    void updatingCurrentPlayerThrowsExceptionIfPlayerNotInGame() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1
        );

        assertThrows(IllegalArgumentException.class, () -> gameState.withCurrentPlayer(player2));
    }

    @Test
    void findsWinnerWhenPlayerExceedsWinningScore() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10, player2, 14),
                player1
        );

        Optional<Player> winner = gameState.winner();

        assertTrue(winner.isPresent());
        assertEquals(player2, winner.get());
    }

    @Test
    void findsWinnerWhenPlayerHasWinningScore() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 13, player2, 10),
                player1
        );

        Optional<Player> winner = gameState.winner();

        assertTrue(winner.isPresent());
        assertEquals(player1, winner.get());
    }

    @Test
    void doesNotFindWinnerWhenNoPlayerHasWinningScore() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10, player2, 12),
                player1
        );

        Optional<Player> winner = gameState.winner();

        assertTrue(winner.isEmpty());
    }
}
