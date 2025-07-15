package com.example.minesweeper.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the game field, containing a grid of {@link Cell} objects.
 * <p>
 * This class handles the entire setup of the game board. It uses a highly
 * efficient, two-pass initialization strategy with a temporary "neighbor map"
 * to ensure both high performance and a clean, immutable API for the {@link Cell} class.
 * It also implements a "safe start" feature.
 */
public final class Field {

    /**
     * An immutable record to represent coordinates on the field.
     * <p>
     * As a {@code record}, it automatically provides a canonical constructor,
     * public accessors ({@code row()}, {@code col()}), and correct implementations
     * of {@code equals()}, {@code hashCode()}, and {@code toString()}.
     * Using this record instead of separate int parameters improves type safety
     * and code clarity.
     */
    public record Coordinate(int row, int col) {}

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
        this.width = settings.width();
        this.height = settings.height();
        this.minesCount = settings.minesCount();
        this.cells = new Cell[height][width];

        initializeField(safeCoordinate);
    }


    /**
     * Initializes the field by first placing mines and pre-calculating neighbor counts,
     * then creating the empty cells.
     * @param safeCoordinate The coordinate that must be free of mines.
     */
    private void initializeField(Coordinate safeCoordinate) {
        int[][] neighborMap = new int[height][width];
        placeMinesAndUpdateNeighborMap(safeCoordinate, neighborMap);
        fillEmptyCells(neighborMap);
    }

    /**
     * Places mines randomly on the field, avoiding the safe zone, and simultaneously
     * populates the neighborMap with adjacent mine counts.
     * @param safeCoordinate The coordinate defining the center of the mine-free zone.
     * @param neighborMap A 2D array to be populated with counts of adjacent mines.
     */
    private void placeMinesAndUpdateNeighborMap(Coordinate safeCoordinate, int[][] neighborMap) {
        List<Integer> potentialPositions = getPotentialMinePositions(safeCoordinate);
        Collections.shuffle(potentialPositions);

        int minesToPlace = Math.min(minesCount, potentialPositions.size());
        for (int i = 0; i < minesToPlace; i++) {
            int pos = potentialPositions.get(i);
            int row = pos / width;
            int col = pos % width;

            // Place a mine cell in the main grid.
            cells[row][col] = Cell.createMine();
            updateNeighborMap(neighborMap, new Coordinate(row, col));
        }
    }

    /**
     * Creates a list of all valid positions for mine placement, excluding the forbidden zone.
     * @param safeCoordinate The center of the 3x3 forbidden zone.
     * @return A list of integer positions available for mine placement.
     */
    private List<Integer> getPotentialMinePositions(Coordinate safeCoordinate) {
        int totalCells = width * height;
        List<Integer> potentialPositions = new ArrayList<>(totalCells);
        Set<Integer> forbiddenPositions = getForbiddenPositions(safeCoordinate);

        for (int i = 0; i < totalCells; i++) {
            if (!forbiddenPositions.contains(i)) {
                potentialPositions.add(i);
            }
        }
        return potentialPositions;
    }

    /**
     * Calculates the set of forbidden positions (a 3x3 grid) for mine placement.
     * @param center The center coordinate of the forbidden zone.
     * @return A set of integer positions where mines cannot be placed.
     */
    private Set<Integer> getForbiddenPositions(Coordinate center) {
        Set<Integer> forbidden = new HashSet<>();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                Coordinate coordinate = new Coordinate(center.row() + dRow, center.col() + dCol);
                if (isValidCoordinate(coordinate)) {
                    forbidden.add(coordinate.row() * width + coordinate.col());
                }
            }
        }
        return forbidden;
    }

    /**
     * Increments the count for all 8 neighbors of a newly placed mine in the neighborMap.
     * @param neighborMap The map of neighbor counts to update.
     * @param mineCoordinate The coordinate of the newly placed mine.
     */
    private void updateNeighborMap(int[][] neighborMap, Coordinate mineCoordinate) {
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                // A mine does not count itself as a neighbor.
                if (dRow == 0 && dCol == 0) continue;

                Coordinate neighborCoordinate = new Coordinate(mineCoordinate.row() + dRow, mineCoordinate.col() + dCol);
                if (isValidCoordinate(neighborCoordinate)) {
                    ++neighborMap[neighborCoordinate.row()][neighborCoordinate.col()];
                }
            }
        }
    }

    /**
     * Fills the remaining empty (null) spots in the grid with non-mine cells,
     * using the pre-calculated counts from the neighborMap.
     * @param neighborMap A 2D array containing the final count of adjacent mines for each cell.
     */
    private void fillEmptyCells(int[][] neighborMap) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (cells[row][col] != null) {
                    // This cell is already a mine, skip it.
                    continue;
                }
                cells[row][col] = Cell.createEmpty(neighborMap[row][col]);
            }
        }
    }

    /**
     * Checks if the given coordinate is within the bounds of the field.
     *
     * @param coordinate The coordinate to check.
     * @return true if the coordinate is valid, false otherwise.
     */
    private boolean isValidCoordinate(Coordinate coordinate) {
        return coordinate.row() >= 0 && coordinate.row() < height &&
                coordinate.col() >= 0 && coordinate.col() < width;
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

        return cells[coordinate.row()][coordinate.col()];
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