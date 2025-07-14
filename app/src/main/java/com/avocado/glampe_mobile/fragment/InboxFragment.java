package com.avocado.glampe_mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.activity.AuthActivity;
import com.avocado.glampe_mobile.adapter.ConversationAdapter;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.chat.ConversationInfo;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.viewModel.ChatViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class InboxFragment extends Fragment {

    RecyclerView rvConversations;
    LottieAnimationView loadingAnimation;
    LinearLayout loginRequiredContainer;

    private ChatViewModel chatViewModel;
    private ConversationAdapter conversationAdapter;
    private List<ConversationInfo> conversations;
    private AuthUserResponse user;
    private NavController navController;
    private MaterialButton btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_list,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (user != null) {
            initViewHolder();
            observeChatViewModel();
            fetchConversations();
        } else {
            displayLoginRequire();
        }
    }

    private void displayLoginRequire() {
        loginRequiredContainer.setVisibility(View.VISIBLE);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            startActivity(intent);
        });
    }
    private void initView(View view) {
        rvConversations = view.findViewById(R.id.rvConversations);
        user = AuthManager.getAuthResponse(requireContext());
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        navController = Navigation.findNavController(view);
        btnLogin = view.findViewById(R.id.btnLogin);
        loginRequiredContainer = view.findViewById(R.id.loginRequiredContainer);
    }

    private void initViewHolder() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void setUpAdapter() {
        rvConversations.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false));
        conversationAdapter = new ConversationAdapter(conversations, conversationInfo -> {
            Bundle bundle = new Bundle();
            bundle.putLong("recipientId", conversationInfo.getUser().getId());
            bundle.putString("recipientName", conversationInfo.getUser().getFirstName());
            navController.navigate(R.id.action_inboxFragment_to_chatFragment, bundle);
        });
        rvConversations.setAdapter(conversationAdapter);
    }

    private void fetchConversations() {
        chatViewModel.fetchAllConversations(user.getUser().getId());
    }

    private void observeChatViewModel() {
        chatViewModel.getConversationsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                loadingAnimation.setVisibility(View.VISIBLE);
            }
        });

        chatViewModel.getConversations().observe(getViewLifecycleOwner(), conversations -> {
            if (conversations != null) {
                loadingAnimation.setVisibility(View.GONE);
                this.conversations = conversations;
                setUpAdapter();
            }
        });
    }
}
