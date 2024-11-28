package players;

import game.DecisionRelevantGameState;

public interface Player {
    boolean rollAgain(DecisionRelevantGameState gameState);
}
