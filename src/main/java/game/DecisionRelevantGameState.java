package game;

import dice.Die;
import dice.DieColour;

import java.util.*;

import static dice.DieFace.FOOTSTEPS;

public record DecisionRelevantGameState(List<DieColour> coloursInCup, List<DieColour> footstepsColours, int blastsThisTurn, int brainsThisTurn) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        // Sort the colours for ease of GameState comparison
        List<DieColour> footstepsColours = gameState.diceOnTable().stream()
                .filter(die -> die.getCurrentFace().equals(Optional.of(FOOTSTEPS)))
                .map(Die::getColour)
                .sorted(Comparator.comparingInt(DieColour::ordinal))
                .toList();
        List<DieColour> cupColours = gameState.diceInCup().stream()
                .map(Die::getColour)
                .sorted(Comparator.comparingInt(DieColour::ordinal))
                .toList();

        return new DecisionRelevantGameState(
                cupColours,
                footstepsColours,
                gameState.blastsThisTurn(),
                gameState.brainsThisTurn()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecisionRelevantGameState that = (DecisionRelevantGameState) o;
        return coloursInCup.equals(that.coloursInCup) &&
                footstepsColours.equals(that.footstepsColours) &&
                blastsThisTurn == that.blastsThisTurn &&
                brainsThisTurn == that.brainsThisTurn;
    }

    @Override
    public int hashCode() {
        int result = coloursInCup.hashCode();
        result = 31 * result + footstepsColours.hashCode();
        result = 31 * result + Integer.hashCode(blastsThisTurn);
        result = 31 * result + Integer.hashCode(brainsThisTurn);
        return result;
    }

    public static Set<DecisionRelevantGameState> generateAllStates(List<Die> dice, int maxBrainsThisTurn, int maxBlastsThisTurn) {
        Set<DecisionRelevantGameState> states = new HashSet<>();

        // Generate all possible allocations of dice into three categories:
        // - In cup
        // - On table with footprints
        // - On table with other faces
        List<int[]> allocations = generateDiceAllocations(dice.size());

        for (int[] allocation : allocations) {
            List<Die> diceInCup = new ArrayList<>();
            List<Die> footstepsDice = new ArrayList<>();

            for (int i = 0; i < allocation.length; i++) {
                if (allocation[i] == 0) {
                    diceInCup.add(dice.get(i));
                } else if (allocation[i] == 1) {
                    footstepsDice.add(dice.get(i));
                }
                // Ignore allocation[i] == 2 (other faces)
            }

            if (footstepsDice.size() > 3) continue; // Can have maximum 3 footprints on the table

            for (int blastsThisTurn = 0; blastsThisTurn <= maxBlastsThisTurn; blastsThisTurn++) {
                for (int brainsThisTurn = 0; brainsThisTurn <= maxBrainsThisTurn; brainsThisTurn++) {
                    // Sort the colours for ease of GameState comparison
                    List<DieColour> cupColours = extractColours(diceInCup).stream()
                            .sorted(Comparator.comparingInt(DieColour::ordinal))
                            .toList();
                    List<DieColour> footstepsColours = extractColours(footstepsDice).stream()
                            .sorted(Comparator.comparingInt(DieColour::ordinal))
                            .toList();;
                    states.add(new DecisionRelevantGameState(
                            cupColours,
                            footstepsColours,
                            blastsThisTurn,
                            brainsThisTurn
                    ));
                }
            }
        }
        return states;
    }

    private static List<int[]> generateDiceAllocations(int numberOfDice) {
        // Generate all valid allocations of the dice, each into three states (cup, footprints, neither)
        List<int[]> allocations = new ArrayList<>();
        int[] allocation = new int[numberOfDice];
        generateDiceAllocations(allocations, allocation, 0, numberOfDice);
        return allocations;
    }

    private static void generateDiceAllocations(List<int[]> allocations, int[] allocation, int index, int numberOfDice) {
        if (index == numberOfDice) {
            allocations.add(allocation.clone());
            return;
        }
        for (int i = 0; i < 3; i++) { // 0: cup, 1: footprints, 2: other faces
            allocation[index] = i;
            generateDiceAllocations(allocations, allocation, index + 1, numberOfDice);
        }
    }

    private static List<DieColour> extractColours(List<Die> dice) {
        return dice.stream().map(Die::getColour).toList();
    }

}
