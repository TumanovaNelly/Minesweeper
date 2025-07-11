package com.example.minesweeper.core.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class FieldTest {

    @Test
    public void constructor_placesCorrectNumberOfMines() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 15);

        // Act
        Field field = new Field(settings);

        // Assert
        long actualMinesCount = 0;
        for (int row = 0; row < field.getHeight(); row++)
            for (int col = 0; col < field.getWidth(); col++)
                if (field.getCell(row, col).hasMine())
                    actualMinesCount++;

        assertEquals("The number of mines on the field should match the settings",
                settings.getMinesCount(), actualMinesCount);
    }

    @Test
    public void constructor_calculatesAdjacentMinesCorrectly() {
        // Arrange
        FieldSettings settings = new FieldSettings(20, 20, 50);

        // Act
        Field field = new Field(settings);

        // Assert
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Cell cell = field.getCell(row, col);
                int expectedAdjacentMines = cell.hasMine() ? -1 :
                        countNeighborsManually(field, row, col);
                assertEquals(
                        String.format("Cell at (%d, %d) has incorrect adjacent mine count.",
                                col, row),
                        expectedAdjacentMines,
                        cell.getAdjacentMinesCount()
                );
            }
        }
    }

    /**
     * An auxiliary method for manually counting neighbors in the test.
     */
    private int countNeighborsManually(Field field, int row, int col) {
        int count = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;

                int nRow = row + dRow;
                int nCol = col + dCol;

                if (nRow >= 0 && nRow < field.getWidth() && nCol >= 0 && nCol < field.getHeight()) {
                    if (field.getCell(nRow, nCol).hasMine()) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCell_throwsException_forNegativeX() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 10);
        Field field = new Field(settings);

        // Act
        field.getCell(-1, 5);

        // Assert
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCell_throwsException_forOutOfBoundsY() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 10);
        Field field = new Field(settings);

        // Act
        field.getCell(5, 10);

        // Assert
    }
}