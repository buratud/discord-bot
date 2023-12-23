package com.buratud.data.openai.chat;

public class ChatMessage {
    public Role role;
    public String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
}