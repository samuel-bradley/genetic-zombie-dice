package game;

import dice.Die;

import java.util.Arrays;

public record DecisionRelevantGameState(Die[] diceInCup, Die[] diceOnTable, int blastsThisTurn, int brainsThisTurn) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        return new DecisionRelevantGameState(gameState.diceInCup(), gameState.diceOnTable(), gameState.blastsThisTurn(), gameState.brainsThisTurn());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecisionRelevantGameState that = (DecisionRelevantGameState) o;
        return Arrays.equals(diceInCup, that.diceInCup) &&
                Arrays.equals(diceOnTable, that.diceOnTable) &&
                blastsThisTurn == that.blastsThisTurn &&
                brainsThisTurn == that.brainsThisTurn;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(diceInCup);
        result = 31 * result + Arrays.hashCode(diceOnTable);
        result = 31 * result + Integer.hashCode(blastsThisTurn);
        result = 31 * result + Integer.hashCode(brainsThisTurn);
        return result;
    }

}
