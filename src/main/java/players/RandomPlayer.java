package players;

import game.DecisionRelevantGameState;

public class RandomPlayer implements Player {
    @java.lang.Override
    public boolean rollAgain(DecisionRelevantGameState gameState) {
        return java.util.concurrent.ThreadLocalRandom.current().nextBoolean();
    }
}
