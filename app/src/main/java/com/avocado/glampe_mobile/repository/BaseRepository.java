package com.avocado.glampe_mobile.repository;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.model.dto.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseRepository {
    protected <T> void executeCall(Call<ApiResponse<T>> call,
                                   MutableLiveData<T> successLiveData,
                                   MutableLiveData<String> errorLiveData) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> apiResponse = response.body();
                    if (apiResponse.getIsSuccess()) {
                        successLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }

                } else {
                    errorLiveData.setValue("Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable throwable) {
                errorLiveData.setValue("Network error: " + throwable.getMessage());
            }
        });
    }
}
