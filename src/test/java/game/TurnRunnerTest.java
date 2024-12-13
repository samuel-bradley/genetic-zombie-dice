package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static dice.Die.STANDARD_SET;
import static dice.DieColour.*;
import static dice.DieFace.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TurnRunnerTest {
    // TODO maybe some of these like the conservation tests would be better placed in the GameRunnerTest

    final TurnRunner runner = new TurnRunner();
    final AlwaysRollAgainPlayer alwaysRollAgainPlayer = new AlwaysRollAgainPlayer();
    final NeverRollAgainPlayer neverRollAgainPlayer = new NeverRollAgainPlayer();
    final RollAgainOncePlayer rollAgainOncePlayer = new RollAgainOncePlayer();
    final int testRepeats = 100;

    @BeforeEach
    void beforeEach() {
        rollAgainOncePlayer.reset();
    }

    @Test
    void doesNotDrawDiceFromCupIfThreeFootstepsOnTable() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{new Die(GREEN, Optional.of(FOOTSTEPS)), new Die(GREEN, Optional.of(FOOTSTEPS)), new Die(GREEN, Optional.of(FOOTSTEPS))},
                Map.of(neverRollAgainPlayer, 10),
                neverRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        // Should not have drawn any dice from cup, since three footsteps already on table
        assertEquals(STANDARD_SET.length, finalGameState.diceInCup().length);
    }

    @Test
    void drawsDiceFromCupIfInsufficientFootstepsOnTable() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{new Die(GREEN, Optional.of(FOOTSTEPS)), new Die(GREEN, Optional.of(FOOTSTEPS))},
                Map.of(neverRollAgainPlayer, 10),
                neverRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        // Should have drawn one die from cup, since only two footsteps already on table
        assertEquals(STANDARD_SET.length - 1, finalGameState.diceInCup().length);
    }

    @Test
    void returnsBrainsFromTableToCupIfInsufficientDiceInCup() {
        // initial game state has 1 dice in cup, 1 footstep on table, N brains on table, 'always roll again' player - assert N-1 in cup

        GameState initialGameState = new GameState(
                new Die[]{new Die(YELLOW, Optional.empty())},
                new Die[]{new Die(GREEN, Optional.of(FOOTSTEPS)), new Die(RED, Optional.of(BRAIN)), new Die(RED, Optional.of(BRAIN)), new Die(RED, Optional.of(BRAIN))},
                Map.of(neverRollAgainPlayer, 10),
                neverRollAgainPlayer,
                0,
                0
        );

        // One die in cup, three brains on table - should return brains to table and draw two, leaving two in cup
        GameState finalGameState = runner.runTurn(initialGameState);

        assertEquals(2, finalGameState.diceInCup().length);
    }

    @RepeatedTest(testRepeats)
    void addsRolledBrainsToCurrentPlayerScore() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(neverRollAgainPlayer, 10),
                neverRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        assertEquals(finalGameState.brainsThisTurn() + 10, finalGameState.playerScores().get(neverRollAgainPlayer));
    }

    @Test
    void leavesThreeDiceOnTableAfterOneRoll() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(neverRollAgainPlayer, 0),
                neverRollAgainPlayer,
                0,
                0
        );

        // There will be one mandatory initial roll
        GameState finalGameState = runner.runTurn(initialGameState);

        assertEquals(3, finalGameState.diceOnTable().length);
    }

    @RepeatedTest(testRepeats)
    void numberOfDiceRemainsConstant() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 0),
                alwaysRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        assertEquals(STANDARD_SET.length, finalGameState.diceInCup().length + finalGameState.diceOnTable().length);
    }

    @RepeatedTest(testRepeats)
    void coloursOfDiceRemainConstant() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 0),
                alwaysRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        List<DieColour> finalCupDiceColours = Arrays.stream(finalGameState.diceInCup()).map(Die::getColour).toList();
        List<DieColour> finalTableDiceColours = Arrays.stream(finalGameState.diceOnTable()).map(Die::getColour).toList();
        List<DieColour> finalDiceColours = Stream.concat(finalCupDiceColours.stream(), finalTableDiceColours.stream()).toList();
        List<DieColour> initialDiceColours = Arrays.stream(STANDARD_SET).map(Die::getColour).toList();
        assertEquals(initialDiceColours.stream().sorted().toList(), finalDiceColours.stream().sorted().toList());
    }

    @RepeatedTest(testRepeats)
    void allDiceOnTableHaveDefinedFaces() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 0),
                alwaysRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        assertTrue(Arrays.stream(finalGameState.diceOnTable()).allMatch(die -> die.getCurrentFace().isPresent()));
    }

    @RepeatedTest(testRepeats)
    void totalBlastsNeverExceedsFive() {
        // The most blasts we should end up with is 2 on the table plus 3 extra rolled, since 3 or more ends the turn
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 0),
                alwaysRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        assertTrue(finalGameState.blastsThisTurn() <= 5);
    }

    @RepeatedTest(testRepeats)
    void totalBrainsNeverExceedsFifteen() {
        // The most brains we should end up with is 12 on the table plus 3 extra rolled, since 13 or more ends the turn
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 0),
                alwaysRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        assertTrue(finalGameState.brainsThisTurn() <= 13);
    }

    @RepeatedTest(testRepeats)
    void doesNotRollAgainIfTurnIsOverDueToBlasts() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{new Die(RED, Optional.of(BLAST)), new Die(RED, Optional.of(BLAST)), new Die(RED, Optional.of(BLAST))},
                Map.of(alwaysRollAgainPlayer, 10),
                alwaysRollAgainPlayer,
                0,
                0
        );

        // One initial roll from cup and then the game is immediately over (unrealistic setup: three blasts on table)
        GameState finalGameState = runner.runTurn(initialGameState);

        // Assert only initial 3 dice were rolled
        assertEquals(STANDARD_SET.length - 3, finalGameState.diceInCup().length);
    }

    @RepeatedTest(testRepeats)
    void doesNotRollAgainIfTurnIsOverDueToScore() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(alwaysRollAgainPlayer, 13),
                alwaysRollAgainPlayer,
                0,
                0
        );

        // One initial roll from cup and then the game is immediately over (unrealistic setup: already winning score)
        GameState finalGameState = runner.runTurn(initialGameState);

        // Assert only initial 3 dice were rolled
        assertEquals(STANDARD_SET.length - 3, finalGameState.diceInCup().length);
    }

    @RepeatedTest(testRepeats)
    void doesNotRollAgainIfPlayerChoosesNotTo() {
        GameState initialGameState = new GameState(
                STANDARD_SET,
                new Die[]{},
                Map.of(neverRollAgainPlayer, 0),
                neverRollAgainPlayer,
                0,
                0
        );

        GameState finalGameState = runner.runTurn(initialGameState);

        // Assert only initial 3 dice were rolled
        assertEquals(STANDARD_SET.length - 3, finalGameState.diceInCup().length);
        assertEquals(3, finalGameState.diceOnTable().length);
    }

    private static class AlwaysRollAgainPlayer implements Player {
        @Override
        public boolean rollAgain(DecisionRelevantGameState gameState) {
            return true;
        }
    }

    private static class NeverRollAgainPlayer implements Player {
        @Override
        public boolean rollAgain(DecisionRelevantGameState gameState) {
            return false;
        }
    }

    private static class RollAgainOncePlayer implements Player {
        private boolean hasRolled = false;
        private int timesAsked = 0;

        @Override
        public boolean rollAgain(DecisionRelevantGameState gameState) {
            ++timesAsked;
            if (hasRolled) {
                return false;
            } else {
                hasRolled = true;
                return true;
            }
        }

        public void reset() {
            timesAsked = 0;
            hasRolled = false;
        }

        public int getTimesAsked() {
            return timesAsked;
        }

    }
}

