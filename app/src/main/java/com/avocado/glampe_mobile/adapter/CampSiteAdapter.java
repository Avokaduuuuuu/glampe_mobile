package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.resp.CampSiteResponse;

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

        holder.txName.setText(campSiteResponse.getName());
        holder.txAddress.setText(campSiteResponse.getAddress());
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
        public CampSiteViewHolder(@NonNull View view) {
            super(view);
            txPlaceType = view.findViewById(R.id.txPlaceType);
            txName = view.findViewById(R.id.txName);
            txAddress = view.findViewById(R.id.txAddress);
            txPrice = view.findViewById(R.id.txPrice);
            cardCampSite = view.findViewById(R.id.cardCampSite);
        }
    }
}
