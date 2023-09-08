package com.buratud.data.openai.chat;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("role")
    public Role role;
    @SerializedName("content")
    public String content;

    public Message(Role role, String content) {
        this.role = role;
        this.content = content;
    }
}