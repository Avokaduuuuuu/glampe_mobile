package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.bookingselection.resp.BookingSelectionResponse;
import com.avocado.glampe_mobile.model.entity.PriceFormat;

import java.util.List;

public class SelectionPriceAdapter extends RecyclerView.Adapter<SelectionPriceAdapter.SelectionPriceViewHolder> {

    private List<BookingSelectionResponse> bookingSelections;

    public SelectionPriceAdapter(List<BookingSelectionResponse> bookingSelections) {
        this.bookingSelections = bookingSelections;
    }

    @NonNull
    @Override
    public SelectionPriceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_selection_price, viewGroup, false);
        return new SelectionPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionPriceViewHolder holder, int i) {
        BookingSelectionResponse bookingSelectionResponse = bookingSelections.get(i);

        holder.tvSelectionName.setText(bookingSelectionResponse.getName());
        String price = PriceFormat.formatUsd(bookingSelectionResponse.getSelection().getPrice()) + " x " + bookingSelectionResponse.getQuantity() + " units";
        holder.tvPriceDetail.setText(price);
        holder.tvSubTotal.setText(PriceFormat.formatUsd(bookingSelectionResponse.getSelection().getPrice() * bookingSelectionResponse.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return bookingSelections.size();
    }

    public class SelectionPriceViewHolder extends RecyclerView.ViewHolder {
        TextView tvSelectionName, tvSubTotal, tvPriceDetail;

        public SelectionPriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSelectionName = itemView.findViewById(R.id.tvSelectionName);
            tvSubTotal = itemView.findViewById(R.id.tvSubtotal);
            tvPriceDetail = itemView.findViewById(R.id.tvPriceDetail);
        }
    }
}
