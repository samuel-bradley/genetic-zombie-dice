package game;

import java.util.Objects;
import java.util.Optional;

import static dice.DieFace.FOOTSTEPS;
import static game.GameOperations.*;

public class TurnRunner {

    public GameState runTurn(GameState gameState) {
        // Initial roll is mandatory
        do {
            int currentFootsteps = (int) gameState.diceOnTable().stream().filter((die) -> Objects.equals(die.getCurrentFace(), Optional.of(FOOTSTEPS))).count();
            if (currentFootsteps < 3) {
                gameState = drawAndRollDiceFromCup(gameState, 3 - currentFootsteps);
                gameState = rollFootprintsFromTable(gameState, currentFootsteps);
            } else {
                gameState = rollFootprintsFromTable(gameState, 3);
            }
            // Now turn is over if 3 or more blasts, or player has won, or doesn't want to roll again
        } while (!gameState.turnIsOver() && gameState.currentPlayer().rollAgain(DecisionRelevantGameState.fromGameState(gameState)));
        return addPlayerScore(gameState, gameState.currentPlayer(), gameState.brainsThisTurn());
    }

}
