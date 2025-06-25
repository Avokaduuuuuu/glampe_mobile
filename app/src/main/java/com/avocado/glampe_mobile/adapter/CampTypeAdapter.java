package com.avocado.glampe_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
//import com.bumptech.glide.Glide;

import java.util.List;

public class CampTypeAdapter extends RecyclerView.Adapter<CampTypeAdapter.CampTypeViewHolder> {

    public interface OnCampTypeListener {
        void onQuantityChanged(CampTypeResponse campType, int newQuantity);
        void onCampTypeSelected(CampTypeResponse campType);
        void onViewDetailsClicked(CampTypeResponse campType);
    }

    private List<CampTypeResponse> campTypes;
    private Context context;
    private OnCampTypeListener listener;

    public CampTypeAdapter(List<CampTypeResponse> campTypes) {
        this.campTypes = campTypes;
    }

    public CampTypeAdapter(Context context, List<CampTypeResponse> campTypes) {
        this.context = context;
        this.campTypes = campTypes;
    }

    public void setOnCampTypeListener(OnCampTypeListener listener) {
        this.listener = listener;
    }

    public void updateData(List<CampTypeResponse> newCampTypes) {
        this.campTypes = newCampTypes;
        notifyDataSetChanged();
    }

    public void resetQuantities() {
        for (CampTypeResponse campType : campTypes) {
            campType.setSelectedQuantity(0);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CampTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_camp_type, viewGroup, false);
        return new CampTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampTypeViewHolder holder, int i) {
        CampTypeResponse campTypeResponse = campTypes.get(i);
        holder.bind(campTypeResponse);
    }

    @Override
    public int getItemCount() {
        return campTypes != null ? campTypes.size() : 0;
    }

    public class CampTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCampType, ivStatus;
        private TextView tvType, tvCampQuantity, tvPrice, tvWeekendRate, tvQuantity, tvViewDetails;
        private ImageButton btnDecrease, btnIncrease;
        private CampTypeResponse currentCampType;

        public CampTypeViewHolder(@NonNull View view) {
            super(view);
            initViews(view);
            setupClickListeners();
        }

        private void initViews(View view) {
            ivCampType = view.findViewById(R.id.ivCampType);
            tvType = view.findViewById(R.id.tvType);
            tvCampQuantity = view.findViewById(R.id.tvCampQuantity);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvWeekendRate = view.findViewById(R.id.tvWeekendRate);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            btnDecrease = view.findViewById(R.id.btnDecrease);
            btnIncrease = view.findViewById(R.id.btnIncrease);
        }

        private void setupClickListeners() {
            btnIncrease.setOnClickListener(v -> {
                if (currentCampType != null &&
                        currentCampType.getSelectedQuantity() < currentCampType.getQuantity() &&
                        !currentCampType.getIsDeleted()) {

                    int newQuantity = currentCampType.getSelectedQuantity() + 1;
                    currentCampType.setSelectedQuantity(newQuantity);
                    updateQuantityDisplay();

                    if (listener != null) {
                        listener.onQuantityChanged(currentCampType, newQuantity);
                    }
                }
            });

            btnDecrease.setOnClickListener(v -> {
                if (currentCampType != null && currentCampType.getSelectedQuantity() > 0) {
                    int newQuantity = currentCampType.getSelectedQuantity() - 1;
                    currentCampType.setSelectedQuantity(newQuantity);
                    updateQuantityDisplay();

                    if (listener != null) {
                        listener.onQuantityChanged(currentCampType, newQuantity);
                    }
                }
            });

            // Card click listener
            itemView.setOnClickListener(v -> {
                if (listener != null && currentCampType != null) {
                    listener.onCampTypeSelected(currentCampType);
                }
            });

            // View details click listener
            if (tvViewDetails != null) {
                tvViewDetails.setOnClickListener(v -> {
                    if (listener != null && currentCampType != null) {
                        listener.onViewDetailsClicked(currentCampType);
                    }
                });
            }
        }

        public void bind(CampTypeResponse campTypeResponse) {
            this.currentCampType = campTypeResponse;

            // Basic information
            tvType.setText(campTypeResponse.getType());
            tvCampQuantity.setText(itemView.getContext().getString(R.string.camp_quantity, campTypeResponse.getRemainQuantity()));

            // Prices
            tvPrice.setText(PriceFormat.formatUsd(campTypeResponse.getPrice()));

            if (campTypeResponse.getWeekendPrice() != null && campTypeResponse.getWeekendPrice() > 0) {
                tvWeekendRate.setText("Weekend: " + PriceFormat.formatUsd(campTypeResponse.getWeekendPrice()));
                tvWeekendRate.setVisibility(View.VISIBLE);
            } else {
                tvWeekendRate.setVisibility(View.GONE);
            }

            // Load image if available
//            if (ivCampType != null && campTypeResponse.getImage() != null && !campTypeResponse.getImage().isEmpty()) {
//                if (context != null) {
//                    Glide.with(context)
//                            .load(campTypeResponse.getImage())
//                            .placeholder(R.drawable.camp_site_example)
//                            .error(R.drawable.camp_site_example)
//                            .into(ivCampType);
//                } else {
//                    ivCampType.setImageResource(R.drawable.camp_site_example);
//                }
//            } else if (ivCampType != null) {
//                ivCampType.setImageResource(R.drawable.camp_site_example);
//            }

            // Status indicator
//            if (ivStatus != null) {
//                if (campTypeResponse.getStatus() != null && campTypeResponse.getStatus()) {
//                    ivStatus.setImageResource(R.drawable.ic_check_circle);
//                    ivStatus.setVisibility(View.VISIBLE);
//                } else {
//                    ivStatus.setImageResource(R.drawable.ic_cancel);
//                    ivStatus.setVisibility(View.VISIBLE);
//                }
//            }

            // Initialize quantity if null
            if (campTypeResponse.getSelectedQuantity() == null) {
                campTypeResponse.setSelectedQuantity(0);
            }

            updateQuantityDisplay();
        }

        private void updateQuantityDisplay() {
            if (currentCampType != null) {
                tvQuantity.setText(String.valueOf(currentCampType.getSelectedQuantity()));
                updateButtonStates();
            }
        }

        private void updateButtonStates() {
            if (currentCampType != null) {
                // Enable/disable decrease button
                btnDecrease.setEnabled(currentCampType.getSelectedQuantity() > 0);
                btnDecrease.setAlpha(currentCampType.getSelectedQuantity() > 0 ? 1.0f : 0.5f);

                // Enable/disable increase button
                boolean canIncrease = currentCampType.getSelectedQuantity() < currentCampType.getRemainQuantity()
                        && !currentCampType.getIsDeleted();
                btnIncrease.setEnabled(canIncrease);
                btnIncrease.setAlpha(canIncrease ? 1.0f : 0.5f);

                // Update card appearance based on availability
                itemView.setAlpha(!currentCampType.getIsDeleted() ? 1.0f : 0.6f);
            }
        }
    }
}