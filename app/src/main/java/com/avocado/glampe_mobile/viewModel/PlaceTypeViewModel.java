package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.repository.placetype.PlaceTypeRepository;

import java.util.List;

import lombok.Getter;

@Getter
public class PlaceTypeViewModel extends ViewModel {
    private final MutableLiveData<List<PlaceTypeResponse>> placeTypes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fetchAllLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> fetchAllError = new MutableLiveData<>();


    private PlaceTypeRepository placeTypeRepository;

    public PlaceTypeViewModel() {
        placeTypeRepository = PlaceTypeRepository.getInstance();
    }

    public void fetchAll() {
        fetchAllLoading.setValue(true);

        placeTypeRepository.fetchAll(placeTypes, fetchAllError);
    }
}
