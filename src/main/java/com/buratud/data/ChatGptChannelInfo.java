package com.buratud.data;

import com.buratud.data.openai.chat.ChatMessage;

import java.util.List;

public class ChatGptChannelInfo {
    public ChatGptChannelInfo(String model) {
        this.model = model;
    }
    public String model;
    public List<ChatMessage> history;
}
