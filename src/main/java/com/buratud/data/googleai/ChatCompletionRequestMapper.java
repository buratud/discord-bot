package com.buratud.data.googleai;

import com.buratud.data.openai.chat.ChatMessage;
import com.buratud.data.openai.chat.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatCompletionRequestMapper {
    ChatCompletionRequestMapper INSTANCE = Mappers.getMapper( ChatCompletionRequestMapper.class );

    @Mapping(source = "content", target = "parts[0].text")
    List<Content> ChatMessageToContent(List<ChatMessage> chatMessages);

    default Content map(ChatMessage value) {
        Content content = new Content();
        Role sourceRole = value.role;
        com.buratud.data.googleai.Role targetRole;
        if (sourceRole == Role.SYSTEM) {
            targetRole = com.buratud.data.googleai.Role.SYSTEM;
        } else if (sourceRole == Role.USER) {
            targetRole = com.buratud.data.googleai.Role.USER;
        } else {
            targetRole = com.buratud.data.googleai.Role.MODEL;
        }
        content.setRole(targetRole);
        Part part = new Part();
        part.setText(value.content);
        content.setParts(List.of(part));
        return content;
    }
}