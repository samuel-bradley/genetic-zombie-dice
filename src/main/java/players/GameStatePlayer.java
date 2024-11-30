package players;

import game.DecisionRelevantGameState;

import java.util.Map;

public class GameStatePlayer implements Player {

    private final Map<DecisionRelevantGameState, Boolean> gameStatesWithDecisions;

    GameStatePlayer(Map<DecisionRelevantGameState, Boolean> gameStatesWithDecisions) {
        this.gameStatesWithDecisions = gameStatesWithDecisions;
    }

    @Override
    public boolean rollAgain(DecisionRelevantGameState gameState) {
        Boolean rollAgain = gameStatesWithDecisions.get(gameState);
        if (rollAgain == null) {
            throw new IllegalArgumentException("Game state " + gameState.toString() + " not in cup");
        }
        return rollAgain;
    }
}
