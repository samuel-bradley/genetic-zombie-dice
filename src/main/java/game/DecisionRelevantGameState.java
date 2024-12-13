package game;

import dice.Die;

import java.util.List;

public record DecisionRelevantGameState(List<Die> diceInCup, List<Die> diceOnTable, int blastsThisTurn, int brainsThisTurn) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        return new DecisionRelevantGameState(gameState.diceInCup(), gameState.diceOnTable(), gameState.blastsThisTurn(), gameState.brainsThisTurn());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecisionRelevantGameState that = (DecisionRelevantGameState) o;
        return diceInCup.equals(that.diceInCup) &&
                diceOnTable.equals(that.diceOnTable) &&
                blastsThisTurn == that.blastsThisTurn &&
                brainsThisTurn == that.brainsThisTurn;
    }

    @Override
    public int hashCode() {
        int result = diceInCup.hashCode();
        result = 31 * result + diceOnTable.hashCode();
        result = 31 * result + Integer.hashCode(blastsThisTurn);
        result = 31 * result + Integer.hashCode(brainsThisTurn);
        return result;
    }

}
