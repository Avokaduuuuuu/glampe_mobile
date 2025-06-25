package com.avocado.glampe_mobile.di;

import com.avocado.glampe_mobile.service.CampSiteService;
import com.avocado.glampe_mobile.service.CampTypeService;

public class ApiServiceFactory {
    private static ApiServiceFactory instance;
    private final RetrofitClient retrofitClient;


    public ApiServiceFactory() {
        this.retrofitClient = RetrofitClient.getInstance();
    }

    public static synchronized ApiServiceFactory getInstance(){
        if (instance == null) {
            instance = new ApiServiceFactory();
        }
        return instance;
    }

    public CampSiteService getCampSiteService(){
        return retrofitClient.getRetrofit().create(CampSiteService.class);
    }

    public CampTypeService getCampTypeService(){
        return retrofitClient.getRetrofit().create(CampTypeService.class);
    }
}
