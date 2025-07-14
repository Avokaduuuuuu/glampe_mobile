package com.avocado.glampe_mobile.repository.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.di.StompManager;
import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;
import com.avocado.glampe_mobile.model.dto.chat.ConversationInfo;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.ChatService;

import java.util.List;


public class ChatRepository extends BaseRepository {
    private static ChatRepository chatRepository;
    private final ChatService chatService;
    private final StompManager manager = StompManager.get();


    public ChatRepository() {
        this.chatService = ApiServiceFactory.getInstance().getChatService();
    }

    public static synchronized ChatRepository getInstance() {
        return chatRepository == null ? new ChatRepository() : chatRepository;
    }

    public void getConversations(Long userId,
                                 MutableLiveData<List<ConversationInfo>> successLiveData,
                                 MutableLiveData<String> errorMessage) {
        executeCall(chatService.getConversations(userId), successLiveData, errorMessage);
    }

    public void getHistory(Long userId1, Long userId2, int limit,
                           MutableLiveData<List<ChatMessageDto>> successLiveData,
                           MutableLiveData<String> errorMessage) {
        executeCall(chatService.getHistory(userId1, userId2, limit), successLiveData, errorMessage);
    }

    public LiveData<ChatMessageDto> incoming() { return manager.incoming(); }
    public void connect(Long me)            { manager.connect(me); }
    public void send(ChatMessageDto dto)    { manager.send(dto); }
    public void disconnect()                { manager.disconnect(); }
}
