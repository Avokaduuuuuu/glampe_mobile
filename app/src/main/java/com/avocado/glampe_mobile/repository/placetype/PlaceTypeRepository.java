package com.avocado.glampe_mobile.repository.placetype;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.PlaceTypeService;

import java.util.List;

public class PlaceTypeRepository extends BaseRepository {
    private static PlaceTypeRepository placeTypeRepository;
    private final PlaceTypeService placeTypeService;

    public PlaceTypeRepository() {
        this.placeTypeService = ApiServiceFactory.getInstance().getPlaceTypeService();
    }

    public static PlaceTypeRepository getInstance() {
        if (placeTypeRepository == null) {
            placeTypeRepository = new PlaceTypeRepository();
        }
        return placeTypeRepository;
    }

    public void fetchAll(MutableLiveData<List<PlaceTypeResponse>> successLiveData,
                         MutableLiveData<String> errorMessage) {
        executeCall(placeTypeService.fetchAllPlaceTypes(), successLiveData, errorMessage);
    }
}
