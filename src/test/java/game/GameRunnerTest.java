package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.RepeatedTest;
import players.AlwaysRollAgainPlayer;
import players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static dice.Die.STANDARD_SET;
import static dice.DieFace.BLAST;
import static dice.DieFace.BRAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameRunnerTest {
    // TODO test resetting game state each turn

    final AlwaysRollAgainPlayer player1 = new AlwaysRollAgainPlayer();
    final AlwaysRollAgainPlayer player2 = new AlwaysRollAgainPlayer();

    final GameRunner runner = new GameRunner(new Player[]{player1, player2}, STANDARD_SET);

    final int testRepeats = 100000;

    @RepeatedTest(testRepeats)
    void numberOfDiceRemainsConstant() {
        GameState finalGameState = runner.runGame();

        assertEquals(STANDARD_SET.length, finalGameState.diceInCup().length + finalGameState.diceOnTable().length);
    }

    @RepeatedTest(testRepeats)
    void coloursOfDiceRemainConstant() {
        GameState finalGameState = runner.runGame();

        List<DieColour> finalCupDiceColours = Arrays.stream(finalGameState.diceInCup()).map(Die::getColour).toList();
        List<DieColour> finalTableDiceColours = Arrays.stream(finalGameState.diceOnTable()).map(Die::getColour).toList();
        List<DieColour> finalDiceColours = Stream.concat(finalCupDiceColours.stream(), finalTableDiceColours.stream()).toList();
        List<DieColour> initialDiceColours = Arrays.stream(STANDARD_SET).map(Die::getColour).toList();
        assertEquals(initialDiceColours.stream().sorted().toList(), finalDiceColours.stream().sorted().toList());
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasWinner() {
        GameState finalGameState = runner.runGame();

        assertTrue(finalGameState.winner().isPresent());
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasAtLeastThreeDiceOnTable() {
        GameState finalGameState = runner.runGame();

        assertTrue(finalGameState.diceOnTable().length >= 3);
    }

    @RepeatedTest(testRepeats)
    void finalGameStateHasAtLeastOneBrainOnTable() {
        GameState finalGameState = runner.runGame();

        assertTrue(Arrays.stream(finalGameState.diceOnTable()).anyMatch(die -> die.getCurrentFace().equals(Optional.of(BRAIN))));
    }

    @RepeatedTest(testRepeats)
    void finalGameStateDoesNotHaveThreeBlastsOnTableUnlessWinningScore() {
        GameState finalGameState = runner.runGame();

        int blastsOnTable = (int) Arrays.stream(finalGameState.diceOnTable()).filter(die -> die.getCurrentFace().equals(Optional.of(BLAST))).count();
        int currentPlayerScore = finalGameState.playerScores().get(finalGameState.currentPlayer());

        // Player may have three blasts on table as long as also winning on that roll
        assertTrue(blastsOnTable < 3 || currentPlayerScore >= 13);
    }

}
