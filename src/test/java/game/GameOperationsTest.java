package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static dice.DieFace.BLAST;
import static dice.DieFace.BRAIN;
import static org.junit.jupiter.api.Assertions.*;

public class GameOperationsTest {

    Die die1 = new Die(DieColour.GREEN, Optional.empty());
    Die die2 = new Die(DieColour.YELLOW, Optional.empty());
    Die die3 = new Die(DieColour.RED, Optional.empty());
    Die die4 = new Die(DieColour.RED, Optional.empty());
    Player player1 = new RandomPlayer();
    Player player2 = new RandomPlayer();

    final int testRepeats = 100;

    @Nested
    class DrawAndRollDiceFromCupTests {

        @Test
        void drawsSpecifiedNumberOfDiceWhenCupHasEnoughDice() {
            GameState initialGameState = new GameState(
                    new Die[]{die1, die2, die3, die4},
                    new Die[]{},
                    Map.of(player1, 10),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = GameOperations.drawAndRollDiceFromCup(initialGameState, 3);

            // Assert that three dice are removed from the cup and three are now on the table
            assertEquals(1, updatedGameState.diceInCup().length);
            assertEquals(3, updatedGameState.diceOnTable().length);

            // Player scores and current player should remain unchanged; blasts and brains may vary depending on rolls
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        }

        @Test
        void usesBrainDiceFromTableIfCupHasInsufficientDice() {
            GameState initialGameState = new GameState(
                    new Die[]{die1, die2},
                    new Die[]{new Die(DieColour.RED, Optional.of(BRAIN))}, // die3 is not brain, so can't be used
                    Map.of(player1, 10),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = GameOperations.drawAndRollDiceFromCup(initialGameState, 3);

            // Assert that two dice are removed from the cup and three are now on the table
            assertEquals(0, updatedGameState.diceInCup().length);
            assertEquals(3, updatedGameState.diceOnTable().length);

            // Player scores and current player should remain unchanged; blasts and brains may vary depending on rolls
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        }

        @Test
        void throwsExceptionIfInsufficientDice() {
            GameState initialGameState = new GameState(
                    new Die[]{die1, die2},
                    new Die[]{die3}, // die3 is not brains, so can't be used
                    Map.of(player1, 10),
                    player1,
                    1,
                    2
            );

            assertThrows(IllegalArgumentException.class, () -> GameOperations.drawAndRollDiceFromCup(initialGameState, 3));
        }

    }

    @Test
    void movesDiceToCup() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        GameState updatedGameState = GameOperations.moveDiceToCup(initialGameState, new Die[]{die3});

        assertArrayEquals(new Die[]{die1, die2, die3}, updatedGameState.diceInCup());
        assertArrayEquals(new Die[]{die4}, updatedGameState.diceOnTable());
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        // Blasts and brains this turn stay the same
        assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
        assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
    }

    @Test
    void throwsExceptionIfMovingNonExistentDiceToCup() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        assertThrows(IllegalArgumentException.class, () -> GameOperations.moveDiceToCup(gameState, new Die[]{die2}));
    }

    @RepeatedTest(testRepeats)
    void rollsDiceToTable() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        GameState updatedGameState = GameOperations.rollDiceToTable(initialGameState, new Die[]{die2});
        DieColour[] updatedDieColoursOnTable = Arrays.stream(updatedGameState.diceOnTable()).map(Die::getColour).toArray(DieColour[]::new);
        int brainsOnTable = (int) Arrays.stream(updatedGameState.diceOnTable()).map(Die::getCurrentFace).filter(face -> face.equals(Optional.of(BRAIN))).count();
        int blastsOnTable = (int) Arrays.stream(updatedGameState.diceOnTable()).map(Die::getCurrentFace).filter(face -> face.equals(Optional.of(BLAST))).count();

        assertArrayEquals(new Die[]{die1}, updatedGameState.diceInCup());
        // Can't assert exact die on table, since it's been rolled
        assertEquals(3, updatedGameState.diceOnTable().length);
        assertArrayEquals(new DieColour[]{die3.getColour(), die4.getColour(), die2.getColour()}, updatedDieColoursOnTable);
        // Player scores stay the same
        assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        // Blasts and brains this turn incremented according to roll
        assertEquals(blastsOnTable + 1, updatedGameState.blastsThisTurn());
        assertEquals(brainsOnTable + 2, updatedGameState.brainsThisTurn());
    }

    @Test
    void throwsExceptionIfDieRolledToTableIsNotInCup() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        assertThrows(IllegalArgumentException.class, () -> GameOperations.rollDiceToTable(gameState, new Die[]{die3}));
    }

    @RepeatedTest(testRepeats)
    void rollsDiceOnTable() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        GameState updatedGameState = GameOperations.rollDiceOnTable(initialGameState, new Die[]{die3});
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
        // Blasts and brains this turn incremented according to roll
        assertEquals(updatedGameState.diceOnTable()[0].getCurrentFace().equals(Optional.of(BLAST)) ? 2 : 1, updatedGameState.blastsThisTurn());
        assertEquals(updatedGameState.diceOnTable()[0].getCurrentFace().equals(Optional.of(BRAIN)) ? 3 : 2, updatedGameState.brainsThisTurn());
    }

    @Test
    void throwsExceptionIfDieRolledOnTableIsNotOnTable() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        assertThrows(IllegalArgumentException.class, () -> GameOperations.rollDiceOnTable(gameState, new Die[]{die2}));
    }

    @Test
    void addsPlayerScore() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10),
                player1,
                1,
                2
        );

        GameState updatedGameState = GameOperations.addPlayerScore(initialGameState, player1, 5);

        assertEquals(15, updatedGameState.playerScores().get(player1));
        // Dice in cup stay the same
        assertArrayEquals(new Die[]{die1, die2}, updatedGameState.diceInCup());
        // Dice on table stay the same
        assertArrayEquals(new Die[]{die3, die4}, updatedGameState.diceOnTable());
        // Current player stays the same
        assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        // Blasts and brains this turn stay the same
        assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
        assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
    }

    @Test
    void addingPlayerScoreThrowsExceptionIfPlayerNotInGame() {
        GameState gameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(),
                player1,
                1,
                2
        );

        assertThrows(IllegalArgumentException.class, () -> GameOperations.addPlayerScore(gameState, player1, 5));
    }

    @Test
    void resetsStateForNextTurn() {
        GameState initialGameState = new GameState(
                new Die[]{die1, die2},
                new Die[]{die3, die4},
                Map.of(player1, 10, player2, 12),
                player1,
                1,
                2
        );

        GameState resetState = GameOperations.resetForNextTurn(initialGameState);

        assertArrayEquals(new Die[]{die1, die2, die3, die4}, resetState.diceInCup());
        assertArrayEquals(new Die[]{}, resetState.diceOnTable());
        assertEquals(initialGameState.playerScores(), resetState.playerScores());
        assertEquals(player2, resetState.currentPlayer());
        assertEquals(0, resetState.blastsThisTurn());
        assertEquals(0, resetState.brainsThisTurn());
    }

}
