package com.avocado.glampe_mobile.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.entity.CalendarDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(LocalDate date);
    }

    private List<CalendarDay> days = new ArrayList<>();
    private Set<LocalDate> selectedDates = new HashSet<>();
    private OnDayClickListener onDayClickListener;

    public CalendarAdapter(OnDayClickListener listener) {
        this.onDayClickListener = listener;
    }

    public void submitDays(List<CalendarDay> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }

    public void updateSelectedDates(Set<LocalDate> dates) {
        selectedDates.clear();
        selectedDates.addAll(dates);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
        }

        public void bind(CalendarDay day) {
            tvDay.setText(String.valueOf(day.getDate().getDayOfMonth()));

            Context context = itemView.getContext();

            // Set text color, alpha and enabled state based on day type
            if (!day.isCurrentMonth()) {
                // Days from previous/next month - light gray and disabled
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.light_gray_text));
                tvDay.setAlpha(0.3f);
                tvDay.setEnabled(false);
            } else if (day.isPast()) {
                // Past days in current month - disabled with strikethrough effect
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.light_gray_text));
                tvDay.setAlpha(0.5f);
                tvDay.setEnabled(false);
                // Optional: Add strikethrough effect
                tvDay.setPaintFlags(tvDay.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else if (day.isSelected()) {
                // Selected days - white text on dark background
                tvDay.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                tvDay.setAlpha(1f);
                tvDay.setEnabled(true);
                tvDay.setPaintFlags(tvDay.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else if (day.isToday()) {
                // Today - highlighted color
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                tvDay.setAlpha(1f);
                tvDay.setEnabled(true);
                tvDay.setPaintFlags(tvDay.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                // Normal future days in current month
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                tvDay.setAlpha(1f);
                tvDay.setEnabled(true);
                tvDay.setPaintFlags(tvDay.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Set background state
            tvDay.setSelected(day.isSelected());
            tvDay.setActivated(day.isInRange());

            // Click listener - only allow clicks on selectable days
            tvDay.setOnClickListener(v -> {
                if (day.isSelectable() && onDayClickListener != null) {
                    onDayClickListener.onDayClick(day.getDate());
                }
            });
        }
    }
}
