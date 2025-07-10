package com.example.minesweeper.core.model;

import java.util.Locale;
import java.util.Objects;

/**
 * An immutable container class for storing field settings.
 */
public final class FieldSettings {

    // Minimum and maximum allowable field sizes and number of mines
    private static final int MIN_DIMENSION = 5;
    private static final int MAX_DIMENSION = 500;
    private static final int MIN_MINES = 1;

    private final int width;
    private final int height;
    private final int minesCount;

    /**
     * Creates a field settings object with input validation.
     *
     * @param width         the width of the field.
     * @param height        the height of the field.
     * @param minesPercent  percentage of mines in the field relative to the size of the field.
     * @throws IllegalArgumentException if the parameters do not match the rules of the game.
     */
    public FieldSettings(int width, int height, int minesPercent) {
        if (width < MIN_DIMENSION || width > MAX_DIMENSION ||
                height < MIN_DIMENSION || height > MAX_DIMENSION)
            throw new IllegalArgumentException(
                    String.format("Board dimensions must be between %d and %d",
                            MIN_DIMENSION, MAX_DIMENSION)
            );

        if (minesPercent < 0 || minesPercent > 100)
            throw new IllegalArgumentException(
                    "The percentage of mines should not be negative or greater than 100"
            );

        int cellsCount = width * height;
        this.minesCount = cellsCount * minesPercent / 100;

        if (minesCount < MIN_MINES)
            throw new IllegalArgumentException(
                    String.format("Mines count must be greater than %d. Specify a different percentage",
                            MIN_MINES)
            );

        this.width = width;
        this.height = height;
    }

    /* _____ Public getters _____ */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinesCount() {
        return minesCount;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,"fieldWidth=%d,\nfieldHeight=%d,\nminesCount=%d",
                width, height, minesCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldSettings that = (FieldSettings) o;
        return width == that.width &&
                height == that.height &&
                minesCount == that.minesCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, minesCount);
    }
}
