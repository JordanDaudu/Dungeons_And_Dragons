package game.engine;

/**
 * Encapsulates configuration settings for initializing the game, including
 * the number of players and the map dimensions (rows and columns).
 */
public class GameSettings {

    // Data Members
    private final int players;
    private final int rows;
    private final int cols;

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
