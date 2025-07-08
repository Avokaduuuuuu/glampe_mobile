package com.avocado.glampe_mobile.repository.booking;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.PageResponse;
import com.avocado.glampe_mobile.model.dto.booking.filter.BookingFilterParams;
import com.avocado.glampe_mobile.model.dto.booking.req.BookingRequest;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.BookingService;

public class BookingRepository extends BaseRepository {
    private static BookingRepository bookingRepository;
    private final BookingService bookingService;

    public BookingRepository() {
        this.bookingService = ApiServiceFactory.getInstance().getBookingService();
    }

    public static synchronized BookingRepository getInstance() {
        if (bookingRepository == null) {
            bookingRepository = new BookingRepository();
        }
        return bookingRepository;
    }

    public void fetchBookings(
            BookingFilterParams params,
            MutableLiveData<PageResponse<BookingResponse>> successLiveData,
            MutableLiveData<String> errorMessage
    ){
        executeCall(bookingService.fetchBookings(params.getStatusList(),params.toMap()), successLiveData, errorMessage);
    }

    public void addBooking(
            BookingRequest request,
            MutableLiveData<BookingResponse> successLiveData,
            MutableLiveData<String> errorMessage
    ){
        executeCall(bookingService.addBooking(request), successLiveData, errorMessage);
    }

    public void fetchBookingById(
            Long id,
            MutableLiveData<BookingResponse> successLiveData,
            MutableLiveData<String> errorMessage
    ) {
        executeCall(bookingService.fetchBookingById(id), successLiveData, errorMessage);
    }
}

