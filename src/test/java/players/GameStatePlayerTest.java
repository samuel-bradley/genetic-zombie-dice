package players;

import game.DecisionRelevantGameState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static dice.DieColour.GREEN;
import static dice.DieColour.RED;
import static dice.DieColour.YELLOW;
import static org.junit.jupiter.api.Assertions.*;

class GameStatePlayerTest {

    DecisionRelevantGameState gameState1 = new DecisionRelevantGameState(List.of(YELLOW), List.of(RED), 1, 2);
    DecisionRelevantGameState gameState2 = new DecisionRelevantGameState(List.of(GREEN), List.of(RED), 2, 1);

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
        assertTrue(player.rollAgain(new DecisionRelevantGameState(List.of(YELLOW), List.of(RED), 1, 2)));
        assertFalse(player.rollAgain(new DecisionRelevantGameState(List.of(GREEN), List.of(RED), 2, 1)));
    }

    @Test
    void throwsExceptionGivenUnrecognisedGameState() {
        DecisionRelevantGameState unknownState = new DecisionRelevantGameState(List.of(), List.of(RED), 1, 2);
        assertThrows(IllegalArgumentException.class, () -> player.rollAgain(unknownState));
    }

}
