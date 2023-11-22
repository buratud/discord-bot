package com.buratud.stores.ephemeral;

import com.buratud.data.openai.chat.ChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatGpt implements com.buratud.stores.ChatGpt {

    HashMap<String, HashMap<String, List<ChatMessage>>> chat = new HashMap<>();

    @Override
    public List<ChatMessage> get(String channelId, String userId) {
        if (chat.containsKey(channelId)) {
            if (chat.get(channelId).containsKey(userId)) {
                return List.copyOf(chat.get(channelId).get(userId));
            }
        }
        return null;
    }

    @Override
    public List<ChatMessage> create(String channelId, String userId) {
        if (!chat.containsKey(channelId)) {
            chat.put(channelId, new HashMap<>());
        }
        if (!chat.get(channelId).containsKey(userId)) {
            chat.get(channelId).put(userId, new ArrayList<>());
        }
        return chat.get(channelId).get(userId);
    }

    @Override
    public List<ChatMessage> put(String channelId, String userId, ChatMessage message) {
        List<ChatMessage> messages = chat.get(channelId).get(userId);
        messages.add(message);
        return messages;
    }

    @Override
    public List<ChatMessage> clear(String channelId, String userId) {
        return chat.get(channelId).put(userId, new ArrayList<>());
    }

    @Override
    public void save(String channelId, String userId, List<ChatMessage> message) {
        chat.get(channelId).put(userId, message);
    }
}
