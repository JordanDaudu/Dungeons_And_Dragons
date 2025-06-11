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

    /**
     * Copy constructor to create a new GameSettings instance by copying another.
     *
     * @param other the GameSettings instance to copy from
     */
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

    /**
     * Gets the music volume setting.
     *
     * @return the music volume (0.0 to 1.0)
     */
    public float getMusicVolume() { return musicVolume; }

    /**
     * Gets the sound effects (SFX) volume setting.
     *
     * @return the SFX volume (0.0 to 1.0)
     */
    public float getSFXVolume() { return sfxVolume; }

    /**
     * Checks whether the HP bar is shown.
     *
     * @return true if the HP bar is shown; false otherwise
     */
    public boolean getShowHPBar() { return showHPBar; }

    /**
     * Checks whether player information is shown.
     *
     * @return true if player information is shown; false otherwise
     */
    public boolean getShowPlayerInformation() { return showPlayerInformation; }

    /**
     * Gets the selected tile color background theme.
     *
     * @return the selected TileColorBackgroundTheme
     */
    public TileColorBackgroundTheme getSelectedTheme() { return selectedTheme; }

    /**
     * Sets the music volume.
     *
     * @param musicVolume the music volume to set (0.0 to 1.0)
     */
    public void setMusicVolume(float musicVolume) {this.musicVolume = musicVolume;}

    /**
     * Sets the sound effects (SFX) volume.
     *
     * @param sfxVolume the SFX volume to set (0.0 to 1.0)
     */
    public void setSfxVolume(float sfxVolume) {this.sfxVolume = sfxVolume;}

    /**
     * Sets whether to show the HP bar.
     *
     * @param showHPBar true to show the HP bar; false to hide
     */
    public void setShowHPBar(boolean showHPBar) {this.showHPBar = showHPBar;}

    /**
     * Sets whether to show player information.
     *
     * @param showPlayerInformation true to show player info; false to hide
     */
    public void setShowPlayerInformation(boolean showPlayerInformation) {this.showPlayerInformation = showPlayerInformation;}

    /**
     * Sets the selected tile color background theme.
     *
     * @param selectedTheme the TileColorBackgroundTheme to set
     */
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
