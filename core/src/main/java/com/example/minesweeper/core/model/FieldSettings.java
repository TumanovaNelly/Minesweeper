package com.example.minesweeper.core.model;

import java.util.Locale;

/**
 * An immutable record that encapsulates the settings for a game field.
 * <p>
 * This record not only holds the configuration data but also validates it upon
 * creation, ensuring that no invalid game state can be configured. It calculates
 * the absolute number of mines from a given percentage.
 *
 * @param width        The width of the game field.
 * @param height       The height of the game field.
 * @param minesCount   The absolute number of mines on the field, calculated from a percentage.
 */
public record FieldSettings(int width, int height, int minesCount) {

    // Constants can be defined inside a record just like in a class.
    private static final int MIN_DIMENSION = 5;
    private static final int MAX_DIMENSION = 500;
    private static final int MIN_MINES = 1;

    /**
     * A compact canonical constructor used for validation and custom initialization logic.
     * This constructor is called automatically by the primary constructor.
     *
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public FieldSettings {
        // --- Validation ---
        if (width < MIN_DIMENSION || width > MAX_DIMENSION ||
                height < MIN_DIMENSION || height > MAX_DIMENSION) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "Field dimensions must be between %d and %d.",
                            MIN_DIMENSION, MAX_DIMENSION)
            );
        }

        if (minesCount < MIN_MINES) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "Calculated mines count (%d) is less than the minimum allowed (%d).",
                            minesCount, MIN_MINES)
            );
        }
        // Ensure there's at least one empty cell.
        if (minesCount >= (long) width * height) {
            throw new IllegalArgumentException("Mines count must be less than the total number of cells.");
        }
    }

    /**
     * A static factory method to create FieldSettings from a mine percentage.
     * This provides a clear and convenient way to instantiate the record
     * based on a different set of input parameters.
     *
     * @param width        The desired width of the field.
     * @param height       The desired height of the field.
     * @param minesPercent The desired percentage of mines (0-100).
     * @return A new, validated {@link FieldSettings} instance.
     */
    public static FieldSettings fromPercentage(int width, int height, int minesPercent) {
        if (minesPercent < 0 || minesPercent > 99) {
            throw new IllegalArgumentException("The percentage of mines must be between 0 and 99.");
        }

        int totalCells = width * height;
        int calculatedMines = totalCells * minesPercent / 100;

        return new FieldSettings(width, height, calculatedMines);
    }
}