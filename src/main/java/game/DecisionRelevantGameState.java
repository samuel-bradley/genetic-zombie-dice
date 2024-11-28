package game;

import dice.Die;

public record DecisionRelevantGameState(Die[] diceInCup, Die[] diceOnTable) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        return new DecisionRelevantGameState(gameState.diceInCup(), gameState.diceOnTable());
    }

}
