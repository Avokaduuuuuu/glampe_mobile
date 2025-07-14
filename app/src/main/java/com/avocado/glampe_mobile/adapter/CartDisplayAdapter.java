// CartDisplayAdapter.java
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
import com.avocado.glampe_mobile.di.CampSiteCartManager;

import com.avocado.glampe_mobile.model.entity.CartItem;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.model.entity.SelectedAddon;
import com.avocado.glampe_mobile.model.entity.SelectedCampType;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartDisplayAdapter extends RecyclerView.Adapter<CartDisplayAdapter.CartItemViewHolder> {

    public interface OnCartActionListener {
        void onRemoveItem(CartItem item);
        void onEditItem(CartItem item);
        void onViewCampsite(CartItem item);
    }

    private List<CartItem> cartItems;
    private Context context;
    private OnCartActionListener listener;
    private SimpleDateFormat dateFormatter;

    public CartDisplayAdapter(Context context) {
        this.context = context;
        this.cartItems = new ArrayList<>();
        this.dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    public void setOnCartActionListener(OnCartActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_campsite, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCampsite;
        private TextView tvCampsiteName, tvLocation, tvDates, tvNights;
        private TextView tvCampTypesSummary, tvAddonsSummary, tvTotalPrice;
        private MaterialButton btnViewCampsite;

        CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCampsite = itemView.findViewById(R.id.imgCampsite);
            tvCampsiteName = itemView.findViewById(R.id.tvCampsiteName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvNights = itemView.findViewById(R.id.tvNights);
            tvCampTypesSummary = itemView.findViewById(R.id.tvCampTypesSummary);
            tvAddonsSummary = itemView.findViewById(R.id.tvAddonsSummary);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnViewCampsite = itemView.findViewById(R.id.btnViewCampsite);
        }

        void bind(CartItem item) {
            // Basic info
            tvCampsiteName.setText(item.getCampsiteName());
            tvLocation.setText(item.getCampsiteAddress());

            // Dates
            if (item.getCheckInDate() != null && item.getCheckOutDate() != null) {
                String checkIn = dateFormatter.format(new Date(item.getCheckInDate()));
                String checkOut = dateFormatter.format(new Date(item.getCheckOutDate()));
                tvDates.setText(checkIn + " - " + checkOut);
                tvNights.setText(item.getNumberOfNights() + " đêm");
            } else {
                tvDates.setText("Chưa chọn ngày");
                tvNights.setText("");
            }

            // Camp types summary
            StringBuilder campTypesSummary = new StringBuilder();
            for (SelectedCampType campType : item.getSelectedCampTypes()) {
                if (campTypesSummary.length() > 0) campTypesSummary.append(", ");
                campTypesSummary.append(campType.getQuantity())
                        .append("x ")
                        .append(campType.getType());
            }

            if (campTypesSummary.length() > 0) {
                tvCampTypesSummary.setText(campTypesSummary.toString());
                tvCampTypesSummary.setVisibility(View.VISIBLE);
            } else {
                tvCampTypesSummary.setText("Chưa chọn loại lều");
                tvCampTypesSummary.setVisibility(View.VISIBLE);
            }

            // Addons summary
            if (!item.getSelectedAddons().isEmpty()) {
                StringBuilder addonsSummary = new StringBuilder();
                for (SelectedAddon addon : item.getSelectedAddons()) {
                    if (addonsSummary.length() > 0) addonsSummary.append(", ");
                    addonsSummary.append(addon.getQuantity())
                            .append("x ")
                            .append(addon.getName());
                }
                tvAddonsSummary.setText("Add-ons: " + addonsSummary.toString());
                tvAddonsSummary.setVisibility(View.VISIBLE);
            } else {
                tvAddonsSummary.setVisibility(View.GONE);
            }

            // Total price
            tvTotalPrice.setText(PriceFormat.formatUsd(item.getTotalAmount()));

            // Load campsite image (placeholder for now)
            if (item.getCampsiteImage() != null && !item.getCampsiteImage().isEmpty()) {
                // Use Glide or Picasso to load image
                // Glide.with(context).load(item.getCampsiteImage()).into(imgCampsite);
                Glide.with(itemView)
                        .load(item.getCampsiteImage())
                        .placeholder(R.drawable.place_holder_image)
                        .into(imgCampsite);
            } else {
                imgCampsite.setImageResource(R.drawable.place_holder_image);
            }

            // Click listeners
//            btnRemove.setOnClickListener(v -> {
//                if (listener != null) listener.onRemoveItem(item);
//            });
//
//            btnEdit.setOnClickListener(v -> {
//                if (listener != null) listener.onEditItem(item);
//            });

            btnViewCampsite.setOnClickListener(v -> {
                if (listener != null) listener.onViewCampsite(item);
            });
        }
    }
}

// SelectedCampType.java