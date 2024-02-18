package com.buratud.entity.openai.chat;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatCompletionRequestMapper {
    ChatCompletionRequestMapper INSTANCE = Mappers.getMapper(ChatCompletionRequestMapper.class);

    List<ChatMessage> fromGeneric(List<com.buratud.entity.ai.ChatMessage> chatMessages);

    List<com.buratud.entity.ai.ChatMessage> toGeneric(List<ChatMessage> chatMessages);
}
