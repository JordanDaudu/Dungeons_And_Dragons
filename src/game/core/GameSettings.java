package game.core;

public class GameSettings {
    private final int players;
    private final int rows;
    private final int cols;

    public GameSettings(int players, int rows, int cols) {
        this.players = players;
        this.rows = rows;
        this.cols = cols;
    }

    public int getPlayers() {
        return players;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public static boolean checkSettings(GameSettings gameSettings) {
        if(gameSettings.getPlayers() < 1 || gameSettings.getPlayers() > 4)
            return false;
        if(gameSettings.getRows() < 10 || gameSettings.getRows() > 100)
            return false;
        if(gameSettings.getCols() < 10 || gameSettings.getCols() > 100)
            return false;
        return true;
    }
}
