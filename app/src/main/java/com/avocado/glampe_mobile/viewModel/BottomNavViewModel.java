package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BottomNavViewModel extends ViewModel {
    private MutableLiveData<Boolean> bottomNavVisible = new MutableLiveData<>(true);

    public LiveData<Boolean> getBottomNavVisible(){
        return bottomNavVisible;
    }

    public void hideBottomNav() {
        bottomNavVisible.setValue(false);
    }

    public void showBottomNav() {
        bottomNavVisible.setValue(true);
    }

    public boolean isBottomNavVisible() {
        Boolean value = bottomNavVisible.getValue();
        return value != null ? value : true;
    }
}
