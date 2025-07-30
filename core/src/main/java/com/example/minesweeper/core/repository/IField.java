package com.example.minesweeper.core.repository;

import com.example.minesweeper.core.model.Cell;
import com.example.minesweeper.core.model.Coordinate;

public interface IField {
    int getWidth();
    int getHeight();
    int getMinesCount();

    /**
     * Retrieves the cell at the specified coordinate.
     *
     * @param coordinate The coordinate object of the cell.
     * @return The {@link Cell} at the given position.
     * @throws IllegalArgumentException if the coordinate is out of bounds.
     */
    Cell getCell(Coordinate coordinate);

    /**
     * Checks if the given coordinate is within the bounds of the field.
     *
     * @param coordinate The coordinate to check.
     * @return true if the coordinate is valid, false otherwise.
     */
    default boolean isValidCoordinate(Coordinate coordinate) {
        return coordinate.row() >= 0 && coordinate.row() < getHeight() &&
                coordinate.col() >= 0 && coordinate.col() < getWidth();
    }
}
