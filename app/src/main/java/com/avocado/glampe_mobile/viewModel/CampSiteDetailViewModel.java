package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.repository.campsite.CampSiteRepository;

import lombok.Getter;

@Getter
public class CampSiteDetailViewModel extends ViewModel {

    private final CampSiteRepository campSiteRepository;

    private final MutableLiveData<CampSiteResponse> campSite = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CampSiteDetailViewModel() {
        this.campSiteRepository = CampSiteRepository.getInstance();
    }

    public void loadById(Long id){
        isLoading.setValue(true);
        campSiteRepository.getById(id, campSite, errorMessage);
    }


}
