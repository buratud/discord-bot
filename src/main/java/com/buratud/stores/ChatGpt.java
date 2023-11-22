package com.buratud.stores;

import java.util.List;

import com.buratud.data.openai.chat.ChatMessage;

public interface ChatGpt {
    List<ChatMessage> get(String channelId, String userId);

    List<ChatMessage> create(String channelId, String userId);

    List<ChatMessage> put(String channelId, String userId, ChatMessage message);

    List<ChatMessage> clear(String channelId, String userId);

    void save(String channelId, String userId, List<ChatMessage> message);
}
