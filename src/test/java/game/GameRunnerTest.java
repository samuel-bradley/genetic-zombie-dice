package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import players.RollAgainOncePlayer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static dice.Die.STANDARD_SET;
import static dice.DieFace.BLAST;
import static dice.DieFace.BRAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameRunnerTest {
    final RollAgainOncePlayer player1 = new RollAgainOncePlayer();
    final RollAgainOncePlayer player2 = new RollAgainOncePlayer();

    final GameRunner runner = new GameRunner(List.of(player1, player2), STANDARD_SET);

    final int testRepeats = 100;

    @BeforeEach
    void beforeEach() {
        player1.reset();
        player2.reset();
    }

    @RepeatedTest(testRepeats)
    void numberOfDiceRemainsConstant() {
        GameState finalGameState = runner.runGame();

        assertEquals(STANDARD_SET.size(), finalGameState.diceInCup().size() + finalGameState.diceOnTable().size());
    }

    @RepeatedTest(testRepeats)
    void coloursOfDiceRemainConstant() {
        GameState finalGameState = runner.runGame();

        List<DieColour> finalCupDiceColours = finalGameState.diceInCup().stream().map(Die::getColour).toList();
        List<DieColour> finalTableDiceColours = finalGameState.diceOnTable().stream().map(Die::getColour).toList();
        List<DieColour> finalDiceColours = Stream.concat(finalCupDiceColours.stream(), finalTableDiceColours.stream()).sorted().toList();
        List<DieColour> initialDiceColours = STANDARD_SET.stream().map(Die::getColour).sorted().toList();
        assertEquals(initialDiceColours, finalDiceColours);
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasWinner() {
        GameState finalGameState = runner.runGame();

        assertTrue(finalGameState.winner().isPresent());
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasAtLeastThreeDiceOnTable() {
        GameState finalGameState = runner.runGame();

        assertTrue(finalGameState.diceOnTable().size() >= 3);
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasAtLeastOneBrainOnTable() {
        GameState finalGameState = runner.runGame();

        assertTrue(finalGameState.diceOnTable().stream().anyMatch(die -> die.getCurrentFace().equals(Optional.of(BRAIN))));
    }

    @RepeatedTest(testRepeats)
    void finalGameStateDoesNotHaveThreeBlastsOnTableUnlessWinningScore() {
        GameState finalGameState = runner.runGame();

        int blastsOnTable = (int) finalGameState.diceOnTable().stream().filter(die -> die.getCurrentFace().equals(Optional.of(BLAST))).count();
        int currentPlayerScore = finalGameState.playerScores().get(finalGameState.currentPlayer());

        // Player may have three blasts on table as long as also winning on that roll
        assertTrue(blastsOnTable < 3 || currentPlayerScore >= 13);
    }

    @RepeatedTest(testRepeats)
    void eachPlayerTakesTurns() {
        runner.runGame();

        assertTrue(player1.getTimesAsked() > 1);
        assertTrue(player2.getTimesAsked() > 1);
    }

}
