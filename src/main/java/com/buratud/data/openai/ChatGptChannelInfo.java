package com.buratud.data.openai;

import com.buratud.data.openai.chat.ChatMessage;

import java.util.List;

public class ChatGptChannelInfo {
    public boolean activated;
    public String model;
    public List<ChatMessage> history;
}
