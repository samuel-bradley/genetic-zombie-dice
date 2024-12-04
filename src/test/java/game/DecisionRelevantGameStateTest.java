package game;

import dice.Die;
import dice.DieColour;
import dice.DieFace;
import org.junit.jupiter.api.Test;
import players.Player;
import players.RandomPlayer;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DecisionRelevantGameStateTest {

    @Test
    void constructsFromGameState() {
        // Create dice
        Die redDie = new Die(DieColour.RED, Optional.of(DieFace.BLAST));
        Die greenDie = new Die(DieColour.GREEN, Optional.empty());

        // Mock data for GameState
        Die[] diceInCup = {redDie};
        Die[] diceOnTable = {greenDie};
        Map<Player, Integer> playerScores = Map.of(
                new RandomPlayer(), 10,
                new RandomPlayer(), 15
        );
        Player currentPlayer = playerScores.keySet().stream().findFirst().get();
        int blastsThisTurn = 2;
        int brainsThisTurn = 3;

        // Create GameState
        GameState gameState = new GameState(diceInCup, diceOnTable, playerScores, currentPlayer, blastsThisTurn, brainsThisTurn);

        // Convert to DecisionRelevantGameState
        DecisionRelevantGameState decisionRelevantGameState = DecisionRelevantGameState.fromGameState(gameState);

        // Verify that only the relevant attributes are transferred
        assertArrayEquals(diceInCup, decisionRelevantGameState.diceInCup());
        assertArrayEquals(diceOnTable, decisionRelevantGameState.diceOnTable());
        assertEquals(blastsThisTurn, decisionRelevantGameState.blastsThisTurn());
        assertEquals(brainsThisTurn, decisionRelevantGameState.brainsThisTurn());
    }

    @Test
    void testEqualsAndHashCode() {
        Die die1 = new Die(DieColour.RED, Optional.of(DieFace.BLAST));
        Die die2 = new Die(DieColour.GREEN, Optional.of(DieFace.BRAIN));

        // Create two identical instances
        DecisionRelevantGameState state1 = new DecisionRelevantGameState(new Die[]{die1}, new Die[]{die2}, 2, 3);
        DecisionRelevantGameState state2 = new DecisionRelevantGameState(new Die[]{die1}, new Die[]{die2}, 2, 3);

        // Verify equality and hashCode
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());

        // Modify one attribute (diceOnTable) to ensure inequality
        DecisionRelevantGameState state3 = new DecisionRelevantGameState(new Die[]{die1}, new Die[]{die1}, 2, 3);
        assertNotEquals(state1, state3);
    }
}
