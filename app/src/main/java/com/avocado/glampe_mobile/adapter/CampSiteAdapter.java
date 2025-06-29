package com.avocado.glampe_mobile.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.util.Comparator;
import java.util.List;

public class CampSiteAdapter extends RecyclerView.Adapter<CampSiteAdapter.CampSiteViewHolder> {
    private final List<CampSiteResponse> campSiteResponses;
    private OnItemClickListener onItemClickListener;

    public CampSiteAdapter(List<CampSiteResponse> campSiteResponses, OnItemClickListener onClick) {
        this.campSiteResponses = campSiteResponses;
        this.onItemClickListener = onClick;
    }

    public interface OnItemClickListener {
        void onClick(CampSiteResponse campsite);
    }

    @NonNull
    @Override
    public CampSiteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_camp_site, viewGroup, false);
        return new CampSiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampSiteViewHolder holder, int i) {
        CampSiteResponse campSiteResponse = campSiteResponses.get(i);

        Double min = campSiteResponse.getCampTypes().stream()
                .min(Comparator.comparing(CampTypeResponse::getPrice))
                .map(CampTypeResponse::getPrice)
                .orElse(0.0);

        Double max = campSiteResponse.getCampTypes().stream()
                .max(Comparator.comparing(CampTypeResponse::getPrice))
                .map(CampTypeResponse::getPrice)
                .orElse(0.0);

        StringBuilder placeType = new StringBuilder();
        if (campSiteResponse.getPlaceTypes().size() >= 2) placeType.append(campSiteResponse.getPlaceTypes().get(0).getName()).append(" • ").append(campSiteResponse.getPlaceTypes().get(1).getName());
        else placeType.append(campSiteResponse.getPlaceTypes().get(0).getName());

        holder.txName.setText(campSiteResponse.getName());
        holder.txAddress.setText(campSiteResponse.getAddress());
        holder.txPrice.setText(holder.itemView.getContext().getString(R.string.price_per_night, PriceFormat.formatUsd(min), PriceFormat.formatUsd(max)));
        holder.txPlaceType.setText(placeType);
        if (!campSiteResponse.getGalleries().isEmpty()) {
            String url = campSiteResponse.getGalleries().get(0).getPath();
            Log.d("First Image", campSiteResponse.getGalleries().get(0).toString());
            if (url != null && !url.trim().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .load(url.trim())
                        .placeholder(R.drawable.place_holder_image)
                        .into(holder.imageCampSite);
            } else {
                Log.e("GLIDE", "Image URL is null or empty");
            }
        }

        holder.cardCampSite.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(campSiteResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return campSiteResponses.size();
    }

    public static class CampSiteViewHolder extends RecyclerView.ViewHolder {
        TextView txPlaceType, txName, txAddress, txPrice;
        CardView cardCampSite;
        ImageView imageCampSite;
        public CampSiteViewHolder(@NonNull View view) {
            super(view);
            txPlaceType = view.findViewById(R.id.txPlaceType);
            txName = view.findViewById(R.id.txName);
            txAddress = view.findViewById(R.id.txAddress);
            txPrice = view.findViewById(R.id.txPrice);
            cardCampSite = view.findViewById(R.id.cardCampSite);
            imageCampSite = view.findViewById(R.id.imageMainCampSite);
        }
    }

    public void updateCampSites(List<CampSiteResponse> newCampSites) {
        this.campSiteResponses.clear();
        this.campSiteResponses.addAll(newCampSites);
        notifyDataSetChanged();
    }
    public void addAll(List<CampSiteResponse> newItems) {
        int startPosition = this.campSiteResponses.size();
        this.campSiteResponses.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }
}
