package com.buratud.stores.ephemeral;

import com.buratud.data.ChatGptChannelInfo;
import com.buratud.data.openai.chat.ChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatGpt implements com.buratud.stores.ChatGpt {

    HashMap<String, HashMap<String, ChatGptChannelInfo>> chat = new HashMap<>();

    @Override
    public ChatGptChannelInfo get(String channelId, String userId) {
        if (chat.containsKey(channelId)) {
            if (chat.get(channelId).containsKey(userId)) {
                return chat.get(channelId).get(userId);
            }
        }
        return null;
    }

    @Override
    public ChatGptChannelInfo create(String channelId, String userId) {
        if (!chat.containsKey(channelId)) {
            chat.put(channelId, new HashMap<>());
        }
        if (!chat.get(channelId).containsKey(userId)) {
            chat.get(channelId).put(userId, new ChatGptChannelInfo());
        }
        return chat.get(channelId).get(userId);
    }

    @Override
    public ChatGptChannelInfo put(String channelId, String userId, ChatMessage message) {
        ChatGptChannelInfo messages = chat.get(channelId).get(userId);
        messages.history.add(message);
        return messages;
    }

    @Override
    public ChatGptChannelInfo clear(String channelId, String userId) {
        return chat.get(channelId).put(userId, new ChatGptChannelInfo());
    }

    @Override
    public void save(String channelId, String userId, ChatGptChannelInfo message) {
        chat.get(channelId).put(userId, message);
    }
}
