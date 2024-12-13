package players;


import game.DecisionRelevantGameState;

public class RollAgainOncePlayer implements Player {
    private boolean hasRolled = false;
    private int timesAsked = 0;

    @Override
    public boolean rollAgain(DecisionRelevantGameState gameState) {
        ++timesAsked;
        if (hasRolled) {
            return false;
        } else {
            hasRolled = true;
            return true;
        }
    }

    public void reset() {
        timesAsked = 0;
        hasRolled = false;
    }

    public int getTimesAsked() {
        return timesAsked;
    }

}
