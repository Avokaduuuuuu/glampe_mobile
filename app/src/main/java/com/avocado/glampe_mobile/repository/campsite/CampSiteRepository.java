package com.avocado.glampe_mobile.repository.campsite;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.PageResponse;
import com.avocado.glampe_mobile.model.dto.campsite.filter.CampSiteFilterParams;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.CampSiteService;

import retrofit2.http.Query;

public class CampSiteRepository extends BaseRepository {
    private static CampSiteRepository instance;
    private final CampSiteService campSiteService;

    public CampSiteRepository() {
        this.campSiteService = ApiServiceFactory.getInstance().getCampSiteService();
    }

    public static synchronized CampSiteRepository getInstance(){
        if (instance == null) {
            instance = new CampSiteRepository();
        }
        return instance;
    }

    public void getAllCampSites(CampSiteFilterParams params,
                                MutableLiveData<PageResponse<CampSiteResponse>> successLiveData,
                                MutableLiveData<String> errorLiveData){
        executeCall(campSiteService.fetchAllCampSite(params.toMap()), successLiveData, errorLiveData);
    }

    public void getById(Long id,
                        MutableLiveData<CampSiteResponse> successLiveData,
                        MutableLiveData<String> errorLiveData) {
        executeCall(campSiteService.fetchById(id), successLiveData, errorLiveData);
    }
}
