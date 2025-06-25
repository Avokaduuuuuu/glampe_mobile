package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.camptype.filter.CampTypeFilterParams;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.repository.camptype.CampTypeRepository;

import java.util.List;

import lombok.Getter;

@Getter
public class CampTypeViewModel extends ViewModel {
    private final CampTypeRepository campTypeRepository;
    private final MutableLiveData<List<CampTypeResponse>> campTypes = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public CampTypeViewModel() {
        this.campTypeRepository = CampTypeRepository.getInstance();
    }

    public void fetchAllCampTypes(CampTypeFilterParams params){
        isLoading.setValue(true);
        campTypeRepository.fetchAllCampTypes(params, campTypes, errorMessage);
    }
}
