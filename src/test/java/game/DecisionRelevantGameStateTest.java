package game;

import dice.Die;
import dice.DieColour;
import dice.DieFace;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
}
