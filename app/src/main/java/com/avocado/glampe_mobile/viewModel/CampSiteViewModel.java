package com.avocado.glampe_mobile.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.PageResponse;
import com.avocado.glampe_mobile.model.dto.campsite.filter.CampSiteFilterParams;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.repository.campsite.CampSiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;


@Getter
public class CampSiteViewModel extends ViewModel {
    private final CampSiteRepository campSiteRepository;
    private final MutableLiveData<PageResponse<CampSiteResponse>> campSites = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<List<CampSiteResponse>> campSiteList = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingMore = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> hasMorePages = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    private int currentPage = 0;
    private CampSiteFilterParams currentFilterParams;

    public CampSiteViewModel() {
        this.campSiteRepository = CampSiteRepository.getInstance();

        campSites.observeForever(this::handleApiResponse);
    }

    public void loadAllCampSites(CampSiteFilterParams params){
        isLoading.setValue(true);
        params.setCurrentPage(this.currentPage);
        currentFilterParams = params;

        if (currentPage == 0) {
            isLoading.setValue(true);
            isLoadingMore.setValue(false);
            hasMorePages.setValue(true);
            isLastPage.setValue(false);
            errorMessage.setValue(null);
        }
        campSiteRepository.getAllCampSites(currentFilterParams, campSites, errorMessage);
    }

    public void loadMoreCampSites(){
        Log.d("CampSiteViewModel", "loadMoreCampSites - Page: " + (currentPage + 1));

        Boolean isCurrentLoading = isLoading.getValue();
        Boolean isCurrentLoadingMore = isLoadingMore.getValue();
        Boolean hasMore = hasMorePages.getValue();

        if ((isCurrentLoading != null && isCurrentLoading) || (isCurrentLoadingMore != null && isCurrentLoadingMore) || (hasMore != null && !hasMore)) {
            Log.d("CampSiteViewModel", "loadMoreCampSites - Skipping: already loading or no more pages");
            return;
        }

        currentPage++;
        currentFilterParams.setCurrentPage(currentPage);

        loadAllCampSites(currentFilterParams);
    }

    private void handleApiResponse(PageResponse<CampSiteResponse> pageResponse) {
        if (pageResponse == null) return;

        Log.d("CampSiteViewModel", "handleApiResponse - Page: " + pageResponse.getCurrentPage() +
                ", Items: " + pageResponse.getRecords().size() +
                ", Total Pages: " + pageResponse.getTotalPages());

        // Update loading states
        isLoading.postValue(false);
        isLoadingMore.setValue(false);


        // Update camp site list
        List<CampSiteResponse> currentList = campSiteList.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        if (pageResponse.getCurrentPage() == 0) {
            // First page - replace all data
            currentList = new ArrayList<>(pageResponse.getRecords());
        } else {
            // Next pages - append data
            currentList.addAll(pageResponse.getRecords());
        }

        campSiteList.setValue(currentList);

        Log.d("CampSiteViewModel", "Total items now: " + currentList.size());
    }

}
