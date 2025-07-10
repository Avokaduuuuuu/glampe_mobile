package com.avocado.glampe_mobile.fragment;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.activity.MainActivity;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.user.req.UserCreateRequest;
import com.avocado.glampe_mobile.viewModel.UserViewModel;
import com.hbb20.CountryCodePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.NavigableMap;

public class CreateAccountFragment extends Fragment {

    private EditText etFirstName, etLastName, etEmail, etAddress, etPhone, etDateOfBirth;
    private ImageView btnBack;
    private Button btnSave;
    private CountryCodePicker ccp;

    private NavController navController;
    private UserViewModel userViewModel;
    private FrameLayout loadingOverlay;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
        setupCountryCodePicker();
        updateView();
    }
    private void initViews(View view) {
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        btnBack = view.findViewById(R.id.btn_back);
        btnSave = view.findViewById(R.id.btnSave);
        ccp = view.findViewById(R.id.ccp);
        navController = Navigation.findNavController(view);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void updateView() {
        if (getArguments() != null) {
            etEmail.setText(getArguments().getString("email"));
        }
    }

    private void createUser() {
        if (validateAllInputs()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            userViewModel.createUser(
                    UserCreateRequest.builder()
                            .firstName(etFirstName.getText().toString())
                            .lastName(etLastName.getText().toString())
                            .email(etEmail.getText().toString())
                            .phoneNumber(etPhone.getText().toString())
                            .address(etAddress.getText().toString())
                            .dob(LocalDate.parse(etDateOfBirth.getText().toString(), formatter))
                            .build()
            );
        }
    }

    private void observeCreateUser() {
        userViewModel.getCreateUserLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                loadingOverlay.setVisibility(View.VISIBLE);
            }
        });

        userViewModel.getCreatedUser().observe(getViewLifecycleOwner(), authUser -> {
            if (authUser != null) {
                loadingOverlay.setVisibility(View.GONE);
                AuthManager.saveAuthResponse(requireContext(), authUser);
                navigateToMain();
            }
        });

        userViewModel.getCreatedUserError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }

    private void setupCountryCodePicker() {
        // Register the EditText with CountryCodePicker
        ccp.registerCarrierNumberEditText(etPhone);

        // Set default country based on SIM or network (optional)
        ccp.setAutoDetectedCountry(true);

        // Set country change listener (optional)
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Toast.makeText(requireContext(),
                        "Country changed to: " + ccp.getSelectedCountryName(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Custom click listener for country selection (optional)
        ccp.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(android.app.Dialog dialog) {
                // Dialog opened - you can customize dialog here
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialog) {
                // Dialog closed
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialog) {
                // Dialog cancelled
            }
        });

        // Phone number validity listener
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    etPhone.setError(null);
                } else if (!etPhone.getText().toString().trim().isEmpty()) {
                    etPhone.setError("Invalid phone number for " + ccp.getSelectedCountryName());
                }
            }
        });
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {navController.popBackStack();});

        // Date picker for DOB
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSave.setOnClickListener(v -> {
            observeCreateUser();
            createUser();
        });

        // Phone number validation with TextWatcher
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // CCP automatically handles formatting
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Additional validation can be added here
                validatePhoneNumber();
            }
        });


        // Real-time validation for other fields
        etFirstName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateFirstName();
        });

        etLastName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateLastName();
        });

        etAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateAddress();
        });
    }

    private boolean validatePhoneNumber() {
        if (etPhone.getText().toString().trim().isEmpty()) {
            return true; // Don't show error for empty field until form submission
        }

        if (ccp.isValidFullNumber()) {
            etPhone.setError(null);
            return true;
        } else {
            etPhone.setError("Invalid phone number for " + ccp.getSelectedCountryName());
            return false;
        }
    }

    private boolean validateFirstName() {
        String firstName = etFirstName.getText().toString().trim();
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        } else if (firstName.length() < 2) {
            etFirstName.setError("First name must be at least 2 characters");
            return false;
        } else {
            etFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String lastName = etLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        } else if (lastName.length() < 2) {
            etLastName.setError("Last name must be at least 2 characters");
            return false;
        } else {
            etLastName.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setError("Address is required");
            return false;
        } else if (address.length() < 10) {
            etAddress.setError("Please enter a complete address");
            return false;
        } else {
            etAddress.setError(null);
            return true;
        }
    }

    private boolean validateDateOfBirth() {
        String dob = etDateOfBirth.getText().toString().trim();
        if (dob.isEmpty()) {
            etDateOfBirth.setError("Date of birth is required");
            return false;
        } else {
            etDateOfBirth.setError(null);
            return true;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etDateOfBirth.setText(selectedDate);
                    validateDateOfBirth(); // Validate after setting date
                },
                calendar.get(Calendar.YEAR) - 25, // Default to 25 years ago
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to today (18 years ago for minimum age)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        // Set min date to 100 years ago
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }



    private boolean validateAllInputs() {
        boolean isValid = true;

        // Validate all fields
        if (!validateFirstName()) isValid = false;
        if (!validateLastName()) isValid = false;
        if (!validateAddress()) isValid = false;
        if (!validateDateOfBirth()) isValid = false;

        // Phone validation using CCP
        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            isValid = false;
        } else if (!ccp.isValidFullNumber()) {
            etPhone.setError("Please enter a valid phone number for " + ccp.getSelectedCountryName());
            isValid = false;
        } else {
            etPhone.setError(null);
        }

        // Scroll to first error if any
        if (!isValid) {
            scrollToFirstError();
        }

        return isValid;
    }

    private void scrollToFirstError() {
        EditText[] errorViews = {etFirstName, etLastName, etEmail, etAddress, etPhone, etDateOfBirth};
        for (EditText view : errorViews) {
            if (view.getError() != null) {
                view.requestFocus();
                view.getParent().requestChildFocus(view, view);

                // Show soft keyboard for EditText
                view.postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager)
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200);
                break;
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
