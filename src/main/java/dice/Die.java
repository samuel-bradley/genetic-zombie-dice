package dice;

import static dice.DieFace.*;

public enum Die {

    RED(new DieFace[]{BLAST, BLAST, BLAST, FOOTSTEPS, FOOTSTEPS, BRAIN}),
    YELLOW(new DieFace[]{BLAST, BLAST, FOOTSTEPS, FOOTSTEPS, BRAIN, BRAIN}),
    GREEN(new DieFace[]{BLAST, FOOTSTEPS, FOOTSTEPS, BRAIN, BRAIN, BRAIN});

    private final DieFace[] faces;

    Die(DieFace[] faces) {
        this.faces = faces;
    }

    public DieFace[] getFaces() {
        return faces;
    }
}
