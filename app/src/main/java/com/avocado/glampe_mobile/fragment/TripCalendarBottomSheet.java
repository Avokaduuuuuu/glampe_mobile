package com.avocado.glampe_mobile.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CalendarAdapter;
import com.avocado.glampe_mobile.adapter.CalendarItemDecoration;
import com.avocado.glampe_mobile.model.entity.CalendarDay;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class TripCalendarBottomSheet extends BottomSheetDialogFragment implements CalendarAdapter.OnDayClickListener {

    public interface OnDatesSelectedListener {
        void onDatesSelected(LocalDate startDate, LocalDate endDate);
    }

    public enum TabType { DATES, MONTHS, FLEXIBLE }
    public enum DurationType { EXACT, PLUS_ONE, PLUS_TWO }

    // Views
    private TextView tvMonthYear;
    private TextView btnSkip;
    private MaterialButton btnNext;
    private RecyclerView rvCalendar;
    private View dragHandle;
    private ImageView ivPrevMonth, ivNextMonth;

    private CalendarAdapter calendarAdapter;
    private LocalDate currentMonth;
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;
    private boolean isSelectingEndDate = false;
    private OnDatesSelectedListener onDatesSelectedListener;

    private boolean allowPastDates = false;
    private boolean allowTodaySelection = true;
    private int minDaysFromToday = 0; // Minimum days from today that can be selected

    public static TripCalendarBottomSheet newInstance(LocalDate startDate, LocalDate endDate,
                                                      OnDatesSelectedListener listener) {
        TripCalendarBottomSheet bottomSheet = new TripCalendarBottomSheet();
        bottomSheet.selectedStartDate = startDate;
        bottomSheet.selectedEndDate = endDate;
        bottomSheet.onDatesSelectedListener = listener;
        bottomSheet.currentMonth = LocalDate.now().withDayOfMonth(1);
        return bottomSheet;
    }

    // Additional factory method with configuration
    public static TripCalendarBottomSheet newInstance(LocalDate startDate, LocalDate endDate,
                                                      OnDatesSelectedListener listener,
                                                      boolean allowPastDates,
                                                      boolean allowTodaySelection,
                                                      int minDaysFromToday) {
        TripCalendarBottomSheet bottomSheet = newInstance(startDate, endDate, listener);
        bottomSheet.allowPastDates = allowPastDates;
        bottomSheet.allowTodaySelection = allowTodaySelection;
        bottomSheet.minDaysFromToday = minDaysFromToday;
        return bottomSheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setDraggable(true);

                // Set peek height to show a good portion of the calendar
                behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels * 3 / 4);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupCalendar();
        setupButtons();
        setupDragHandle();
        setupMonthNavigation();

        updateCalendar();
    }

    private void initViews(View view) {
        dragHandle = view.findViewById(R.id.drag_handle);
        tvMonthYear = view.findViewById(R.id.tv_month_year);
        ivPrevMonth = view.findViewById(R.id.iv_prev_month);
        ivNextMonth = view.findViewById(R.id.iv_next_month);
//        tabDates = view.findViewById(R.id.tab_dates);
//        tabMonths = view.findViewById(R.id.tab_months);
//        tabFlexible = view.findViewById(R.id.tab_flexible);
//        btnExactDates = view.findViewById(R.id.btn_exact_dates);
//        btnPlus1Day = view.findViewById(R.id.btn_plus_1_day);
//        btnPlus2Days = view.findViewById(R.id.btn_plus_2_days);
        btnSkip = view.findViewById(R.id.btn_skip);
        btnNext = view.findViewById(R.id.btn_next);
        rvCalendar = view.findViewById(R.id.rv_calendar);
    }

    private void setupDragHandle() {
        if (dragHandle != null) {
            dragHandle.setOnClickListener(v -> {
                // Toggle bottom sheet state when drag handle is clicked
                if (getDialog() instanceof BottomSheetDialog) {
                    BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                    View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    if (bottomSheetInternal != null) {
                        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                }
            });
        }
    }

    private void setupMonthNavigation() {
        if (ivPrevMonth != null) {
            ivPrevMonth.setOnClickListener(v -> {
                currentMonth = currentMonth.minusMonths(1);
                updateCalendar();
            });
        }

        if (ivNextMonth != null) {
            ivNextMonth.setOnClickListener(v -> {
                currentMonth = currentMonth.plusMonths(1);
                updateCalendar();
            });
        }
    }

    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(this);
        rvCalendar.setAdapter(calendarAdapter);
        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvCalendar.addItemDecoration(new CalendarItemDecoration());

        // Disable nested scrolling to work better with bottom sheet
        rvCalendar.setNestedScrollingEnabled(false);
    }

    private void setupButtons() {

        btnSkip.setOnClickListener(v -> {
            if (onDatesSelectedListener != null) {
                onDatesSelectedListener.onDatesSelected(null, null);
            }
            dismiss();
        });

        btnNext.setOnClickListener(v -> {
            if (onDatesSelectedListener != null && selectedStartDate != null) {
                onDatesSelectedListener.onDatesSelected(selectedStartDate, selectedEndDate);
            }
            dismiss();
        });
    }

    @Override
    public void onDayClick(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate minSelectableDate = today.plusDays(minDaysFromToday);

        // Check if the date is selectable based on configuration
        if (!allowPastDates) {
            if (!allowTodaySelection && (date.isBefore(minSelectableDate) || date.equals(today))) {
                // Optionally show a toast message
                // Toast.makeText(getContext(), "Cannot select this date", Toast.LENGTH_SHORT).show();
                return;
            } else if (date.isBefore(minSelectableDate)) {
                // Optionally show a toast message
                // Toast.makeText(getContext(), "Cannot select past dates", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (selectedStartDate == null) {
            // First selection
            selectedStartDate = date;
            isSelectingEndDate = true;
        } else if (isSelectingEndDate && date.isAfter(selectedStartDate)) {
            // Second selection (end date)
            selectedEndDate = date;
            isSelectingEndDate = false;
        } else {
            // Reset selection or select new start date
            selectedStartDate = date;
            selectedEndDate = null;
            isSelectingEndDate = true;
        }

        updateCalendar();
        updateNextButton();
    }

    private void updateCalendar() {
        LocalDate monthStart = currentMonth;
        LocalDate monthEnd = currentMonth.plusMonths(1).minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate minSelectableDate = today.plusDays(minDaysFromToday);

        // Get first day of week for the month (Sunday = 0, Monday = 1, etc.)
        int firstDayOfWeek = monthStart.getDayOfWeek().getValue() % 7;
        LocalDate calendarStart = monthStart.minusDays(firstDayOfWeek);

        // Generate exactly 6 weeks of days (42 days) to fill the calendar grid
        List<CalendarDay> days = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            LocalDate date = calendarStart.plusDays(i);

            // Check if this date is in the current month
            boolean isCurrentMonth = !date.isBefore(monthStart) && !date.isAfter(monthEnd);

            // Check if this date is selected
            boolean isSelected = date.equals(selectedStartDate) || date.equals(selectedEndDate);

            // Check if this date is in the range between selected dates
            boolean isInRange = selectedStartDate != null && selectedEndDate != null &&
                    date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate);

            // Check if this date is today
            boolean isToday = date.equals(today);

            // Check if this date should be disabled
            boolean isPast = false;
            if (!allowPastDates) {
                if (!allowTodaySelection) {
                    isPast = date.isBefore(minSelectableDate) || date.equals(today);
                } else {
                    isPast = date.isBefore(minSelectableDate);
                }
            }

            days.add(new CalendarDay(date, isCurrentMonth, isSelected, isInRange, isToday, isPast));
        }

        calendarAdapter.submitDays(days);

        // Update month/year display
        tvMonthYear.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    }

    private void updateNextButton() {
        btnNext.setEnabled(selectedStartDate != null);
    }


    private void resetTabBackground(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_tab_unselected);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void setSelectedTabBackground(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_tab_selected);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
    }


    private void resetDurationBackground(TextView btn) {
        btn.setBackgroundResource(R.drawable.bg_duration_unselected);
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void setSelectedDurationBackground(TextView btn) {
        btn.setBackgroundResource(R.drawable.bg_duration_selected);
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
    }

    // Add month navigation methods if needed
    public void navigateToNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }

    public void navigateToPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    // Override to handle back button behavior
    @Override
    public void onStart() {
        super.onStart();

        // Ensure bottom sheet is fully expanded when shown
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}

