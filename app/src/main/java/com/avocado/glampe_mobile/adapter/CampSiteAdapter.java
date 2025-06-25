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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
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
            Log.d("Campsite's first image", campSiteResponse.getGalleries().get(0).getPath());
            Glide.with(holder.itemView.getContext())
                    .load("https://glampe-bucket.s3.ap-southeast-1.amazonaws.com/camp_sites/Pine%20Valley%20Campground/images/1750662865282_LlS4gw7H-441887555_122149963652188663_7815166877511652508_n.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250625T101127Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA6K5V7HCWSEK6MUGJ%2F20250625%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=7200&X-Amz-Signature=b0726b6addd79543e47a5d9668ec172398782a2fa3d529dc01b6a3939a9713e1")
                    .placeholder(R.drawable.place_holder_image)
                    .error(R.drawable.ic_broken_image) // Fallback if failed
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GLIDE_ERROR", "Failed to load image", e);
                            return false;
                        }


                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("GLIDE", "Image loaded successfully");
                            return false;
                        }
                    })
                    .into(holder.imageCampSite);
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
