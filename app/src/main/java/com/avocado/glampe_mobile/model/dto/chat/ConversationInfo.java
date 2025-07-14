package com.avocado.glampe_mobile.model.dto.chat;

import com.avocado.glampe_mobile.model.dto.user.resp.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationInfo {
    private UserResponse user;
    private String lastMessage;
    private long   timestamp;
}
