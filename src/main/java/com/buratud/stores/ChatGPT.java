package com.buratud.stores;

import java.util.List;

import com.buratud.data.openai.chat.ChatMessage;

public interface ChatGPT {
    public List<ChatMessage> get(String channelId, String userId);

    public List<ChatMessage> create(String channelId, String userId);

    public List<ChatMessage> put(String channelId, String userId, ChatMessage message);

    public List<ChatMessage> clear(String channelId, String userId);

    public void save(String channelId, String userId, List<ChatMessage> message);
}
