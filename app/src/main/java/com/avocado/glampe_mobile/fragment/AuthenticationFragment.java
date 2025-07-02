package com.avocado.glampe_mobile.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.avocado.glampe_mobile.BuildConfig;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.activity.MainActivity;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.user.req.UserVerifyRequest;
import com.avocado.glampe_mobile.viewModel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationFragment extends Fragment {

    LinearLayout guestBrowseButton, googleSignInButton;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    UserViewModel userViewModel;

    ProgressBar progressBar;
    FrameLayout loadingOverlay;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK){
                Log.d("GOOGLE_SIGN_IN", "Received result from Google Sign-In intent");
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                auth = FirebaseAuth.getInstance();
                                Log.d("User email", auth.getCurrentUser().getEmail());
                                userViewModel.verifyUser(UserVerifyRequest.builder().email(auth.getCurrentUser().getEmail()).build());
                                observeVerifyUser();
                            }else{
                                Toast.makeText(requireContext(), "Failed to sign in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (ApiException e){
                    e.printStackTrace();
                }
            }else {
                Log.d("Google Failed Sign In", "Failed to sign in");
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authentication, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseApp.initializeApp(requireContext());

        initiateView(view);
        initViewModel();
        onClickListener();
    }


    private void initiateView(View view){
        guestBrowseButton = view.findViewById(R.id.guestBrowseButton);
        googleSignInButton = view.findViewById(R.id.googleSignInButton);
        progressBar = view.findViewById(R.id.progressBar);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void initViewModel(){
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void authWithGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);
        auth = FirebaseAuth.getInstance();

        // Sign out from Firebase (optional)
        auth.signOut();

        // Sign out from Google to force account picker every time
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });
    }
    private void onClickListener(){
        guestBrowseButton.setOnClickListener(v -> navigateToMain());
        googleSignInButton.setOnClickListener(v -> authWithGoogle());
    }

    private void navigateToMain(){
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }

    private void observeVerifyUser(){
        userViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    googleSignInButton.setEnabled(false);
                    guestBrowseButton.setEnabled(false);
                    loadingOverlay.setVisibility(View.VISIBLE);
                }
            }
        });

        userViewModel.getAuthUser().observe(getViewLifecycleOwner(), authUser -> {
            if (authUser != null) {
                loadingOverlay.setVisibility(View.GONE);
                AuthManager.saveAuthResponse(requireContext(), authUser);
                navigateToMain();
            }
        });

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                googleSignInButton.setEnabled(true);
                guestBrowseButton.setEnabled(true);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
