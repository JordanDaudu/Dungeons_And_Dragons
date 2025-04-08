package game.map;

public class Position {

    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public int distanceTo(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    public boolean equals(Position other) {
        return row == other.row && col == other.col;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
