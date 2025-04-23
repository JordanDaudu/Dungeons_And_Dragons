package game.map;

/**
 * Represents a position on a 2D grid with row and column coordinates.
 * Used for placing and tracking game entities on the map.
 */
public class Position {

    // Data Members
    private int row;
    private int col;

    // Methods
    /**
     * Constructs a new position with the specified row and column.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructs a new position as a copy of another position.
     *
     * @param other the position to copy
     */
    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    /**
     * Calculates the Manhattan distance from this position to another.
     *
     * @param other the target position
     * @return the distance between the two positions
     */
    public int distanceTo(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    /**
     * Checks if this position is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the other object is a Position with the same coordinates
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj instanceof Position other) {
            return row == other.row && col == other.col;
        }
        return false;
    }

    /**
     * Returns a hash code for this position.
     *
     * @return the hash code based on row and column
     */
    public int hashCode() {
        return java.util.Objects.hash(row, col);
    }

    /**
     * Returns a string representation of the position.
     *
     * @return the position in (row,col) format
     */
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    /**
     * Gets the row coordinate of this position.
     *
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate of this position.
     *
     * @return the column
     */
    public int getCol() {
        return col;
    }
}
