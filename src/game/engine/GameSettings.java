package game.engine;

import game.gui.TileColorBackgroundTheme;

import java.io.Serializable;

/**
 * Encapsulates configuration settings for initializing the game, including
 * the number of players and the map dimensions (rows and columns).
 */
public class GameSettings implements Serializable {

    // Data Members
    private final int players;
    private final int rows;
    private final int cols;

    private float musicVolume = 0.6f;
    private float sfxVolume = 0.6f;
    private boolean showHPBar = true;
    private boolean showPlayerInformation = true;
    private TileColorBackgroundTheme selectedTheme = TileColorBackgroundTheme.CLEAR;

    // Methods
    /**
     * Constructs a GameSettings object with the specified player count and map dimensions.
     *
     * @param players the number of players in the game (must be between 1 and 4)
     * @param rows    the number of rows in the game map (must be between 10 and 100)
     * @param cols    the number of columns in the game map (must be between 10 and 100)
     */
    public GameSettings(int players, int rows, int cols) {
        this.players = players;
        this.rows = rows;
        this.cols = cols;
    }

    public GameSettings(GameSettings other) {
        this.players = other.players;
        this.rows = other.getRows();
        this.cols = other.getCols();
        this.musicVolume = other.musicVolume;
        this.sfxVolume = other.sfxVolume;
        this.showHPBar = other.showHPBar;
        this.showPlayerInformation = other.showPlayerInformation;
        this.selectedTheme = other.selectedTheme;
    }

    /**
     * Gets the number of players.
     *
     * @return the number of players
     */
    public int getPlayers() {
        return players;
    }

    /**
     * Gets the number of rows in the game map.
     *
     * @return the row count
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns in the game map.
     *
     * @return the column count
     */
    public int getCols() {
        return cols;
    }

    public float getMusicVolume() { return musicVolume; }
    public float getSFXVolume() { return sfxVolume; }
    public boolean getShowHPBar() { return showHPBar; }
    public boolean getShowPlayerInformation() { return showPlayerInformation; }
    public TileColorBackgroundTheme getSelectedTheme() { return selectedTheme; }

    public void setMusicVolume(float musicVolume) {this.musicVolume = musicVolume;}
    public void setSfxVolume(float sfxVolume) {this.sfxVolume = sfxVolume;}
    public void setShowHPBar(boolean showHPBar) {this.showHPBar = showHPBar;}
    public void setShowPlayerInformation(boolean showPlayerInformation) {this.showPlayerInformation = showPlayerInformation;}
    public void setSelectedTheme(TileColorBackgroundTheme selectedTheme) {this.selectedTheme = selectedTheme;}

    /**
     * Validates whether the provided game settings are within acceptable bounds.
     *
     * @param gameSettings the GameSettings instance to validate
     * @return true if all settings are within valid ranges; false otherwise
     */
    public static boolean checkSettings(GameSettings gameSettings) {
        if(gameSettings.getPlayers() < 1 || gameSettings.getPlayers() > 4)
            return false;
        if(gameSettings.getRows() < 10 || gameSettings.getRows() > 50)
            return false;
        if(gameSettings.getCols() < 10 || gameSettings.getCols() > 50)
            return false;
        return true;
    }

}
