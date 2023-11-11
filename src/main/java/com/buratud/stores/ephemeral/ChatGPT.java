package com.buratud.stores.ephemeral;

import com.buratud.data.openai.chat.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatGPT implements com.buratud.stores.ChatGPT {

    HashMap<String, HashMap<String, List<Message>>> chat = new HashMap<>();

    @Override
    public List<Message> get(String channelId, String userId) {
        if (chat.containsKey(channelId)) {
            if (chat.get(channelId).containsKey(userId)) {
                return List.copyOf(chat.get(channelId).get(userId));
            }
        }
        return null;
    }

    @Override
    public List<Message> create(String channelId, String userId) {
        if (!chat.containsKey(channelId)) {
            chat.put(channelId, new HashMap<>());
        }
        if (!chat.get(channelId).containsKey(userId)) {
            chat.get(channelId).put(userId, new ArrayList<>());
        }
        return chat.get(channelId).get(userId);
    }

    @Override
    public List<Message> put(String channelId, String userId, Message message) {
        List<Message> messages = chat.get(channelId).get(userId);
        messages.add(message);
        return messages;
    }

    @Override
    public List<Message> clear(String channelId, String userId) {
        return chat.get(channelId).put(userId, new ArrayList<>());
    }

    @Override
    public void save(String channelId, String userId, List<Message> message) {
        chat.get(channelId).put(userId, message);
    }
}
