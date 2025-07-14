package com.avocado.glampe_mobile.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaceTypeAdapter extends RecyclerView.Adapter<PlaceTypeAdapter.PlaceTypeViewHolder> {

    private static final String TAG = "PlaceTypeAdapter";
    private List<PlaceTypeResponse> placeTypes;
    private OnPlaceTypeClickListener listener;
    private int selectedPosition = 0; // Default first item selected

    public interface OnPlaceTypeClickListener {
        void onClick(PlaceTypeResponse placeType);
    }

    public PlaceTypeAdapter(List<PlaceTypeResponse> placeTypes, OnPlaceTypeClickListener listener){
        this.placeTypes = placeTypes;
        this.listener = listener;

        if (!placeTypes.isEmpty()) {
            placeTypes.get(0).setSelected(true);
            Log.d(TAG, "Constructor: Set first item as selected");
        }
    }


    @NonNull
    @Override
    public PlaceTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_place_type, viewGroup, false);
        return new PlaceTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceTypeViewHolder holder, int i) {
        PlaceTypeResponse placeTypeResponse = placeTypes.get(i);
        holder.bind(placeTypeResponse, i);
    }

    @Override
    public int getItemCount() {
        return placeTypes.size();
    }

    public void setSelectedPosition(int position) {
        Log.d(TAG, "setSelectedPosition: from " + selectedPosition + " to " + position);

        // Deselect previous item
        if (selectedPosition >= 0 && selectedPosition < placeTypes.size()) {
            PlaceTypeResponse previousItem = placeTypes.get(selectedPosition);
            previousItem.setSelected(false);
            Log.d(TAG, "Deselected position: " + selectedPosition + ", name: " + previousItem.getName());
            notifyItemChanged(selectedPosition);
        }

        // Select new item
        selectedPosition = position;
        if (position >= 0 && position < placeTypes.size()) {
            PlaceTypeResponse newItem = placeTypes.get(position);
            newItem.setSelected(true);
            Log.d(TAG, "Selected position: " + position + ", name: " + newItem.getName());
            notifyItemChanged(position);
        }
    }

    public class PlaceTypeViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPlaceType;
        TextView tvPlaceTypeName;
        View selectionIndicator;

        public PlaceTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPlaceType = itemView.findViewById(R.id.ivPlaceType);
            tvPlaceTypeName = itemView.findViewById(R.id.tvPlaceTypeName);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);

            Log.d(TAG, "✅ ViewHolder created");

            // ✅ Test touch events first
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d(TAG, "🖐️ TOUCH detected at position: " + getAdapterPosition());
                    }
                    return false; // Don't consume the event
                }
            });

            // ✅ Your original click listener with more logs
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d(TAG, "🔥 CLICK detected! Position: " + position);

                    if (position != RecyclerView.NO_POSITION) {
                        Log.d(TAG, "✅ Valid position: " + position);
                        if (listener != null) {
                            Log.d(TAG, "✅ Listener exists, processing click...");
                            setSelectedPosition(position);
                            listener.onClick(placeTypes.get(position));
                            Log.d(TAG, "✅ Click processing complete");
                        } else {
                            Log.e(TAG, "❌ Listener is NULL!");
                        }
                    } else {
                        Log.e(TAG, "❌ Invalid position: " + position);
                    }
                }
            });
        }

        public void bind(PlaceTypeResponse category, int position) {
            tvPlaceTypeName.setText(category.getName());

            // ✅ Fix: Use proper image method (check your model for correct method name)
            Glide.with(itemView)
                    .load(category.getImage()) // or category.getImagePath() - check your model
                    .placeholder(R.drawable.ic_broken_image)
                    .into(ivPlaceType);

            // ✅ Fix: Proper null checking for Boolean field
            Boolean isSelected = category.getSelected();
            boolean selected = (isSelected != null && isSelected);

            Log.d(TAG, "bind position " + position + ": " + category.getName() +
                    ", selected=" + isSelected + ", actuallySelected=" + selected);

            if (selected) {
                tvPlaceTypeName.setTextColor(itemView.getContext().getColor(android.R.color.black));
                selectionIndicator.setVisibility(View.VISIBLE);
                Log.d(TAG, "Showing selection indicator for: " + category.getName());
            } else {
                tvPlaceTypeName.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                selectionIndicator.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Hiding selection indicator for: " + category.getName());
            }
        }
    }
}