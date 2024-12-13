package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.List;
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
        GameState state = GameState.makeInitialState(List.of(player1, player2), List.of(die1, die2, die3, die4));
        assertIterableEquals(List.of(die1, die2, die3, die4), state.diceInCup());
        assertIterableEquals(List.of(), state.diceOnTable());
        assertEquals(Map.of(player1, 0, player2, 0), state.playerScores());
    }

    @Nested
    class UpdateStateTests {

        @Test
        void updatesDiceInCup() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 5),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withDiceInCup(List.of(die1, die2, die3));

            assertIterableEquals(List.of(die1, die2, die3), updatedGameState.diceInCup());
            // Dice on table stay the same
            assertIterableEquals(List.of(die3, die4), updatedGameState.diceOnTable());
            // Player scores stay the same
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            // Current player stays the same
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
            // Blasts and brains this turn stay the same
            assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
            assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
        }

        @Test
        void updatesDiceOnTable() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 5),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withDiceOnTable(List.of(die1, die2, die3));

            assertIterableEquals(List.of(die1, die2, die3), updatedGameState.diceOnTable());
            // Dice in cup stay the same
            assertIterableEquals(List.of(die1, die2), updatedGameState.diceInCup());
            // Player scores stay the same
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            // Current player stays the same
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
            // Blasts and brains this turn stay the same
            assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
            assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
        }

        @Test
        void updatesPlayerScores() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 5),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withPlayerScores(Map.of(player1, 12, player2, 5));

            assertEquals(Map.of(player1, 12, player2, 5), updatedGameState.playerScores());
            // Dice in cup stay the same
            assertIterableEquals(List.of(die1, die2), updatedGameState.diceInCup());
            // Dice on table stay the same
            assertIterableEquals(List.of(die3, die4), updatedGameState.diceOnTable());
            // Current player stays the same
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
            // Blasts and brains this turn stay the same
            assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
            assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
        }

        @Test
        void updatesCurrentPlayer() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 5),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withCurrentPlayer(player2);

            assertEquals(player2, updatedGameState.currentPlayer());
            // Dice in cup stay the same
            assertIterableEquals(List.of(die1, die2), updatedGameState.diceInCup());
            // Dice on table stay the same
            assertIterableEquals(List.of(die3, die4), updatedGameState.diceOnTable());
            // Player scores stay the same
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            // Blasts and brains this turn stay the same
            assertEquals(initialGameState.blastsThisTurn(), updatedGameState.blastsThisTurn());
            assertEquals(initialGameState.brainsThisTurn(), updatedGameState.brainsThisTurn());
        }

        @Test
        void updatingCurrentPlayerThrowsExceptionIfPlayerNotInGame() {
            GameState gameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10),
                    player1,
                    1,
                    2
            );

            assertThrows(IllegalArgumentException.class, () -> gameState.withCurrentPlayer(player2));
        }

        @Test
        void updatesBlastsThisTurn() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 14),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withBlastsThisTurn(3);

            assertEquals(3, updatedGameState.blastsThisTurn());
            // Brains this turn stays the same
            assertEquals(2, updatedGameState.brainsThisTurn());
            // Dice in cup stay the same
            assertIterableEquals(List.of(die1, die2), updatedGameState.diceInCup());
            // Dice on table stay the same
            assertIterableEquals(List.of(die3, die4), updatedGameState.diceOnTable());
            // Player scores stay the same
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            // Current player stays the same
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        }

        @Test
        void updatesBrainsThisTurn() {
            GameState initialGameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 14),
                    player1,
                    1,
                    2
            );

            GameState updatedGameState = initialGameState.withBrainsThisTurn(3);

            assertEquals(3, updatedGameState.brainsThisTurn());
            // Blasts this turn stays the same
            assertEquals(1, updatedGameState.blastsThisTurn());
            // Dice in cup stay the same
            assertIterableEquals(List.of(die1, die2), updatedGameState.diceInCup());
            // Dice on table stay the same
            assertIterableEquals(List.of(die3, die4), updatedGameState.diceOnTable());
            // Player scores stay the same
            assertEquals(initialGameState.playerScores(), updatedGameState.playerScores());
            // Current player stays the same
            assertEquals(initialGameState.currentPlayer(), updatedGameState.currentPlayer());
        }
    }

    @Nested
    class WinnerTests {

        @Test
        void findsWinnerWhenPlayerExceedsWinningScore() {
            GameState gameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 14),
                    player1,
                    1,
                    2
            );

            Optional<Player> winner = gameState.winner();

            assertTrue(winner.isPresent());
            assertEquals(player2, winner.get());
        }

        @Test
        void findsWinnerWhenPlayerHasWinningScore() {
            GameState gameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 13, player2, 10),
                    player1,
                    1,
                    2
            );

            Optional<Player> winner = gameState.winner();

            assertTrue(winner.isPresent());
            assertEquals(player1, winner.get());
        }

        @Test
        void doesNotFindWinnerWhenNoPlayerHasWinningScore() {
            GameState gameState = new GameState(
                    List.of(die1, die2),
                    List.of(die3, die4),
                    Map.of(player1, 10, player2, 12),
                    player1,
                    1,
                    2
            );

            Optional<Player> winner = gameState.winner();

            assertTrue(winner.isEmpty());
        }
    }

    @Nested
    class TurnIsOverTests {

        @Test
        void turnIsOverWhenBlastsIsThree() {
            GameState gameState = new GameState(
                    List.of(),
                    List.of(),
                    Map.of(player1, 5, player2, 10),
                    player1,
                    3,
                    0
            );
            assertTrue(gameState.turnIsOver(), "Turn should be over when blasts is 3.");
        }

        @Test
        void turnIsOverWhenBlastsExceedsThree() {
            GameState gameState = new GameState(
                    List.of(),
                    List.of(),
                    Map.of(player1, 5, player2, 10),
                    player1,
                    4,
                    0
            );
            assertTrue(gameState.turnIsOver(), "Turn should be over when blasts exceeds 3.");
        }

        @Test
        void turnIsOverWhenCurrentPlayerScorePlusBrainsIsThirteen() {
            GameState gameState = new GameState(
                    List.of(),
                    List.of(),
                    Map.of(player1, 10, player2, 10),
                    player1,
                    2,
                    3
            );
            assertTrue(gameState.turnIsOver(), "Turn should be over when current player score plus brains equals 13.");
        }

        @Test
        void turnIsOverWhenCurrentPlayerScorePlusBrainsExceedsThirteen() {
            GameState gameState = new GameState(
                    List.of(),
                    List.of(),
                    Map.of(player1, 11, player2, 10),
                    player1,
                    2,
                    3
            );
            assertTrue(gameState.turnIsOver(), "Turn should be over when current player score plus brains exceeds 13.");
        }

        @Test
        void turnNotOverWhenBlastsLessThanThreeAndBrainsAndCurrentPlayerScoreLessThanThirteen() {
            GameState gameState = new GameState(
                    List.of(),
                    List.of(),
                    Map.of(player1, 10, player2, 10),
                    player1,
                    2,
                    2
            );
            assertFalse(gameState.turnIsOver(), "Turn should not be over when blasts are less than 3 and current player score is less than 13.");
        }
    }
}
