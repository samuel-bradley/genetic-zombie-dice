package game;

import dice.Die;

import java.util.Arrays;

public record DecisionRelevantGameState(Die[] diceInCup, Die[] diceOnTable) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        return new DecisionRelevantGameState(gameState.diceInCup(), gameState.diceOnTable());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecisionRelevantGameState that = (DecisionRelevantGameState) o;
        return Arrays.equals(diceInCup, that.diceInCup) &&
                Arrays.equals(diceOnTable, that.diceOnTable);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(diceInCup);
        result = 31 * result + Arrays.hashCode(diceOnTable);
        return result;
    }

}
