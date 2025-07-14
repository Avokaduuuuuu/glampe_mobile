package com.avocado.glampe_mobile.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.fcmtoken.req.FcmTokenUpdateRequest;
import com.avocado.glampe_mobile.model.dto.fcmtoken.resp.FcmTokenResponse;
import com.avocado.glampe_mobile.model.dto.user.req.UserCreateRequest;
import com.avocado.glampe_mobile.model.dto.user.req.UserVerifyRequest;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.UserService;

public class UserRepository extends BaseRepository {
    private static UserRepository userRepository;
    private final UserService userService;


    public UserRepository() {
        this.userService = ApiServiceFactory.getInstance().getUserService();
    }

    public static synchronized UserRepository getInstance(){
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    public void verifyUser(UserVerifyRequest request,
                           MutableLiveData<AuthUserResponse> successLiveData,
                           MutableLiveData<String> errorMessage){
        executeCall(userService.verifyUser(request), successLiveData, errorMessage);
    }

    public void createUser(UserCreateRequest request,
                           MutableLiveData<AuthUserResponse> successLiveData,
                           MutableLiveData<String> errorMessage){
        executeCall(userService.addUser(request), successLiveData, errorMessage);
    }

    public void saveFcmToken(FcmTokenUpdateRequest request,
                             MutableLiveData<FcmTokenResponse> successLiveData,
                             MutableLiveData<String> errorMessage){
        executeCall(userService.saveFcmToken(request), successLiveData, errorMessage);
    }

    public void deleteToken(Long userId, String deviceId,
                            MutableLiveData<String> successLiveData,
                            MutableLiveData<String> errorMessage) {
        executeCall(userService.deleteToken(userId, deviceId), successLiveData, errorMessage);
    }
}
