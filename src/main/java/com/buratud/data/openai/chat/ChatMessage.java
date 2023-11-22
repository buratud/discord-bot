package com.buratud.data.openai.chat;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("role")
    public Role role;
    @SerializedName("content")
    public String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
}