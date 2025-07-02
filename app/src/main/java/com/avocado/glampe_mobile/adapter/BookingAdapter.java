package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingResponse> bookings;
    private OnItemClickListener listener;

    public BookingAdapter(List<BookingResponse> bookings, OnItemClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;

    }

    public interface OnItemClickListener {
        void onClick(BookingResponse bookingResponse);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_booking, viewGroup, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int i) {
        BookingResponse booking = bookings.get(i);

        // Use DateTimeFormatter for LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        holder.tvBookingId.setText(booking.getId().toString());
        holder.tvCampSiteName.setText(booking.getCampSite().getName());

        // Format LocalDateTime using DateTimeFormatter
        holder.tvCheckInAt.setText(booking.getCheckInAt().format(formatter));
        holder.tvCheckOutAt.setText(booking.getCheckOutAt().format(formatter));
        holder.tvCreatedAt.setText(booking.getCreatedAt().format(formatter));

        holder.tvStatus.setText(booking.getStatus());

        if (!booking.getCampSite().getGalleries().isEmpty()) {
            String imageUrl = booking.getCampSite().getGalleries().get(0).getPath();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.place_holder_image)
                    .into(holder.ivCampSite);
        }

        holder.cardBooking.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvCampSiteName, tvCheckInAt, tvCheckOutAt, tvCreatedAt, tvStatus;
        LinearLayout layoutStatus;
        ImageView ivCampSite;

        CardView cardBooking;

        public BookingViewHolder(@NonNull View view) {
            super(view);
            tvBookingId = view.findViewById(R.id.tvBookingId);
            tvCampSiteName = view.findViewById(R.id.tvCampSiteName);
            tvCheckInAt = view.findViewById(R.id.tvCheckInAt);
            tvCheckOutAt = view.findViewById(R.id.tvCheckOutAt);
            tvCreatedAt = view.findViewById(R.id.tvCreatedDate);
            tvStatus = view.findViewById(R.id.tvStatus);
            layoutStatus = view.findViewById(R.id.layoutStatus);
            ivCampSite = view.findViewById(R.id.ivCampSite);
            cardBooking = view.findViewById(R.id.cardBooking);
        }
    }

    public void updateData(List<BookingResponse> newBookings) {
        this.bookings.clear();
        this.bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

}
