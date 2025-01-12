package evolution;

import dice.Die;
import game.DecisionRelevantGameState;
import game.GameRunner;
import game.GameState;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import players.GameStatePlayer;
import players.Player;
import players.RandomPlayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Evolver {

    public static void main(String[] args) {
        // First generate game states
        List<Die> dice = Die.STANDARD_SET;
        System.out.println("Generating states");
        Set<DecisionRelevantGameState> gameStates = DecisionRelevantGameState.generateAllStates(dice, 12, 2);
        System.out.println("Generated " + gameStates.size() + " states");

        // Create a factory for the initial population
        Factory<Genotype<GameStateGene>> genotypeFactory = Genotype.of(
                new GameStateChromosome(
                        ISeq.of(
                                gameStates.stream()
                                        .map(state -> new GameStateGene(state, Math.random() < 0.5))
                                        .collect(Collectors.toList())
                        )
                )
        );

        Function<Genotype<GameStateGene>, Integer> fitnessFunction = Evolver::evaluateFitness;

        Engine<GameStateGene, Integer> engine = Engine.builder(fitnessFunction, genotypeFactory)
                .populationSize(1000)
                .alterers(
                        new Mutator<>(0.03),
                        new UniformCrossover<>(0.2)
                )
                .build();

        EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();

        // Run the optimization
        Genotype<GameStateGene> best = engine.stream()
                .limit(300) // Limit the number of generations
                .peek(statistics)
                .peek(r -> System.out.println(statistics))
                .collect(EvolutionResult.toBestGenotype());

        // Output the best strategy
        System.out.println("Best strategy:");
        System.out.println(best.chromosome().toString());
        System.out.println("Total wins for best strategy: " + evaluateFitness(best));
    }

    private static Integer evaluateFitness(Genotype<GameStateGene> genotype) {
        Map<DecisionRelevantGameState, Boolean> gameStateMap = genotype.chromosome().stream()
                .collect(Collectors.toMap(
                        GameStateGene::getGameState,
                        GameStateGene::allele
                ));
        Player gameStatePlayer = new GameStatePlayer(gameStateMap);
        List<Player> players = List.of(gameStatePlayer, new RandomPlayer());
        GameRunner gameRunner = new GameRunner(players, Die.STANDARD_SET);

        int totalWins = 0;
        int gamesRun = 0;

        while (gamesRun < 1000) {
            GameState finalState = gameRunner.runGame();
            if (finalState.winner().equals(Optional.of(gameStatePlayer))) ++totalWins;
            ++gamesRun;
        }

        return totalWins;
    }
}
