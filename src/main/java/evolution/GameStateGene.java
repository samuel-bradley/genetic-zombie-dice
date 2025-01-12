package evolution;

import game.DecisionRelevantGameState;
import io.jenetics.Gene;

public class GameStateGene implements Gene<Boolean, GameStateGene>, Comparable<GameStateGene> {

    private final DecisionRelevantGameState gameState;
    private final Boolean allele;

    public GameStateGene(DecisionRelevantGameState gameState, Boolean allele) {
        this.gameState = gameState;
        this.allele = allele;
    }

    public DecisionRelevantGameState getGameState() {
        return gameState;
    }

    @Override
    public Boolean allele() {
        return allele;
    }

    @Override
    public GameStateGene newInstance() {
        return new GameStateGene(gameState, Math.random() < 0.5);
    }

    @Override
    public GameStateGene newInstance(Boolean allele) {
        return new GameStateGene(gameState, allele);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int compareTo(GameStateGene other) {
        return this.allele.compareTo(other.allele);
    }
}
