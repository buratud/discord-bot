package com.buratud.entity.googleai;

import com.buratud.Utility;
import com.buratud.entity.ai.ChatMessage;
import com.buratud.entity.ai.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.List;

import static com.buratud.Utility.MimeTypeDetector.defaultReturn;

@Mapper
public interface ChatCompletionRequestMapper {
    ChatCompletionRequestMapper INSTANCE = Mappers.getMapper(ChatCompletionRequestMapper.class);

    List<Content> ChatMessageToContent(List<ChatMessage> chatMessages);

    default Content map(ChatMessage value) throws IOException {
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
        TextPart part = new TextPart(value.getContent());
        List<Part> parts = new java.util.ArrayList<>(List.of(part));
        if (value.getImages() != null) {
            for (String imageUrl : value.getImages()) {
                InlineData inlineData = new InlineData();
                String mimeType = Utility.MimeTypeDetector.getMimeType(imageUrl);
                if (mimeType.equals(defaultReturn)) {
                    throw new IllegalArgumentException("Invalid image type: " + Utility.MimeTypeDetector.getMimeType(imageUrl));
                } else {
                    inlineData.setMimeType(mimeType);
                    String base64Image = Utility.Base64WithoutPadding.downloadImageToBase64WithoutPadding(imageUrl);
                    inlineData.setData(base64Image);
                    parts.add(new InlineDataPart(inlineData));
                }
            }
        }
        content.setParts(parts);
        return content;
    }
}
