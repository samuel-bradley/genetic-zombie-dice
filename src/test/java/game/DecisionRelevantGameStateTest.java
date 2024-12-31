package game;

import dice.Die;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dice.DieColour.*;
import static dice.DieFace.*;
import static org.junit.jupiter.api.Assertions.*;

class DecisionRelevantGameStateTest {

    final Die die1 = new Die(RED, Optional.of(BLAST));
    final Die die2 = new Die(GREEN, Optional.of(BRAIN));
    final Die die3 = new Die(YELLOW, Optional.of(FOOTSTEPS));
    final Die unrolledDie1 = new Die(RED, Optional.empty());
    final Die unrolledDie2 = new Die(GREEN, Optional.empty());
    final Player player1 = new RandomPlayer();
    final Player player2 = new RandomPlayer();

    @Test
    void constructsFromGameState() {
        Map<Player, Integer> playerScores = Map.of(player1, 5, player2, 6);
        GameState gameState = new GameState(List.of(unrolledDie1, unrolledDie2), List.of(die1, die2, die3), playerScores, player1, 2, 3);

        DecisionRelevantGameState decisionRelevantGameState = DecisionRelevantGameState.fromGameState(gameState);

        assertIterableEquals(List.of(RED, GREEN), decisionRelevantGameState.coloursInCup());
        assertIterableEquals(List.of(die1, die2, die3), decisionRelevantGameState.diceOnTable());
        assertEquals(2, decisionRelevantGameState.blastsThisTurn());
        assertEquals(3, decisionRelevantGameState.brainsThisTurn());
    }

    @Test
    void testEqualsAndHashCode() {

        // Create two identical instances
        DecisionRelevantGameState state1 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(die1, die2), 2, 3);
        DecisionRelevantGameState state2 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(die1, die2), 2, 3);

        // Verify equality and hashCode
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());

        // Modify one attribute (diceOnTable) to ensure inequality
        DecisionRelevantGameState state3 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(die1, die3), 2, 3);
        assertNotEquals(state1, state3);
    }

    @Nested
    class StatesGenerationTests {

        @Test
        void generatesCorrectNumberOfStates() {
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(List.of(unrolledDie1, unrolledDie2), 1, 1);

            // Calculate the expected number of states
            int numDice = 2;
            int numDieStates = 4; // In cup, or one of three faces on table = 1 + 3
            int numDiceStates = (int) Math.pow(numDieStates, numDice); // Die states are independent so multiply them together
            int numBrains = 2; // Brains range from 0 to 1 in this test
            int numBlasts = 2; // Blasts range from 0 to 1 in this test
            int expectedNumberOfStates = numDiceStates * numBlasts * numBrains; // 64

            assertEquals(expectedNumberOfStates, generatedStates.size());
        }

        @Test
        void testStateCountsForSpecificCupConfiguration() {
            final Die unrolledDie1 = new Die(RED, Optional.empty());
            final Die unrolledDie2 = new Die(GREEN, Optional.empty());
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(List.of(unrolledDie1, unrolledDie2), 2, 2);

            // Filter states where the cup has [RED, GREEN] (both dice)
            long matchingStates = generatedStates.stream().filter(state -> state.coloursInCup().equals(List.of(RED, GREEN))).count();

            // Expect states for all permutations of brains and blasts
            int expectedStates = 9; // Blasts (0-2) × brains (0-2)
            assertEquals(expectedStates, matchingStates);
        }

    }

}
