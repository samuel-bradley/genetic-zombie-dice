package evolution;

import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

import java.util.stream.Collectors;

public class GameStateChromosome implements Chromosome<GameStateGene> {

    private final ISeq<GameStateGene> genes;

    public GameStateChromosome(ISeq<GameStateGene> genes) {
        this.genes = genes;
    }

    @Override
    public Chromosome<GameStateGene> newInstance(ISeq<GameStateGene> newGenes) {
        return new GameStateChromosome(newGenes);
    }

    @Override
    public Chromosome<GameStateGene> newInstance() {
        return new GameStateChromosome(genes.map(GameStateGene::newInstance));
    }

    @Override
    public GameStateGene get(int i) {
        return genes.get(i);
    }

    @Override
    public int length() {
        return genes.length();
    }

    @Override
    public String toString() {
        return stream().map(g -> String.format("%s - %s%n",
                g.getGameState().toString(),
                g.allele() ? "roll" : "stop"
        )).collect(Collectors.joining());
    }
}
