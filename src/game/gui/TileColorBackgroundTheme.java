package game.gui;

import java.awt.Color;

public enum TileColorBackgroundTheme {
    SKY_BLUE(new Color(100, 150, 255, 100)),
    SANDY_BEIGE(new Color(220, 200, 160, 100)),
    DARK_PURPLE(new Color(80, 60, 120, 100)),
    CLEAR(null); // Transparent

    private final Color color;

    TileColorBackgroundTheme(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        // Friendly name
        return name().replace('_', ' ').toLowerCase();
    }
}
