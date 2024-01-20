package com.buratud.entity.openai.chat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ChatMessage {
    @JsonSerialize(using = RoleSerializer.class)
    public Role role;
    public String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
}