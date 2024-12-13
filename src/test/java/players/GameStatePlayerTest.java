package players;

import dice.Die;
import game.DecisionRelevantGameState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dice.DieColour.RED;
import static dice.DieColour.YELLOW;
import static dice.DieFace.BLAST;
import static dice.DieFace.BRAIN;
import static org.junit.jupiter.api.Assertions.*;

class GameStatePlayerTest {

    Die die1 = new Die(YELLOW, Optional.of(BRAIN));
    Die die2 = new Die(RED, Optional.of(BLAST));

    DecisionRelevantGameState gameState1 = new DecisionRelevantGameState(List.of(die1), List.of(die2), 1, 2);
    DecisionRelevantGameState gameState2 = new DecisionRelevantGameState(List.of(die2), List.of(die1), 2, 1);

    Map<DecisionRelevantGameState, Boolean> gameStatesWithDecisions = Map.of(
            gameState1, true,
            gameState2, false
    );

    GameStatePlayer player = new GameStatePlayer(gameStatesWithDecisions);

    @Test
    void rollsAgainCorrectlyGivenExactGameState() {
        assertTrue(player.rollAgain(gameState1));
        assertFalse(player.rollAgain(gameState2));
    }

    @Test
    void rollsAgainCorrectlyGivenEquivalentGameState() {
        assertTrue(player.rollAgain(new DecisionRelevantGameState(List.of(die1), List.of(die2), 1, 2)));
        assertFalse(player.rollAgain(new DecisionRelevantGameState(List.of(die2), List.of(die1), 2, 1)));
    }

    @Test
    void throwsExceptionGivenUnrecognisedGameState() {
        DecisionRelevantGameState unknownState = new DecisionRelevantGameState(List.of(), List.of(), 1, 2);
        assertThrows(IllegalArgumentException.class, () -> player.rollAgain(unknownState));
    }

}
