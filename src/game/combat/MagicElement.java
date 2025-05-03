package game.combat;

/**
 * Represents the elemental type used in magic-based combat.
 * Each element has strengths and weaknesses relative to the others.
 *
 * The elemental relationships are:
 * FIRE > ICE, ICE > LIGHTNING, LIGHTNING > ACID, ACID > FIRE.
 */
public enum MagicElement {
    FIRE, ICE, LIGHTNING, ACID;

    /**
     * Checks if the current element is stronger than the other element.
     * @param other The other MagicElement to compare against.
     * @return true if this element is stronger than the other, false otherwise.
     */
    public boolean isStrongerThan(MagicElement other) {
        return switch (this) {
            case FIRE -> other == ICE;
            case ICE -> other == LIGHTNING;
            case LIGHTNING -> other == ACID;
            case ACID -> other == FIRE;
            default -> false;
        };
    }

    /**
     * Checks if the current element is weaker than the other element.
     * This is determined by checking if the other element is stronger than this one.
     *
     * @param other The other MagicElement to compare against.
     * @return true if this element is weaker than the other, false otherwise.
     */
    public boolean isWeakerThan(MagicElement other) {
        return other.isStrongerThan(this);
    }

}