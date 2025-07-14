package com.avocado.glampe_mobile.model.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    Long senderId;
    Long receiverId;
    String content;
    long timestamp;
}
