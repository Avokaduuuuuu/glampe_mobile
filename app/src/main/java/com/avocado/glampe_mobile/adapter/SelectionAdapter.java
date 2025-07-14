package com.avocado.glampe_mobile.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Map;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> {

    public void applySavedQuantities(Map<Long, Integer> savedQuantities) {
        Log.d("SelectionAdapter", "🔄 Applying saved quantities: " + savedQuantities.size() + " items");

        for (int i = 0; i < selections.size(); i++) {
            SelectionResponse selection = selections.get(i);
            Integer savedQuantity = savedQuantities.get(selection.getId());

            int newQuantity = (savedQuantity != null) ? savedQuantity : 0;
            selection.setSelectedQuantity(newQuantity);

            Log.d("SelectionAdapter", "   - " + selection.getName() + ": " + newQuantity);
        }

        notifyDataSetChanged();
        Log.d("SelectionAdapter", "✅ Saved quantities applied");
    }

    public interface OnSelectionListener{
        void onQuantityChanged(SelectionResponse selectionResponse, int newQuantity);
        void onSelectionSelected(SelectionResponse selectionResponse);
    }

    public SelectionAdapter(List<SelectionResponse> selections) {
        this.selections = selections;
    }

    public void setOnSelectionListener(OnSelectionListener listener) {
         this.listener = listener;
    }

    private List<SelectionResponse> selections;
    private OnSelectionListener listener;

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_selection, viewGroup, false);
        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int i) {
        SelectionResponse selection = selections.get(i);

        holder.bind(selection);
    }

    @Override
    public int getItemCount() {
        return selections.size();
    }

    public class SelectionViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView ivSelection;
        TextView tvSelectionName, tvSelectionDescription, tvQuantity, tvPrice;
        ImageButton btnIncrease, btnDecrease;

        SelectionResponse currentSelection;


        public SelectionViewHolder(@NonNull View view) {
            super(view);
            ivSelection = view.findViewById(R.id.ivSelection);
            tvSelectionDescription = view.findViewById(R.id.tvSelectionDescription);
            tvSelectionName = view.findViewById(R.id.tvSelectionName);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            tvPrice = view.findViewById(R.id.tvPrice);
            btnIncrease = view.findViewById(R.id.btnIncrease);
            btnDecrease = view.findViewById(R.id.btnDecrease);
            onClickListener();
        }

        private void onClickListener(){
            btnIncrease.setOnClickListener(v -> {
                if (currentSelection != null && !currentSelection.getIsDeleted()) {
                    int newQuantity = currentSelection.getSelectedQuantity() + 1;
                    currentSelection.setSelectedQuantity(newQuantity);
                    updateQuantityDisplay();

                    if (listener != null) {
                        listener.onQuantityChanged(currentSelection, newQuantity);
                    }
                }
            });

            btnDecrease.setOnClickListener(v -> {
                if (currentSelection != null && currentSelection.getSelectedQuantity() > 0) {
                    int newQuantity = currentSelection.getSelectedQuantity() - 1;
                    currentSelection.setSelectedQuantity(newQuantity);
                    updateQuantityDisplay();

                    if (listener != null) {
                        listener.onQuantityChanged(currentSelection, newQuantity);
                    }
                }
            });
        }

        private void updateQuantityDisplay() {
            if (currentSelection != null) {
                tvQuantity.setText(String.valueOf(currentSelection.getSelectedQuantity()));
                updateButtonStates();
            }
        }

        private void updateButtonStates() {
            if (currentSelection != null) {
                // Enable/disable decrease button
                btnDecrease.setEnabled(currentSelection.getSelectedQuantity() > 0);
                btnDecrease.setAlpha(currentSelection.getSelectedQuantity() > 0 ? 1.0f : 0.5f);

                // Enable/disable increase button
                boolean canIncrease = !currentSelection.getIsDeleted();
                btnIncrease.setEnabled(canIncrease);
                btnIncrease.setAlpha(canIncrease ? 1.0f : 0.5f);

                // Update card appearance based on availability
                itemView.setAlpha(!currentSelection.getIsDeleted() ? 1.0f : 0.6f);
            }
        }

        private void bind(SelectionResponse selection) {
            this.currentSelection = selection;

            tvSelectionName.setText(selection.getName());
            tvSelectionDescription.setText(selection.getDescription());
            tvPrice.setText(PriceFormat.formatUsd(selection.getPrice().doubleValue()));

            updateQuantityDisplay();
        }
    }

}
