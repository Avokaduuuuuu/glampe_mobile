package com.avocado.glampe_mobile.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.PageResponse;
import com.avocado.glampe_mobile.model.dto.booking.filter.BookingFilterParams;
import com.avocado.glampe_mobile.model.dto.booking.req.BookingRequest;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.avocado.glampe_mobile.repository.booking.BookingRepository;

import lombok.Getter;

@Getter
public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepository;
    private final MutableLiveData<PageResponse<BookingResponse>> bookings = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    private final MutableLiveData<Boolean> isLoadingAdd = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorAdd = new MutableLiveData<>();
    private final MutableLiveData<BookingResponse> booking = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoadingFetchById = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorFetchById = new MutableLiveData<>();
    private final MutableLiveData<BookingResponse> fetchedByIdBooking = new MutableLiveData<>();

    public BookingViewModel() {
        this.bookingRepository = BookingRepository.getInstance();
    }

    public void loadBookings(BookingFilterParams params){
        Log.d("BookingViewModel", "loadBookings called");

        isLoading.setValue(true);

        bookingRepository.fetchBookings(params, bookings, errorMessage);
    }

    public void addBooking(BookingRequest request){
        Log.d("BookingViewModel", "Adding Booking");

        isLoadingAdd.setValue(true);
        bookingRepository.addBooking(request, booking, errorAdd);
    }

    public void fetchBookingById(Long id) {
        Log.d("BookingViewModel", "Loading booking by id");

        isLoadingFetchById.setValue(true);
        bookingRepository.fetchBookingById(id, fetchedByIdBooking, errorFetchById);
    }
}
