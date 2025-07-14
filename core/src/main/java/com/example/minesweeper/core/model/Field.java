package com.example.minesweeper.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the game field, containing a grid of {@link Cell} objects.
 * <p>
 * This class handles the entire setup of the game board, including a "safe start"
 * feature. It ensures that the first clicked cell and its immediate neighbors
 * will not contain mines.
 */
public final class Field {

    /**
     * A simple, immutable value object to represent coordinates on the field.
     */
    public static final class Coordinate {
        public final int row;
        public final int col;

        public Coordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return row == that.row && col == that.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    // _____ Field Class Members _____

    private final int width;
    private final int height;
    private final int minesCount;
    private final Cell[][] cells;

    /**
     * Constructs a new game field, ensuring the area around the provided
     * safe coordinate is free of mines.
     *
     * @param settings       The validated settings object for the game.
     * @param safeCoordinate The coordinate of the first click, which defines the center of the safe zone.
     */
    public Field(FieldSettings settings, Coordinate safeCoordinate) {
        this.width = settings.getWidth();
        this.height = settings.getHeight();
        this.minesCount = settings.getMinesCount();
        this.cells = new Cell[height][width];

        initializeField(safeCoordinate);
    }

    private void initializeField(Coordinate safeCoordinate) {
        placeMines(safeCoordinate);
        fillEmptyCells();
    }

    /**
     * First pass: Randomly places mine cells onto the grid, avoiding the
     * 3x3 area around the safe coordinate.
     */
    private void placeMines(Coordinate safeCoordinate) {
        int totalCells = width * height;
        List<Integer> potentialPositions = new ArrayList<>(totalCells);
        Set<Integer> forbiddenPositions = getForbiddenPositions(safeCoordinate);

        for (int i = 0; i < totalCells; i++) {
            if (!forbiddenPositions.contains(i)) {
                potentialPositions.add(i);
            }
        }

        Collections.shuffle(potentialPositions);

        int minesToPlace = Math.min(minesCount, potentialPositions.size());
        for (int i = 0; i < minesToPlace; i++) {
            int pos = potentialPositions.get(i);
            int row = pos / width;
            int col = pos % width;
            cells[row][col] = Cell.createMine();
        }
    }

    /**
     * Calculates the set of forbidden positions (a 3x3 grid) for mine placement.
     */
    private Set<Integer> getForbiddenPositions(Coordinate center) {
        Set<Integer> forbidden = new HashSet<>();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                Coordinate coordinate = new Coordinate(center.row + dRow, center.col + dCol);
                if (isValidCoordinate(coordinate)) {
                    forbidden.add(coordinate.row * width + coordinate.col);
                }
            }
        }
        return forbidden;
    }

    /**
     * Second pass: Fills the remaining empty (null) spots with non-mine cells
     * after calculating their adjacent mine counts.
     */
    private void fillEmptyCells() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (cells[row][col] != null) {
                    // This cell is already a mine, skip it.
                    continue;
                }
                int adjacentMines = countAdjacentMines(new Coordinate(row, col));
                cells[row][col] = Cell.createEmpty(adjacentMines);
            }
        }
    }

    /**
     * Counts the number of mines in the 8 cells surrounding a given coordinate.
     *
     * @param coordinate The coordinate of the cell to check around.
     * @return The total number of adjacent mines.
     */
    private int countAdjacentMines(Coordinate coordinate) {
        int count = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;

                Coordinate neighborCoordinate = new Coordinate(coordinate.row + dRow, coordinate.col + dCol);

                if (isValidCoordinate(neighborCoordinate)) {
                    Cell neighbor = cells[neighborCoordinate.row][neighborCoordinate.col];
                    // A neighbor is a mine if it was placed in the first pass (i.e., not null).
                    if (neighbor != null && neighbor.hasMine()) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Checks if the given coordinate is within the bounds of the field.
     *
     * @param coordinate The coordinate to check.
     * @return true if the coordinate is valid, false otherwise.
     */
    private boolean isValidCoordinate(Coordinate coordinate) {
        return coordinate.row >= 0 && coordinate.row < height &&
                coordinate.col >= 0 && coordinate.col < width;
    }

    // --- Public API ---

    /**
     * Retrieves the cell at the specified coordinate.
     *
     * @param coordinate The coordinate object of the cell.
     * @return The {@link Cell} at the given position.
     * @throws IllegalArgumentException if the coordinate is out of bounds.
     */
    public Cell getCell(Coordinate coordinate) {
        if (!isValidCoordinate(coordinate)) {
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        }

        return cells[coordinate.row][coordinate.col];
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