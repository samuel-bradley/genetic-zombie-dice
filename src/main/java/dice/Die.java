package dice;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static dice.DieColour.*;
import static dice.DieColour.RED;

public class Die {

    private final DieColour colour;
    private final Optional<DieFace> currentFace;
    private final Random random;

    public Die(DieColour colour, Optional<DieFace> currentFace) {
        this(colour, currentFace, ThreadLocalRandom.current());
    }

    public Die(DieColour colour, Optional<DieFace> currentFace, Random random) {
        this.colour = colour;
        this.currentFace = currentFace;
        this.random = random;
    }

    public DieColour getColour() {
        return colour;
    }

    public Optional<DieFace> getCurrentFace() {
        return currentFace;
    }

    public Die rolled() {
        final int faceIndex = random.nextInt(0, colour.getFaces().length);
        return new Die(colour, Optional.of(colour.getFaces()[faceIndex]), random);
    }

    public static final Die[] STANDARD_SET = {
            new Die(GREEN, Optional.empty()),
            new Die(GREEN, Optional.empty()),
            new Die(GREEN, Optional.empty()),
            new Die(GREEN, Optional.empty()),
            new Die(GREEN, Optional.empty()),
            new Die(GREEN, Optional.empty()),
            new Die(YELLOW, Optional.empty()),
            new Die(YELLOW, Optional.empty()),
            new Die(YELLOW, Optional.empty()),
            new Die(YELLOW, Optional.empty()),
            new Die(RED, Optional.empty()),
            new Die(RED, Optional.empty()),
            new Die(RED, Optional.empty())
    };
}
