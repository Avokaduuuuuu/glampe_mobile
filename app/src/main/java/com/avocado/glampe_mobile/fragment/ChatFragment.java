package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.ChatAdapter;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.viewModel.ChatViewModel;

public class ChatFragment extends Fragment {

    NavController navController;
    TextView tvName;
    ImageView btnBack;
    RecyclerView rvMessages;
    ImageButton btnSend;
    EditText etText;

    private Long recipientId;
    private ChatViewModel chatViewModel;
    private AuthUserResponse user;
    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        updateView();
        initViewModel();
        onClickListener();
        observeChatViewModel();
        fetchMessages();
    }

    private void initView(View view) {
        tvName = view.findViewById(R.id.tvName);
        btnBack = view.findViewById(R.id.btnBack);
        navController = Navigation.findNavController(view);
        rvMessages = view.findViewById(R.id.rvMessages);
        btnSend = view.findViewById(R.id.btnSend);
        user = AuthManager.getAuthResponse(requireContext());
        chatAdapter = new ChatAdapter(user.getUser().getId());
        etText = view.findViewById(R.id.etText);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(chatAdapter);
    }

    private void initViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void updateView() {
        if (getArguments() != null) {
            tvName.setText(getArguments().getString("recipientName"));
            recipientId = getArguments().getLong("recipientId");
        }
    }

    private void onClickListener() {
        btnBack.setOnClickListener(v -> {
            navController.navigate(R.id.inboxFragment, null ,new NavOptions.Builder()
                    .setPopUpTo(R.id.app_graph, true).build());
        });

        btnSend.setOnClickListener(v -> {
            String text = etText.getText().toString().trim();
            if (!text.isEmpty()) {
                chatViewModel.send(new ChatMessageDto(
                        user.getUser().getId(), recipientId, text, System.currentTimeMillis()));
                etText.setText("");
                rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

    }

    private void fetchMessages() {
        chatViewModel.start(user.getUser().getId());
        chatViewModel.getHistory(user.getUser().getId(), recipientId, 300);

    }

    private void observeChatViewModel() {
        chatViewModel.getMessagesLoading().observe(getViewLifecycleOwner(), loading -> {

        });

        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                chatAdapter.appendList(messages);
            }
        });
    }


}
