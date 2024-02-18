package com.buratud.entity.googleai;

import com.buratud.entity.ai.ChatMessage;
import com.buratud.entity.ai.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatCompletionRequestMapper {
    ChatCompletionRequestMapper INSTANCE = Mappers.getMapper(ChatCompletionRequestMapper.class);

    @Mapping(source = "content", target = "parts[0].text")
    List<Content> ChatMessageToContent(List<ChatMessage> chatMessages);

    default Content map(ChatMessage value) {
        Content content = new Content();
        Role sourceRole = value.getRole();
        com.buratud.entity.googleai.Role targetRole;
        if (sourceRole == Role.SYSTEM) {
            targetRole = com.buratud.entity.googleai.Role.SYSTEM;
        } else if (sourceRole == Role.USER) {
            targetRole = com.buratud.entity.googleai.Role.USER;
        } else {
            targetRole = com.buratud.entity.googleai.Role.MODEL;
        }
        content.setRole(targetRole);
        TextPart part = new TextPart();
        part.setText(value.getContent());
        content.setParts(List.of(part));
        return content;
    }
}
