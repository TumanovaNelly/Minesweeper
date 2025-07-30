package com.example.minesweeper.ui.game.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minesweeper.core.model.Cell;
import com.example.minesweeper.core.model.Coordinate;
import com.example.minesweeper.core.repository.IField;
import com.example.minesweeper.databinding.ItemCellBinding;

/**
 * An adapter for the {@link RecyclerView} that displays the Minesweeper game field.
 * <p>
 * This class is responsible for creating the views for each cell, binding the state of a
 * {@link Cell} object to its corresponding view, and delegating user click events
 * to a listener, typically the hosting {@link com.example.minesweeper.ui.game.GameActivity}.
 */
public class CellAdapter extends RecyclerView.Adapter<CellAdapter.CellViewHolder> {

    private IField field;
    private final OnCellClickListener listener;

    /**
     * A callback interface to handle user interactions with cells in the grid.
     * The containing {@code Activity} or {@code Fragment} must implement this interface
     * to receive click and long-click events.
     */
    public interface OnCellClickListener {
        /**
         * Called when a cell is short-clicked.
         * @param row The row of the clicked cell.
         * @param col The column of the clicked cell.
         */
        void onCellClick(int row, int col);

        /**
         * Called when a cell is long-clicked.
         * @param row The row of the long-clicked cell.
         * @param col The column of the long-clicked cell.
         */
        void onCellLongClick(int row, int col);
    }

    /**
     * Constructs a new {@code CellAdapter}.
     *
     * @param listener The callback that will handle user interaction events.
     */
    public CellAdapter(OnCellClickListener listener) {
        this.listener = listener;
    }

    /**
     * Updates the data set of the adapter with a new game field and refreshes the entire {@link RecyclerView}.
     * <p>
     * Note: This method uses {@code notifyDataSetChanged()}, which is simple but inefficient for
     * partial updates. For better performance, especially with animations, consider implementing
     * {@link androidx.recyclerview.widget.DiffUtil} in the future.
     *
     * @param newField The new state of the game field, or null to clear the board.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateField(IField newField) {
        this.field = newField;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single cell item using the generated binding class.
        ItemCellBinding binding = ItemCellBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CellViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        if (field == null) return;

        // Calculate the 2D (row, col) coordinates from the adapter's 1D position.
        int row = position / field.getWidth();
        int col = position % field.getWidth();

        Cell cell = field.getCell(new Coordinate(row, col));
        holder.bind(cell);

        // Set click listeners on the root view of the ViewHolder.
        holder.itemView.setOnClickListener(v -> listener.onCellClick(row, col));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onCellLongClick(row, col);
            return true; // Consume the long click event.
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of cells in the field, or 0 if the field is not initialized.
        if (field == null) {
            return 0;
        }
        return field.getWidth() * field.getHeight();
    }

    /**
     * A {@code ViewHolder} that describes a cell item view and its place within the {@link RecyclerView}.
     * It holds a reference to the {@link ItemCellBinding} for type-safe and efficient view access,
     * avoiding repeated calls to {@code findViewById}.
     */
    public static class CellViewHolder extends RecyclerView.ViewHolder {
        private final ItemCellBinding binding;

        /**
         * Constructs a new {@code CellViewHolder}.
         * @param binding The binding object for the {@code item_cell.xml} layout.
         */
        public CellViewHolder(ItemCellBinding binding) {
            super(binding.getRoot()); // Pass the root view to the superclass constructor.
            this.binding = binding;
        }

        /**
         * Binds a {@link Cell} data object to this ViewHolder.
         * This method updates the appearance of the cell view (e.g., background color, text, icon)
         * based on the cell's current state.
         *
         * @param cell The cell data to display.
         */
        public void bind(Cell cell) {
            // Access views via the binding object for type safety and performance.
            binding.cellButton.setText("");

            switch (cell.getState()) {
                case HIDDEN:
                    binding.cellButton.setText("");
                    binding.cellButton.setBackgroundColor(Color.GRAY);
                    break;
                case FLAGGED:
                    binding.cellButton.setText("🚩"); // Using emoji for simplicity.
                    binding.cellButton.setBackgroundColor(Color.LTGRAY);
                    break;
                case REVEALED:
                    binding.cellButton.setBackgroundColor(Color.WHITE);
                    if (cell.hasMine()) {
                        binding.cellButton.setText("💣");
                    } else {
                        int count = cell.getAdjacentMinesCount();
                        if (count > 0) {
                            binding.cellButton.setText(String.valueOf(count));
                            // TODO: Set text color based on the number (e.g., 1=blue, 2=green).
                        } else {
                            binding.cellButton.setText(""); // Empty revealed cell.
                        }
                    }
                    break;
            }
        }
    }
}