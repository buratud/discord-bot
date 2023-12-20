package com.buratud.stores.ephemeral;

import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.ChatGptMetadata;
import com.buratud.data.openai.chat.ChatMessage;

import java.util.HashMap;

public class ChatGpt implements com.buratud.stores.ChatGpt {

    HashMap<String, HashMap<String, ChatGptChannelInfo>> chat = new HashMap<>();
    HashMap<String, HashMap<String, ChatGptMetadata>> metadata = new HashMap<>();

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
        info.getHistory().add(item.getHistory().get(item.getHistory().size() - 1));
        return item;
    }

    @Override
    public ChatGptChannelInfo deleteChannelInfo(String guildId, String channelId, String userId) {
        return chat.get(channelId).put(userId, new ChatGptChannelInfo());
    }

    @Override
    public ChatGptMetadata getChannelMemberMetadata(String guildId, String channelId, String userId) {
        if (metadata.containsKey(channelId) && metadata.get(channelId).containsKey(userId)) {
            return metadata.get(channelId).get(userId);
        }
        return null;
    }

    @Override
    public ChatGptMetadata createChannelMemberMetadata(ChatGptMetadata item) {
        if (!metadata.containsKey(item.getChannelId())) {
            metadata.put(item.getChannelId(), new HashMap<>());
        }
        metadata.get(item.getChannelId()).put(item.getMemberId(), item);
        return metadata.get(item.getChannelId()).get(item.getMemberId());
    }

    @Override
    public ChatGptMetadata putChannelMemberMetadata(ChatGptMetadata item) {
        metadata.get(item.getChannelId()).put(item.getMemberId(), item);
        return getChannelMemberMetadata(item.getGuildId(), item.getChannelId(), item.getMemberId());
    }

    @Override
    public void deleteChannelMemberMetadata(String guildId, String channelId, String userId) {
        metadata.get(channelId).remove(userId);
    }
}
