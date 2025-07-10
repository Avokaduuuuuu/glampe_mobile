package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.user.req.UserCreateRequest;
import com.avocado.glampe_mobile.model.dto.user.req.UserVerifyRequest;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.repository.user.UserRepository;

import lombok.Getter;

@Getter
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<AuthUserResponse> authUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<AuthUserResponse> createdUser = new MutableLiveData<>();
    private final MutableLiveData<String> createdUserError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createUserLoading = new MutableLiveData<>(false);
    public UserViewModel() {
        this.userRepository = UserRepository.getInstance();
    }

    public void verifyUser(UserVerifyRequest request){
        isLoading.setValue(true);
        userRepository.verifyUser(request, authUser, errorMessage);
    }

    public void createUser(UserCreateRequest request) {
        createUserLoading.setValue(true);

        userRepository.createUser(request, createdUser, createdUserError);
    }

}
