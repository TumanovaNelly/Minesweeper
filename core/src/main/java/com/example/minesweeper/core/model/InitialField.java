package com.example.minesweeper.core.model;

import com.example.minesweeper.core.repository.IField;

public class InitialField implements IField {

    // _____ Field Class Members _____

    private final int width;
    private final int height;
    private final int minesCount;
    private final Cell emptyCell = Cell.createEmpty(0);


    public InitialField(FieldSettings settings) {
        this.width = settings.width();
        this.height = settings.height();
        this.minesCount = settings.minesCount();
    }


    @Override
    public Cell getCell(Coordinate coordinate) {
        if (!isValidCoordinate(coordinate)) {
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        }

        emptyCell.setState(CellState.HIDDEN);
        return emptyCell;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMinesCount() {
        return minesCount;
    }
}
