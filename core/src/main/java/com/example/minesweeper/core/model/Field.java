package com.example.minesweeper.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the game field, containing a grid of {@link Cell} objects.
 * <p>
 * This class is responsible for the entire setup of the game board. Upon instantiation,
 * it initializes the grid based on the provided {@link FieldSettings}, places mines
 * randomly, and calculates the number of adjacent mines for each non-mine cell.
 * The initialization is performed in a memory-efficient, two-pass process.
 */
public final class Field {

    private final int width;
    private final int height;
    private final int minesCount;
    private final Cell[][] cells;

    /**
     * Constructs a new game field based on the provided settings.
     * The constructor orchestrates the entire setup process.
     *
     * @param settings The validated settings object for the game.
     */
    public Field(FieldSettings settings) {
        this.width = settings.getWidth();
        this.height = settings.getHeight();
        this.minesCount = settings.getMinesCount();
        this.cells = new Cell[height][width];

        initializeField();
    }

    /**
     * Initializes the field using a two-pass approach:
     * 1. Place mine cells at random locations.
     * 2. Fill the remaining empty spots with non-mine cells, calculating their adjacent mine counts.
     */
    private void initializeField() {
        placeMines();
        fillEmptyCells();
    }

    /**
     * First pass: Randomly places mine cells onto the grid.
     * The remaining cells in the grid are left as null to be filled in the second pass.
     */
    private void placeMines() {
        // Create a list of all possible positions on the field.
        int totalCells = width * height;
        List<Integer> positions = new ArrayList<>(totalCells);
        for (int i = 0; i < totalCells; i++) {
            positions.add(i);
        }

        // Shuffle the list to randomize positions.
        Collections.shuffle(positions);

        // Place mine cells at the first 'minesCount' positions from the shuffled list.
        for (int i = 0; i < minesCount; i++) {
            int pos = positions.get(i);
            int row = pos / width;
            int col = pos % width;
            cells[row][col] = Cell.createMine();
        }
    }

    /**
     * Second pass: Iterates through the grid to find empty (null) spots,
     * calculates their adjacent mine counts, and creates the corresponding empty cells.
     */
    private void fillEmptyCells() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // If a cell is not null, it's a mine placed in the first pass. Skip it.
                if (cells[row][col] != null && cells[row][col].hasMine())
                    continue;

                int adjacentMines = countAdjacentMines(row, col);
                cells[row][col] = Cell.createEmpty(adjacentMines);
            }
        }
    }

    /**
     * Counts the number of mines in the 8 cells surrounding a given coordinate.
     *
     * @param row The row-coordinate of the cell to check around.
     * @param col The column-coordinate of the cell to check around.
     * @return The total number of adjacent mines.
     */
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                // Skip the cell itself.
                if (dRow == 0 && dCol == 0)
                    continue;

                int neighborRow = row + dRow;
                int neighborCol = col + dCol;

                if (isValidCoordinate(neighborRow, neighborCol)) {
                    Cell neighbor = cells[neighborRow][neighborCol];
                    // A neighbor is a mine if it's not null (placed in the first pass) and has a mine.
                    if (neighbor != null && neighbor.hasMine()) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Checks if the given coordinates are within the bounds of the field.
     *
     * @param row The row-coordinate to check.
     * @param col The column-coordinate to check.
     * @return true if the coordinate is valid, false otherwise.
     */
    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    // _____ Public Getters _____

    /**
     * Retrieves the cell at the specified coordinates.
     *
     * @param row The row-coordinate of the cell.
     * @param col The column-coordinate of the cell.
     * @return The {@link Cell} at the given position.
     * @throws IllegalArgumentException if the coordinates are out of bounds.
     */
    public Cell getCell(int row, int col) {
        if (!isValidCoordinate(row, col))
            throw new IllegalArgumentException("Coordinates are out of bounds.");

        return cells[col][row];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinesCount() {
        return minesCount;
    }
}