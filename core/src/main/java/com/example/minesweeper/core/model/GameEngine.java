package com.example.minesweeper.core.model;

import com.example.minesweeper.core.repository.IField;

/**
 * The main engine for the Minesweeper game.
 * <p>
 * This class encapsulates all the game logic, state management, and rules.
 * It acts as a facade for the UI layer, providing a simple API to interact
 * with the game (e.g., starting a new game, revealing a cell).
 * The UI should only communicate with this class to drive the game forward.
 */
public class GameEngine {
    private IField field;
    private GameState gameState;
    private FieldSettings settings;
    private int revealedCellsCount;
    private boolean isFirstMove;

    /**
     * Default constructor. Initializes the game in a non-started state.
     */
    public GameEngine() {
        this.gameState = GameState.NOT_STARTED;
    }

    /**
     * Prepares a new game session by storing settings and resetting the state.
     * The actual game field is not created until the first move is made.
     *
     * @param settings The configuration for the new game.
     */
    public void startNewGame(FieldSettings settings) {
        this.settings = settings;
        this.field = new InitialField(settings);
        this.gameState = GameState.IN_PROGRESS;
        this.revealedCellsCount = 0;
        this.isFirstMove = true;
    }

    /**
     * Processes a player's move to reveal a cell.
     * If it's the first move, it generates a new field with a safe zone
     * around the specified coordinate before proceeding.
     *
     * @param row The row-coordinate of the cell to reveal.
     * @param col The column-coordinate of the cell to reveal.
     */
    public void revealCell(int row, int col) {
        // On the first move, generate the field with a safe starting area.
        if (isFirstMove) {
            if (settings == null) {
                throw new IllegalStateException("Game has not been started. Call startNewGame() first.");
            }
            Coordinate safeCoordinate = new Coordinate(row, col);
            this.field = new Field(settings, safeCoordinate);
            this.isFirstMove = false;
        }

        if (gameState != GameState.IN_PROGRESS) {
            return; // Game is over, no more moves allowed.
        }

        Cell cell = field.getCell(new Coordinate(row, col));

        if (cell.isRevealed() || cell.isFlagged()) {
            return; // Cannot reveal an already revealed or flagged cell.
        }

        cell.setState(CellState.REVEALED);

        if (cell.hasMine()) {
            // This block will never be reached on the first move due to the safe start.
            this.gameState = GameState.LOST;
            return;
        }

        revealedCellsCount++;

        if (cell.getAdjacentMinesCount() == 0) {
            revealNeighbors(new Coordinate(row, col));
        }

        checkWinCondition();
    }

    /**
     * Recursively reveals all neighboring cells of a given empty cell.
     *
     * @param coordinate The coordinate of the initial empty cell.
     */
    private void revealNeighbors(Coordinate coordinate) {
        // ...
    }


    /**
     * Toggles a flag on a cell at the specified coordinates.
     * <p>
     * This method cycles through the following states:
     * <ul>
     *     <li>{@code HIDDEN} -> {@code FLAGGED}</li>
     *     <li>{@code FLAGGED} -> {@code HIDDEN}</li>
     * </ul>
     * No action is taken if the cell is already revealed.
     *
     * @param row The row-coordinate of the cell.
     * @param col The column-coordinate of the cell.
     */
    public void toggleFlag(int row, int col) {
        Cell cell = field.getCell(new Coordinate(row, col));
        switch (cell.getState()) {
            case FLAGGED -> cell.setState(CellState.HIDDEN);
            case HIDDEN -> cell.setState(CellState.FLAGGED);
        }
    }

    /**
     * Checks if the win condition has been met after a cell is revealed.
     * The game is won when all non-mine cells have been revealed.
     */
    private void checkWinCondition() {
        int totalNonMineCells = (field.getWidth() * field.getHeight()) - field.getMinesCount();
        if (revealedCellsCount == totalNonMineCells) {
            this.gameState = GameState.WON;
        }
    }

    // _____ Public Getters for UI _____

    /**
     * Returns the current state of the game.
     *
     * @return The current {@link GameState} (e.g., IN_PROGRESS, WON, LOST).
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns the current game field.
     *
     * @return The {@link Field} object, allowing the UI to access cells for rendering.
     *         Returns null if the game has not been started yet.
     */
    public IField getField() {
        return field;
    }
}