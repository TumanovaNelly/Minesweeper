package com.example.minesweeper.ui.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.minesweeper.core.model.Field;
import com.example.minesweeper.core.model.FieldSettings;
import com.example.minesweeper.core.model.GameEngine;
import com.example.minesweeper.core.model.GameState;
import com.example.minesweeper.core.repository.IField;

/**
 * ViewModel for the game screen ({@link GameActivity}).
 * <p>
 * This class holds the UI state and handles all user interactions related to the game.
 * It acts as a bridge between the UI (the View) and the business logic (the {@link GameEngine}).
 * Its lifecycle is managed by the Android Framework, allowing it to survive configuration
 * changes like screen rotations.
 */
public class GameViewModel extends ViewModel {

    private final GameEngine gameEngine = new GameEngine();

    // --- LiveData for UI State ---

    // Private MutableLiveData, which can be changed only within this ViewModel.
    private final MutableLiveData<IField> _fieldLiveData = new MutableLiveData<>();
    private final MutableLiveData<GameState> _gameStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> _minesCountLiveData = new MutableLiveData<>();
    // private final MutableLiveData<Long> _timerLiveData = new MutableLiveData<>();

    // Public, immutable LiveData exposed to the View for observation.
    // This prevents the View from directly modifying the state.

    /**
     * Exposes the current state of the game field to the UI.
     * @return A LiveData stream of the {@link Field} object.
     */
    public LiveData<IField> getFieldLiveData() {
        return _fieldLiveData;
    }

    /**
     * Exposes the current state of the game (e.g., IN_PROGRESS, WON, LOST).
     * @return A LiveData stream of the {@link GameState}.
     */
    public LiveData<GameState> getGameStateLiveData() {
        return _gameStateLiveData;
    }

    /**
     * Exposes the current number of mines to be displayed on the UI.
     * @return A LiveData stream of the mine count.
     */
    public LiveData<Integer> getMinesCountLiveData() {
        return _minesCountLiveData;
    }


    // --- User Action Handlers ---

    /**
     * Handles a request to start a new game, typically triggered by a button click in the View.
     * It initializes the {@link GameEngine} with new settings and updates the UI state.
     */
    public void onNewGameRequested() {
        // TODO: In the future, these settings will come from a settings repository/screen.
        FieldSettings settings = new FieldSettings(10, 10, 15);
        gameEngine.startNewGame(settings);

        // Push the initial state to the UI.
        _gameStateLiveData.postValue(gameEngine.getGameState());
        _minesCountLiveData.postValue(settings.minesCount());
        // The field is initially a "dummy" field for the user to make the first move.
        _fieldLiveData.postValue(gameEngine.getField());
    }

    /**
     * Handles a short click on a cell, delegating the action to the {@link GameEngine}.
     * After the move, it updates the LiveData to reflect the new state.
     *
     * @param row The row of the clicked cell.
     * @param col The column of the clicked cell.
     */
    public void onCellClicked(int row, int col) {
        if (gameEngine.getGameState() != GameState.IN_PROGRESS) return;

        gameEngine.revealCell(row, col);
        updateAllLiveData();
    }

    /**
     * Handles a long click on a cell, typically for flagging it as a mine.
     *
     * @param row The row of the long-clicked cell.
     * @param col The column of the long-clicked cell.
     */
    public void onCellLongClicked(int row, int col) {
        if (gameEngine.getGameState() != GameState.IN_PROGRESS) return;

        // TODO: Add a toggleFlag(row, col) method in GameEngine.
        // gameEngine.toggleFlag(row, col);
        updateAllLiveData();
    }

    /**
     * A helper method to push the current state from the {@link GameEngine}
     * to all relevant LiveData objects, ensuring the UI is synchronized.
     */
    private void updateAllLiveData() {
        _fieldLiveData.postValue(gameEngine.getField());
        _gameStateLiveData.postValue(gameEngine.getGameState());
        // TODO: Update the mine counter if it changes when flagging.
        // _minesCountLiveData.postValue(gameEngine.getRemainingMines());
    }
}