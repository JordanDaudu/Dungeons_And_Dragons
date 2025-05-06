package game.gui;

import java.awt.Color;

/**
 * Enum representing different background color themes for game tiles.
 * Each theme has an associated semi-transparent {@link Color}, or null for clear.
 */
public enum TileColorBackgroundTheme {

    /** Light blue sky theme with partial transparency. */
    SKY_BLUE(new Color(100, 150, 255, 100)),

    /** Sandy beige desert-like theme with partial transparency. */
    SANDY_BEIGE(new Color(220, 200, 160, 100)),

    /** Dark purple mystical theme with partial transparency. */
    DARK_PURPLE(new Color(80, 60, 120, 100)),

    /** Bright green nature theme with partial transparency. */
    GREEN(new Color(170, 240, 140,110)),

    /** Clear background (no color). */
    CLEAR(null); // Transparent

    /** The color associated with this background theme. */
    private final Color color;

    /**
     * Constructs a background theme with the given color.
     *
     * @param color the {@link Color} associated with the theme, or null for transparent
     */
    TileColorBackgroundTheme(Color color) {
        this.color = color;
    }

    /**
     * Returns the color associated with the theme.
     *
     * @return the theme {@link Color}, or null if transparent
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns a user-friendly name of the theme, replacing underscores with spaces.
     *
     * @return the theme name in lowercase with spaces
     */
    @Override
    public String toString() {
        // Friendly name
        return name().replace('_', ' ').toLowerCase();
    }
}
