package game.combat;

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
}