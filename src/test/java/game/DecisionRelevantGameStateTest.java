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
    final Die unrolledRedDie1 = new Die(RED, Optional.empty());
    final Die unrolledRedDie2 = new Die(RED, Optional.empty());
    final Die unrolledRedDie3 = new Die(RED, Optional.empty());
    final Die unrolledGreenDie = new Die(GREEN, Optional.empty());
    final Player player1 = new RandomPlayer();
    final Player player2 = new RandomPlayer();

    @Test
    void constructsFromGameState() {
        Map<Player, Integer> playerScores = Map.of(player1, 5, player2, 6);
        GameState gameState = new GameState(List.of(unrolledRedDie1, unrolledRedDie2, unrolledGreenDie), List.of(die1, die2, die3), playerScores, player1, 2, 3);

        DecisionRelevantGameState decisionRelevantGameState = DecisionRelevantGameState.fromGameState(gameState);

        assertIterableEquals(List.of(RED, RED, GREEN), decisionRelevantGameState.coloursInCup());
        assertIterableEquals(List.of(YELLOW), decisionRelevantGameState.footstepsColours());
        assertEquals(2, decisionRelevantGameState.blastsThisTurn());
        assertEquals(3, decisionRelevantGameState.brainsThisTurn());
    }

    @Test
    void testEqualsAndHashCode() {
        // Create two identical instances
        DecisionRelevantGameState state1 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(YELLOW), 2, 3);
        DecisionRelevantGameState state2 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(YELLOW), 2, 3);

        // Verify equality and hashCode
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());

        // Modify one attribute (one of the colours in the cup) to ensure inequality
        DecisionRelevantGameState state3 = new DecisionRelevantGameState(List.of(RED, GREEN, GREEN), List.of(YELLOW), 2, 3);
        assertNotEquals(state1, state3);

        // Modify footstepsColours to ensure inequality
        DecisionRelevantGameState state4 = new DecisionRelevantGameState(List.of(RED, GREEN, YELLOW), List.of(RED), 2, 3);
        assertNotEquals(state1, state4);
    }

    @Nested
    class StatesGenerationTests {

        @Test
        void generatesCorrectNumberOfStates() {
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(
                    List.of(unrolledRedDie1, unrolledRedDie2, unrolledRedDie3, unrolledGreenDie), 1, 1
            );

            /* Possible dice states:
                #   R_cup   R_footprints    G_cup   G_footprints
                1   0       0               0       0
                2   0       0               0       1
                3   0       0               1       0
                4   0       1               0       0
                5   0       1               0       1
                6   0       1               1       0
                7   0       2               0       0
                8   0       2               0       1
                9   0       2               1       0
                10  0       3               0       0
                11  0       3               1       0
                12  1       0               0       0
                13  1       0               0       1
                14  1       0               1       0
                15  1       1               0       0
                16  1       1               0       1
                17  1       1               1       0
                18  1       2               0       0
                19  1       2               0       1
                20  1       2               1       0
                21  2       0               0       0
                22  2       0               0       1
                23  2       0               1       0
                24  2       1               0       0
                25  2       1               0       1
                26  2       1               1       0
                27  3       0               0       0
                28  3       0               0       1
                29  3       0               1       0
             */

            // Blasts and blasts range from 0 to 1 in this test, so 2 each; 29 * 2 * 2 = 116
            assertEquals(116, generatedStates.size());
        }

        @Test
        void testStateCountsForSpecificCupConfiguration() {
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(List.of(unrolledRedDie1, unrolledGreenDie), 2, 2);

            // Filter states where the cup has [RED, GREEN] (both dice) and footstepsColours is empty
            long matchingStates = generatedStates.stream()
                    .filter(state -> state.coloursInCup().equals(List.of(RED, GREEN)) && state.footstepsColours().isEmpty())
                    .count();

            // Expect states for all permutations of brains and blasts
            int expectedStates = 9; // Blasts (0-2) × brains (0-2)
            assertEquals(expectedStates, matchingStates);
        }

    }

}
