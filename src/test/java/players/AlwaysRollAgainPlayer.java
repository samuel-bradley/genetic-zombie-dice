package players;

import game.DecisionRelevantGameState;

public class AlwaysRollAgainPlayer implements Player {
    @Override
    public boolean rollAgain(DecisionRelevantGameState gameState) {
        return true;
    }
}
