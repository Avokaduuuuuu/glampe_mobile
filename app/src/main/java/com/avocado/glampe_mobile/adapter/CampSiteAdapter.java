package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.resp.CampSiteResponse;

import java.util.List;

public class CampSiteAdapter extends RecyclerView.Adapter<CampSiteAdapter.CampSiteViewHolder> {
    private final List<CampSiteResponse> campSiteResponses;

    public CampSiteAdapter(List<CampSiteResponse> campSiteResponses) {
        this.campSiteResponses = campSiteResponses;
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

    }

    @Override
    public int getItemCount() {
        return campSiteResponses.size();
    }

    public static class CampSiteViewHolder extends RecyclerView.ViewHolder {
        TextView txPlaceType, txName, txAddress, txPrice;
        public CampSiteViewHolder(@NonNull View view) {
            super(view);
            txPlaceType = view.findViewById(R.id.txPlaceType);
            txName = view.findViewById(R.id.txName);
            txAddress = view.findViewById(R.id.txAddress);
            txPrice = view.findViewById(R.id.txPrice);
        }
    }
}
