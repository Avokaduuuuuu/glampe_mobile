package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.CombinedBookingDetailResponse;
import com.avocado.glampe_mobile.model.entity.PriceFormat;

import java.math.BigDecimal;
import java.util.List;

public class CampTypePriceAdapter extends RecyclerView.Adapter<CampTypePriceAdapter.CampTypePriceViewHolder> {

    private List<CombinedBookingDetailResponse> combinedBookingDetailResponses;

    public CampTypePriceAdapter(List<CombinedBookingDetailResponse> combinedBookingDetailResponses) {
        this.combinedBookingDetailResponses = combinedBookingDetailResponses;
    }

    @NonNull
    @Override
    public CampTypePriceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_camp_type_price, viewGroup, false);
        return new CampTypePriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampTypePriceViewHolder holder, int i) {
        CombinedBookingDetailResponse combinedBookingDetailResponse = combinedBookingDetailResponses.get(i);

        holder.tvCampTypeName.setText(combinedBookingDetailResponse.getCampTypeResponse().getType());
        holder.tvPriceDetail.setText(combinedBookingDetailResponse.getQuantity() + " units");
        holder.tvSubTotal.setText(PriceFormat.formatUsd(combinedBookingDetailResponse.getTotalAmount().doubleValue()));
    }

    @Override
    public int getItemCount() {
        return combinedBookingDetailResponses.size();
    }

    public class CampTypePriceViewHolder extends RecyclerView.ViewHolder {

        TextView tvCampTypeName, tvPriceDetail, tvSubTotal;
        public CampTypePriceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCampTypeName = itemView.findViewById(R.id.tvCampTypeName);
            tvPriceDetail = itemView.findViewById(R.id.tvPriceDetail);
            tvSubTotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
