package com.example.minesweeper.core.model;

/**
 * The main engine for the Minesweeper game.
 * <p>
 * This class encapsulates all the game logic, state management, and rules.
 * It acts as a facade for the UI layer, providing a simple API to interact
 * with the game (e.g., starting a new game, revealing a cell).
 * The UI should only communicate with this class to drive the game forward.
 */
public class GameEngine {

    private Field field;
    private GameState gameState;
    private int revealedCellsCount;

    /**
     * Starts a new game session.
     *
     * @param settings The configuration object containing the desired width,
     *                 height, and number of mines for the new game.
     */
    public void startNewGame(FieldSettings settings) {
        this.field = new Field(settings);
        this.gameState = GameState.IN_PROGRESS;
        this.revealedCellsCount = 0;
    }

    /**
     * Processes a player's move to reveal a cell at the specified coordinates.
     *
     * @param row The row-coordinate of the cell to reveal.
     * @param col The column-coordinate of the cell to reveal.
     */
    public void revealCell(int row, int col) {
        if (gameState != GameState.IN_PROGRESS)
            return;

        Cell cell = field.getCell(row, col);

        if (cell.isRevealed() || cell.isFlagged())
            return;

        // --- the main logic ---
        cell.setState(CellState.REVEALED);

        if (cell.hasMine()) {
            this.gameState = GameState.LOST;
            return;
        }

        ++revealedCellsCount;

        if (cell.getAdjacentMinesCount() == 0) {
            revealNeighbors(row, col);
        }

        checkWinCondition();
    }

    /**
     * Recursively reveals all neighboring cells of a given empty cell (a cell with 0 adjacent mines).
     * This method implements the "flood fill" algorithm.
     *
     * @param row The row-coordinate of the initial empty cell.
     * @param col The column-coordinate of the initial empty cell.
     */
    private void revealNeighbors(int row, int col) {
        // ...
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
    public Field getField() {
        return field;
    }

    // ... other methods like toggleFlag(), getGameState(), getField() ...
}