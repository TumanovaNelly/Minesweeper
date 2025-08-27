package com.example.minesweeper.core.model;

import com.example.minesweeper.core.repository.IField;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * The main engine for the Minesweeper game.
 * <p>
 * This class encapsulates all game logic, state management, and rules. It acts as a facade
 * for the UI layer, providing a simple API to interact with the game. The engine implements
 * advanced logic, including a safe first move and a solver-assisted reveal mechanism.
 */
public class GameEngine {
    private IField field;
    private GameState gameState;
    private FieldSettings settings;
    private int revealedCellsCount;
    private boolean isFirstMove;

    /**
     * A private record to hold the counts of neighboring cells by their state.
     * Used by the internal solver logic.
     */
    private record StateAmounts(int hidden, int flagged) {}

    /**
     * Default constructor. Initializes the game in a non-started state.
     */
    public GameEngine() {
        this.gameState = GameState.NOT_STARTED;
    }

    /**
     * Prepares a new game session by storing settings and creating an initial, empty field.
     * The actual game field with mines is not generated until the first move is made.
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
     * Processes a player's click on a cell, triggering the main game logic loop.
     * <p>
     * If it's the first move, this action generates the actual game field with a safe zone.
     * For subsequent moves, it initiates a solver-like process that reveals cells and
     * deduces further moves based on the state of the board.
     *
     * @param row The row-coordinate of the cell.
     * @param col The column-coordinate of the cell.
     */
    public void handleCellClick(int row, int col) {
        if (gameState != GameState.IN_PROGRESS)
            return;

        Coordinate coordinate = new Coordinate(row, col);

        if (isFirstMove) {
            field = new Field(settings, coordinate);
            isFirstMove = false;
        }

        Cell cell = field.getCell(coordinate);
        if (cell.isRevealed()) {
            cell.setState(CellState.HIDDEN);
            --revealedCellsCount;
        }

        attemptChordReveal(coordinate);
        checkWinCondition();
    }

    /**
     * Initiates and manages the non-recursive, queue-based reveal process.
     * <p>
     * This method simulates an intelligent player. It starts by revealing the initial cell,
     * then iteratively analyzes the board, flagging obvious mines and revealing obvious
     * safe cells until no more logical moves can be deduced.
     *
     * @param coordinate The starting coordinate for the reveal process.
     */
    private void attemptChordReveal(Coordinate coordinate) {
        Queue<Coordinate> queue = new ArrayDeque<>();
        revealHiddenCell(coordinate, queue);

        while (!queue.isEmpty()) {
            Coordinate newCoordinate = queue.remove();
            Cell cell = field.getCell(newCoordinate);

            StateAmounts neighbourStates = countNeighborStates(newCoordinate);

            if (cell.getAdjacentMinesCount() == neighbourStates.flagged)
                revealHiddenNeighbours(newCoordinate, queue);
            else if (cell.getAdjacentMinesCount() == neighbourStates.flagged + neighbourStates.hidden)
                flaggedAllHiddenNeighbours(newCoordinate);
        }
    }

    /**
     * Reveals a single hidden cell and adds it to the processing queue.
     * This is the primary action for opening a cell.
     *
     * @param coordinate The coordinate of the cell to reveal.
     * @param queue      The queue to which the coordinate will be added for further processing.
     */
    private void revealHiddenCell(Coordinate coordinate, Queue<Coordinate> queue) {
        Cell cell = field.getCell(coordinate);

        if (!cell.isHidden())
            return;

        cell.setState(CellState.REVEALED);
        ++revealedCellsCount;

        if (cell.hasMine())
            this.gameState = GameState.LOST;

        queue.add(coordinate);
    }


    /**
     * Automatically flags all hidden neighbors of a given cell.
     * This is a solver action based on logical deduction.
     *
     * @param coordinate The coordinate of the cell whose neighbors will be flagged.
     */
    private void flaggedAllHiddenNeighbours(Coordinate coordinate) {
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0)
                    continue;

                Coordinate neighbourCoordinate =
                        new Coordinate(coordinate.row() + dRow, coordinate.col() + dCol);

                if (!field.isValidCoordinate(neighbourCoordinate))
                    continue;

                Cell cell = field.getCell(neighbourCoordinate);

                if (cell.isHidden()) {
                    cell.setState(CellState.FLAGGED);
                }
            }
        }
    }

    /**
     * Reveals all hidden, non-flagged neighbors of a given cell.
     * This is a solver action ("chord") based on logical deduction.
     *
     * @param coordinate The coordinate of the cell whose neighbors will be revealed.
     * @param queue      The processing queue to add newly revealed cells to.
     */
    private void revealHiddenNeighbours(Coordinate coordinate, Queue<Coordinate> queue) {
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0)
                    continue;

                Coordinate neighbourCoordinate =
                        new Coordinate(coordinate.row() + dRow, coordinate.col() + dCol);

                if (!field.isValidCoordinate(neighbourCoordinate))
                    continue;

                revealHiddenCell(neighbourCoordinate, queue);
            }
        }
    }

    /**
     * Counts the number of hidden and flagged neighbors around a given coordinate.
     *
     * @param coordinate The center coordinate.
     * @return A {@link StateAmounts} record containing the counts.
     */
    private StateAmounts countNeighborStates(Coordinate coordinate) {
        int hiddenAmount = 0;
        int flaggedAmount = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0)
                    continue;

                Coordinate neighbourCoordinate =
                        new Coordinate(coordinate.row() + dRow, coordinate.col() + dCol);

                if (!field.isValidCoordinate(neighbourCoordinate))
                    continue;

                switch (field.getCell(neighbourCoordinate).getState()) {
                    case HIDDEN -> ++hiddenAmount;
                    case FLAGGED -> ++flaggedAmount;
                }
            }
        }
        return new StateAmounts(hiddenAmount, flaggedAmount);
    }


    /**
     * Toggles a flag on a cell at the specified coordinates.
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