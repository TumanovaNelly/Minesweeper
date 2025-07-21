package com.example.minesweeper.ui.game;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.minesweeper.core.model.GameState;
import com.example.minesweeper.databinding.ActivityGameBinding;
import com.example.minesweeper.ui.game.adapter.CellAdapter;

import java.util.Objects;

/**
 * The main screen for the Minesweeper game.
 * <p>
 * This Activity is responsible for displaying the game field and handling user input.
 * It follows the MVVM architecture pattern, where its primary role is to observe data
 * from the {@link GameViewModel} and delegate user actions to it.
 * It implements {@link CellAdapter.OnCellClickListener} to receive click events from the game grid.
 */
public class GameActivity extends AppCompatActivity implements CellAdapter.OnCellClickListener {

    private GameViewModel viewModel;
    private ActivityGameBinding binding;
    private CellAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setupRecyclerView();
        observeViewModel();

        // Start a new game only when the activity is first created, not on configuration changes.
        if (savedInstanceState == null) {
            viewModel.onNewGameRequested();
        }

        binding.restartButton.setOnClickListener(v -> viewModel.onNewGameRequested());
    }

    /**
     * Initializes the RecyclerView, its adapter, and its LayoutManager.
     */
    private void setupRecyclerView() {
        adapter = new CellAdapter(this);
        binding.gameFieldRecycler.setAdapter(adapter);
        // The span count will be dynamically updated when the field data is received.
        binding.gameFieldRecycler.setLayoutManager(new GridLayoutManager(this, 10));
    }

    /**
     * Subscribes to LiveData streams from the {@link GameViewModel} to update the UI
     * in a reactive manner.
     */
    @SuppressLint("DefaultLocale")
    private void observeViewModel() {
        // Observes changes in the game field and updates the adapter.
        viewModel.getFieldLiveData().observe(this, field -> {
            if (field != null) {
                // Dynamically set the number of columns in the grid.
                ((GridLayoutManager) Objects.requireNonNull(binding.gameFieldRecycler.getLayoutManager())).setSpanCount(field.getWidth());
                adapter.updateField(field);
            }
        });

        // Observes changes in the game state to show win/loss messages.
        viewModel.getGameStateLiveData().observe(this, gameState -> {
            if (gameState == null) return;

            if (gameState == GameState.WON) {
                Toast.makeText(this, "You Won!", Toast.LENGTH_LONG).show();
                // TODO: Update the smile icon ("Cool")
            } else if (gameState == GameState.LOST) {
                Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show();
                // TODO: Update the smile icon ("Dead")
            }
        });

        // Observes changes in the mine counter.
        viewModel.getMinesCountLiveData().observe(this, minesCount -> {
            if (minesCount != null) {
                // Formats the number to always have three digits (e.g., 015).
                binding.minesCountText.setText(String.format("%03d", minesCount));
            }
        });
    }

    /**
     * Callback method from {@link CellAdapter.OnCellClickListener}.
     * Delegates the short click event to the ViewModel.
     *
     * @param row The row of the clicked cell.
     * @param col The column of the clicked cell.
     */
    @Override
    public void onCellClick(int row, int col) {
        viewModel.onCellClicked(row, col);
    }

    /**
     * Callback method from {@link CellAdapter.OnCellClickListener}.
     * Delegates the long click event to the ViewModel.
     *
     * @param row The row of the long-clicked cell.
     * @param col The column of the long-clicked cell.
     */
    @Override
    public void onCellLongClick(int row, int col) {
        viewModel.onCellLongClicked(row, col);
    }
}