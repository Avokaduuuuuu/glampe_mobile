package com.avocado.glampe_mobile.repository;

import android.util.Log;

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
                        Log.d("BaseRepository", "LiveData instance: " + successLiveData.toString());
                        successLiveData.postValue(apiResponse.getData()); // ✅ FIXED
                    } else {
                        Log.d("BaseRepository", "Is Success false");
                        errorLiveData.postValue(apiResponse.getMessage());
                    }

                } else {
                    Log.d("BaseRepository", "response fail or body is null");
                    errorLiveData.postValue("Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable throwable) {
                Log.d("BaseRepository", "on Failure");
                errorLiveData.postValue("Network error: " + throwable.getMessage());
            }
        });
    }

}
