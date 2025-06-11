package com.avocado.glampe_mobile.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CampSiteDetailFragment extends Fragment implements TripCalendarBottomSheet.OnDatesSelectedListener {

    ImageButton imageButton;
    NavController navController;
    MaterialButton btnAbout, btnOption, btnReview;
    List<MaterialButton> buttons;
    ViewFlipper viewFlipper;
    Calendar checkInCalendar = Calendar.getInstance();
    Calendar checkOutCalendar = Calendar.getInstance();
    TextView tvCheckInDate, tvCheckOutDate;
    LinearLayout layoutDate;
    long checkInMillis = 0;
    long checkOutMillis = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camp_site_detail, container ,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        setUpButtons();
        setUpCalendar();


        imageButton.setOnClickListener(v -> navController.popBackStack());
    }

    private void initialize(View view){
        imageButton = view.findViewById(R.id.backButton);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnOption = view.findViewById(R.id.btnOption);
        btnReview = view.findViewById(R.id.btnReview);
        navController = Navigation.findNavController(view);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        tvCheckInDate = view.findViewById(R.id.check_in_date);
        layoutDate = view.findViewById(R.id.date_layout);
    }

    private void setUpButtons() {
        buttons = List.of(btnAbout, btnOption, btnReview);

        // Define what each button index maps to in the ViewFlipper
        for (int i = 0; i < buttons.size(); i++) {
            final int index = i;
            MaterialButton button = buttons.get(i);

            button.setOnClickListener(v -> {
                // Set view flipper to correct view
                viewFlipper.setDisplayedChild(index);


                // Update selected state for all buttons
                for (MaterialButton btn : buttons) {
                    btn.setSelected(false);
                }
                button.setSelected(true);
            });
        }

        // Set default selection
        btnAbout.performClick();
    }

    private void setUpCalendar(){
        layoutDate.setOnClickListener(v -> showTripCalendar());
    }

    private void showDatePicker(Calendar calendar, TextView textView) {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            textView.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showMaterialDatePicker(boolean isCheckIn) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isCheckIn ? "Select Check-in Date" : "Select Check-out Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());

            String formattedDate = sdf.format(selection);

            if (isCheckIn) {
                checkInMillis = selection;
                tvCheckInDate.setText(formattedDate);
            } else {
                checkOutMillis = selection;
                tvCheckOutDate.setText(formattedDate);
            }
        });

        datePicker.show(getParentFragmentManager(), isCheckIn ? "check_in_picker" : "check_out_picker");
    }

    private void showTripCalendar() {
        TripCalendarBottomSheet dialog = TripCalendarBottomSheet.newInstance(
                null, // Current check-in date
                null, // Current check-out date
                this  // OnDatesSelectedListener
        );

        dialog.show(getChildFragmentManager(), "TripCalendarDialog");
    }

    @Override
    public void onDatesSelected(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            updateBookingDates(startDate, endDate);
        }
    }

    private void updateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        String checkInDate = checkIn.format(formatter);
        String checkOutDate = checkOut.format(formatter);
        String date = checkInDate + " - " + checkOutDate;
        tvCheckInDate.setText(date);
    }

}
