package com.example.minesweeper.core.model;

/**
 * Represents a single, immutable cell on the game field.
 * <p>
 * This class encapsulates the intrinsic properties of a cell (whether it contains a mine
 * and the number of adjacent mines) as well as its current, mutable state within the game
 * (e.g., HIDDEN, REVEALED, FLAGGED).
 * <p>
 * Instantiation is controlled via static factory methods {@link #createMine()} and
 * {@link #createEmpty(int)} to ensure that only valid cell configurations can be created.
 */
public final class Cell {
    private final boolean hasMine;
    private final int adjacentMinesCount;

    private CellState state;

    /**
     * Private constructor to enforce instantiation via static factory methods.
     *
     * @param hasMine            true if the cell contains a mine.
     * @param adjacentMinesCount the number of mines in the 8 neighboring cells.
     */
    private Cell(boolean hasMine, int adjacentMinesCount) {
        this.hasMine = hasMine;
        this.adjacentMinesCount = adjacentMinesCount;
        this.state = CellState.HIDDEN; // All cells start as hidden.
    }

    // _____ Static factory methods _____

    /**
     * Creates a new cell that contains a mine.
     * The adjacent mine count is set to a sentinel value of -1.
     *
     * @return A new, non-null Cell instance representing a mine.
     */
    static Cell createMine() {
        return new Cell(true, -1);
    }

    /**
     * Creates a new, empty (non-mine) cell with a specified number of adjacent mines.
     *
     * @param adjacentMinesCount The number of mines in neighboring cells.
     *                           Must be between 0 and 8, inclusive.
     * @return A new, non-null Cell instance representing an empty cell.
     * @throws IllegalArgumentException if adjacentMinesCount is not in the range [0, 8].
     */
    static Cell createEmpty(int adjacentMinesCount) {
        if (adjacentMinesCount < 0 || adjacentMinesCount > 8)
            throw new IllegalArgumentException("Adjacent mines count must be between 0 and 8.");

        return new Cell(false, adjacentMinesCount);
    }

    // _____ Public getters _____

    /**
     * @return true if the cell contains a mine, false otherwise.
     */
    public boolean hasMine() {
        return hasMine;
    }

    /**
     * @return The number of mines in the 8 adjacent cells.
     *         Returns -1 if this cell itself contains a mine.
     */
    public int getAdjacentMinesCount() {
        return adjacentMinesCount;
    }

    /**
     * @return The current state of the cell (HIDDEN, REVEALED, or FLAGGED).
     */
    public CellState getState() {
        return state;
    }

    // _____ Package-private Setters _____

    /**
     * Sets the new state for the cell. This method is package-private to ensure
     * that only the core game logic can modify a cell's state.
     *
     * @param newState The new state to set.
     */
    void setState(CellState newState) {
        this.state = newState;
    }

    // _____ Convenience Methods _____

    /**
     * A convenience method to check if the cell is currently revealed.
     * @return true if the cell's state is {@link CellState#REVEALED}.
     */
    public boolean isRevealed() {
        return state == CellState.REVEALED;
    }

    /**
     * A convenience method to check if the cell is currently flagged.
     * @return true if the cell's state is {@link CellState#FLAGGED}.
     */
    public boolean isFlagged() {
        return state == CellState.FLAGGED;
    }

}
