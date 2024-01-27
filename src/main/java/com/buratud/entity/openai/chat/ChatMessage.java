package com.buratud.entity.openai.chat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public class ChatMessage {
    @JsonSerialize(using = RoleSerializer.class)
    private Role role;
    private String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatMessage(String role, String content) {
        this.role = Role.valueOf(role);
        this.content = content;
    }
}