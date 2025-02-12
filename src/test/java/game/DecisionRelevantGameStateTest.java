package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.*;

import static dice.DieColour.*;
import static dice.DieFace.*;
import static org.junit.jupiter.api.Assertions.*;

class DecisionRelevantGameStateTest {

    final Die greenBrainDie = new Die(GREEN, Optional.of(BRAIN));
    final Die redBlastDie = new Die(RED, Optional.of(BLAST));
    final Die greenFootstepsDie = new Die(GREEN, Optional.of(FOOTSTEPS));
    final Die yellowFootstepsDie = new Die(YELLOW, Optional.of(FOOTSTEPS));
    final Die redFootstepsDie = new Die(RED, Optional.of(FOOTSTEPS));
    final Die unrolledYellowDie = new Die(YELLOW, Optional.empty());
    final Die unrolledGreenDie = new Die(GREEN, Optional.empty());
    final Die unrolledRedDie1 = new Die(RED, Optional.empty());
    final Die unrolledRedDie2 = new Die(RED, Optional.empty());
    final Die unrolledRedDie3 = new Die(RED, Optional.empty());
    final Player player1 = new RandomPlayer();
    final Player player2 = new RandomPlayer();

    @Test
    void constructsFromGameState() {
        Map<Player, Integer> playerScores = Map.of(player1, 5, player2, 6);
        GameState gameState = new GameState(List.of(unrolledYellowDie, unrolledRedDie1, unrolledGreenDie), List.of(redBlastDie, redFootstepsDie, greenBrainDie, greenFootstepsDie), playerScores, player1, 2, 3);

        DecisionRelevantGameState decisionRelevantGameState = DecisionRelevantGameState.fromGameState(gameState);

        assertIterableEquals(List.of(GREEN, YELLOW, RED), decisionRelevantGameState.coloursInCup());
        assertIterableEquals(List.of(GREEN, RED), decisionRelevantGameState.footstepsColours());
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

            /* Possible dice states (note that states with 3 footsteps are "collapsed", with empty dice in cup representing all cup states):
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
                11  1       0               0       0
                12  1       0               0       1
                13  1       0               1       0
                14  1       1               0       0
                15  1       1               0       1
                16  1       1               1       0
                17  1       2               0       0
                18  1       2               1       0
                19  2       0               0       0
                20  2       0               0       1
                21  2       0               1       0
                22  2       1               0       0
                23  2       1               0       1
                24  2       1               1       0
                25  3       0               0       0
                26  3       0               0       1
                27  3       0               1       0
             */

            // Blasts and blasts range from 0 to 1 in this test, so 2 each; 27 * 2 * 2 = 108
            assertEquals(108, generatedStates.size());
        }

        @Test
        void testStateCountsForSpecificCupConfiguration() {
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(List.of(unrolledRedDie1, unrolledGreenDie), 2, 2);

            // Filter states where the cup has [RED, GREEN] (both dice) and footstepsColours is empty
            long matchingStates = generatedStates.stream()
                    .filter(state -> state.coloursInCup().equals(List.of(GREEN, RED)) && state.footstepsColours().isEmpty())
                    .count();

            // Expect states for all permutations of brains and blasts
            int expectedStates = 9; // Blasts (0-2) × brains (0-2)
            assertEquals(expectedStates, matchingStates);
        }

        @Test
        void sortsColours() {
            Set<DecisionRelevantGameState> generatedStates = DecisionRelevantGameState.generateAllStates(List.of(yellowFootstepsDie, redBlastDie, greenBrainDie), 2, 2);

            generatedStates.forEach(state -> {
                assertEquals(state.coloursInCup().stream().sorted(Comparator.comparingInt(DieColour::ordinal)).toList(), state.coloursInCup());
                assertEquals(state.footstepsColours().stream().sorted(Comparator.comparingInt(DieColour::ordinal)).toList(), state.footstepsColours());
            });
        }

    }

}
