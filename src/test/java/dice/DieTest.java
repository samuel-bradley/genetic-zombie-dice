package dice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static dice.DieColour.YELLOW;
import static dice.DieFace.*;
import static org.junit.jupiter.api.Assertions.*;

class DieTest {

    @Test
    void testAllFacesCanBeRolled() {
        DieColour colour = YELLOW;
        DieFace[] faces = colour.getFaces();

        // Random which will return each index in possible face indices in turn
        Random mockRandom = new Random() {
            private int counter = 0;

            @Override
            public int nextInt(int origin, int bound) {
                assertTrue(origin == 0 && bound == 6);
                return counter++;
            }
        };

        Die die = new Die(colour, Optional.empty(), mockRandom);

        // Keep track of rolls of each face
        Map<DieFace, Integer> faceCounts = new HashMap<>();
        for (DieFace face : faces) {
            faceCounts.put(face, 0);
        }

        // Roll the die 6 times - we should get each face once
        for (int i = 0; i < 6; i++) {
            die = die.rolled();
            DieFace rolledFace = die.getCurrentFace().orElseThrow();
            faceCounts.put(rolledFace, faceCounts.get(rolledFace) + 1);
        }

        // Yellow dice have two of each face, so we should get two of each type
        assertEquals(2, faceCounts.get(BLAST));
        assertEquals(2, faceCounts.get(BRAIN));
        assertEquals(2, faceCounts.get(FOOTSTEPS));
    }
}
