package com.example.minesweeper.core.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class FieldTest {

    // A dummy safe coordinate for tests where its specific location doesn't matter.
    private final Field.Coordinate DUMMY_SAFE_COORDINATE = new Field.Coordinate(0, 0);

    @Test
    public void constructor_placesCorrectNumberOfMines() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 15);

        // Act
        Field field = new Field(settings, DUMMY_SAFE_COORDINATE);

        // Assert
        long actualMinesCount = 0;
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (field.getCell(new Field.Coordinate(row, col)).hasMine()) {
                    actualMinesCount++;
                }
            }
        }
        assertEquals("The number of mines on the field should match the settings",
                settings.getMinesCount(), actualMinesCount);
    }

    @Test
    public void constructor_safeZone_doesNotContainMines() {
        // Arrange
        FieldSettings settings = new FieldSettings(5, 5, 16); // 16 mines on a 25-cell field
        Field.Coordinate safeCenter = new Field.Coordinate(2, 2);

        // Act: Create a field with a guaranteed safe zone around (2, 2)
        Field field = new Field(settings, safeCenter);

        // Assert: Check that the 3x3 area around the safe coordinate is clear of mines
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int row = safeCenter.row + dRow;
                int col = safeCenter.col + dCol;
                Cell cell = field.getCell(new Field.Coordinate(row, col));
                assertFalse(
                        String.format("Cell at safe zone (%d, %d) should not contain a mine", row, col),
                        cell.hasMine()
                );
            }
        }
    }

    @Test
    public void constructor_calculatesAdjacentMinesCorrectly() {
        // Arrange
        FieldSettings settings = new FieldSettings(20, 20, 50);

        // Act
        Field field = new Field(settings, DUMMY_SAFE_COORDINATE);

        // Assert
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Cell cell = field.getCell(new Field.Coordinate(row, col));
                if (!cell.hasMine()) {
                    int expectedAdjacentMines = countNeighborsManually(field, row, col);
                    assertEquals(
                            String.format("Cell at (%d, %d) has incorrect adjacent mine count.", row, col),
                            expectedAdjacentMines,
                            cell.getAdjacentMinesCount()
                    );
                }
            }
        }
    }

    /**
     * Helper method to manually count adjacent mines for verification.
     */
    private int countNeighborsManually(Field field, int row, int col) {
        int count = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;

                int nRow = row + dRow;
                int nCol = col + dCol;

                // Correct boundary check
                if (nRow >= 0 && nRow < field.getHeight() && nCol >= 0 && nCol < field.getWidth()) {
                    if (field.getCell(new Field.Coordinate(nRow, nCol)).hasMine()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCell_throwsException_forNegativeCoordinate() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 10);
        Field field = new Field(settings, DUMMY_SAFE_COORDINATE);

        // Act
        field.getCell(new Field.Coordinate(-1, 5));

        // Assert: JUnit expects an IllegalArgumentException to be thrown.
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCell_throwsException_forOutOfBoundsCoordinate() {
        // Arrange
        FieldSettings settings = new FieldSettings(10, 10, 10);
        Field field = new Field(settings, DUMMY_SAFE_COORDINATE);

        // Act
        field.getCell(new Field.Coordinate(5, 10)); // Height is 10, so max row index is 9.

        // Assert
    }
}