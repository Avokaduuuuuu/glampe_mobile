package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;
import com.avocado.glampe_mobile.model.dto.chat.ConversationInfo;
import com.avocado.glampe_mobile.repository.chat.ChatRepository;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


@Getter
public class ChatViewModel extends ViewModel {
    private ChatRepository chatRepository;

    private final MutableLiveData<List<ChatMessageDto>> messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> messagesLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> messagesError = new MutableLiveData<>();

    private final MutableLiveData<List<ConversationInfo>> conversations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> conversationsLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> conversationsError = new MutableLiveData<>();

    private final Observer<ChatMessageDto> wsObserver = msg -> {
        List<ChatMessageDto> list = new ArrayList<>(messages.getValue());
        list.add(msg);
        messages.postValue(list);
    };


    public ChatViewModel() {
        chatRepository = ChatRepository.getInstance();
    }

    public void fetchAllConversations(Long id) {
        conversationsLoading.setValue(true);

        chatRepository.getConversations(id, conversations, conversationsError);

    }

    public void getHistory(Long userId1, Long userId2, int limit) {
        messagesLoading.setValue(true);

        chatRepository.getHistory(userId1, userId2, limit, messages, messagesError);
    }

    public void start(Long meId) {
        chatRepository.incoming().observeForever(wsObserver);
        chatRepository.connect(meId);
    }

    public void send(ChatMessageDto dto) {
        chatRepository.send(dto);
    }

    @Override
    protected void onCleared() {
        chatRepository.incoming().removeObserver(wsObserver);
        chatRepository.disconnect();
    }
}
