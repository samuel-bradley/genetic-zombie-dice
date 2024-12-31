package game;

import dice.Die;
import dice.DieColour;
import dice.DieFace;

import java.util.*;

public record DecisionRelevantGameState(List<DieColour> coloursInCup, List<Die> diceOnTable, int blastsThisTurn, int brainsThisTurn) {

    public static DecisionRelevantGameState fromGameState(GameState gameState) {
        return new DecisionRelevantGameState(
                gameState.diceInCup().stream().map(Die::getColour).toList(),
                gameState.diceOnTable(),
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
                diceOnTable.equals(that.diceOnTable) &&
                blastsThisTurn == that.blastsThisTurn &&
                brainsThisTurn == that.brainsThisTurn;
    }

    @Override
    public int hashCode() {
        int result = coloursInCup.hashCode();
        result = 31 * result + diceOnTable.hashCode();
        result = 31 * result + Integer.hashCode(blastsThisTurn);
        result = 31 * result + Integer.hashCode(brainsThisTurn);
        return result;
    }

    public static Set<DecisionRelevantGameState> generateAllStates(List<Die> dice, int maxBrainsThisTurn, int maxBlastsThisTurn) {
        // Generate all combinations of dice in the cup
        List<List<Die>> cupCombinations = generateSubsets(dice);

        // Generate all combinations of these with max blasts and brains this turn
        Set<DecisionRelevantGameState> states = new HashSet<>();
        for (List<Die> diceInCup : cupCombinations) {
            // Calculate the complementary set for the table
            List<Die> diceOnTable = new ArrayList<>(dice);
            diceOnTable.removeAll(diceInCup);

            // Generate all face permutations for the dice on the table
            List<List<DieFace>> facePermutations = generateFacePermutations(diceOnTable.size());

            for (List<DieFace> faces : facePermutations) {
                // Assign faces to the dice on the table
                List<Die> tableWithFaces = new ArrayList<>();
                for (int i = 0; i < diceOnTable.size(); i++) {
                    Die die = diceOnTable.get(i);
                    tableWithFaces.add(new Die(die.getColour(), Optional.of(faces.get(i))));
                }

                for (int blastsThisTurn = 0; blastsThisTurn <= maxBlastsThisTurn; blastsThisTurn++) {
                    for (int brainsThisTurn = 0; brainsThisTurn <= maxBrainsThisTurn; brainsThisTurn++) {
                        states.add(new DecisionRelevantGameState(
                                extractColours(diceInCup),
                                tableWithFaces,
                                blastsThisTurn,
                                brainsThisTurn
                        ));
                    }
                }
            }
        }
        return states;
    }

    private static List<DieColour> extractColours(List<Die> dice) {
        return dice.stream().map(Die::getColour).toList();
    }

    private static <T> List<List<T>> generateSubsets(List<T> list) {
        List<List<T>> subsets = new ArrayList<>();
        int n = list.size();
        for (int i = 0; i < (1 << n); i++) { // 2^n subsets
            List<T> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    subset.add(list.get(j));
                }
            }
            subsets.add(subset);
        }
        return subsets;
    }

    private static List<List<DieFace>> generateFacePermutations(int numDice) {
        DieFace[] faces = DieFace.values();
        List<List<DieFace>> permutations = new ArrayList<>();

        int numFaces = faces.length;
        int totalCombinations = (int) Math.pow(numFaces, numDice);

        for (int i = 0; i < totalCombinations; i++) {
            List<DieFace> permutation = new ArrayList<>();
            int combination = i;

            // Generate the combination by assigning a face to each die
            for (int j = 0; j < numDice; j++) {
                permutation.add(faces[combination % numFaces]);
                combination /= numFaces;
            }

            permutations.add(permutation);
        }

        return permutations;
    }

}
