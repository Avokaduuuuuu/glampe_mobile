package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.utility.resp.UtilityResponse;
import com.bumptech.glide.Glide;

import java.util.List;

public class UtilityAdapter extends RecyclerView.Adapter<UtilityAdapter.UtilityViewHolder> {

    List<UtilityResponse> utilities;


    public UtilityAdapter(List<UtilityResponse> utilities) {
        this.utilities = utilities;
    }

    @NonNull
    @Override
    public UtilityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_utility, viewGroup, false);
        return new UtilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UtilityViewHolder holder, int i) {
        UtilityResponse utilityResponse = utilities.get(i);

        holder.tvName.setText(utilityResponse.getName());
        Glide.with(holder.itemView.getContext())
                .load(utilityResponse.getImagePath())
                .placeholder(R.drawable.empty)
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return utilities.size();
    }

    public static class UtilityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;


        public UtilityViewHolder(@NonNull View view) {
            super(view);
            ivIcon = view.findViewById(R.id.ivIcon);
            tvName = view.findViewById(R.id.tvName);
        }
    }
}
