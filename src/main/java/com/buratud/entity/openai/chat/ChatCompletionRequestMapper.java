package com.buratud.entity.openai.chat;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatCompletionRequestMapper {
    ChatCompletionRequestMapper INSTANCE = Mappers.getMapper(ChatCompletionRequestMapper.class);

    ChatMessage fromGeneric(com.buratud.entity.ai.ChatMessage chatMessages);

    com.buratud.entity.ai.ChatMessage toGeneric(ChatMessage chatMessages);

    List<ChatMessage> fromGenericList(List<com.buratud.entity.ai.ChatMessage> chatMessages);

    List<com.buratud.entity.ai.ChatMessage> toGenericList(List<ChatMessage> chatMessages);
}
