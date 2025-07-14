package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;
import com.avocado.glampe_mobile.model.dto.chat.ConversationInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {

    @GET("/api/v1/chat/conversations/{userId}")
    Call<ApiResponse<List<ConversationInfo>>> getConversations(@Path("userId") Long id);


    @GET("/api/v1/chat/history")
    Call<ApiResponse<List<ChatMessageDto>>> getHistory(
            @Query("userId1") Long userId1,
            @Query("userId2") Long userId2,
            @Query("limit") int limit
    );
}
