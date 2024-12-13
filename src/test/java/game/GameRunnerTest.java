package game;

import dice.Die;
import dice.DieColour;
import org.junit.jupiter.api.RepeatedTest;
import players.AlwaysRollAgainPlayer;
import players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static dice.Die.STANDARD_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameRunnerTest {

    final AlwaysRollAgainPlayer player1 = new AlwaysRollAgainPlayer();
    final AlwaysRollAgainPlayer player2 = new AlwaysRollAgainPlayer();

    final GameRunner runner = new GameRunner(new Player[]{player1, player2}, STANDARD_SET);

    final int testRepeats = 100;

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

}
