package com.buratud.stores.ephemeral;

import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.chat.ChatMessage;

import java.util.HashMap;

public class ChatGpt implements com.buratud.stores.ChatGpt {

    HashMap<String, HashMap<String, ChatGptChannelInfo>> chat = new HashMap<>();

    @Override
    public ChatGptChannelInfo getChannelInfo(String guildId, String channelId, String userId) {
        if (chat.containsKey(channelId)) {
            if (chat.get(channelId).containsKey(userId)) {
                return chat.get(channelId).get(userId);
            }
        }
        return null;
    }

    @Override
    public ChatGptChannelInfo createChannelInfo(ChatGptChannelInfo item) {
        if (!chat.containsKey(item.getChannelId())) {
            chat.put(item.getChannelId(), new HashMap<>());
        }
        if (!chat.get(item.getChannelId()).containsKey(item.getMemberId())) {
            chat.get(item.getChannelId()).put(item.getMemberId(), item);
        }
        return chat.get(item.getChannelId()).get(item.getMemberId());
    }

    @Override
    public ChatGptChannelInfo putChannelInfo(ChatGptChannelInfo item) {
        chat.get(item.getChannelId()).put(item.getMemberId(), item);
        return item;
    }

    @Override
    public ChatGptChannelInfo appendHistory(ChatGptChannelInfo item) {
        ChatGptChannelInfo info = chat.get(item.getChannelId()).get(item.getMemberId());
        info.getHistory().add(item.getHistory().getLast());
        return item;
    }

    @Override
    public ChatGptChannelInfo deleteChannelInfo(String guildId, String channelId, String userId) {
        return chat.get(channelId).put(userId, new ChatGptChannelInfo());
    }
}
