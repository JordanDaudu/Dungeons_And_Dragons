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

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj instanceof Position) {
            Position other = (Position) obj;
            return row == other.row && col == other.col;
        }
        return false;
    }

    public int hashCode() {
        return 31 * row + col;
    }

    public String toString() {
        return "(" + row + "," + col + ")";
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
