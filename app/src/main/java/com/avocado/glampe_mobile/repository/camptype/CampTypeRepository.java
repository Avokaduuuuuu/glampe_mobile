package com.avocado.glampe_mobile.repository.camptype;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.camptype.filter.CampTypeFilterParams;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.CampTypeService;

import java.util.List;

public class CampTypeRepository extends BaseRepository {
    private static CampTypeRepository campTypeRepository;
    private final CampTypeService campTypeService;

    public CampTypeRepository() {
        this.campTypeService = ApiServiceFactory.getInstance().getCampTypeService();
    }

    public static synchronized CampTypeRepository getInstance(){
        if (campTypeRepository == null) {
            campTypeRepository = new CampTypeRepository();
        }
        return campTypeRepository;
    }

    public void fetchAllCampTypes(CampTypeFilterParams params,
                                  MutableLiveData<List<CampTypeResponse>> successLiveData,
                                  MutableLiveData<String> errorMessage){
        executeCall(campTypeService.fetchAllCampTypes(params.toMap()), successLiveData, errorMessage);
    }
}
