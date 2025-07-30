package com.example.minesweeper.core.model;

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
