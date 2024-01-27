package com.buratud.stores.ephemeral;

import com.buratud.entity.openai.AiChatMetadata;
import com.buratud.entity.openai.AiChatSession;

import java.util.HashMap;

public class ChatGpt implements com.buratud.stores.ChatGpt {

    HashMap<String, HashMap<String, AiChatMetadata>> chat = new HashMap<>();
    HashMap<String, HashMap<String, AiChatSession>> metadata = new HashMap<>();

    @Override
    public AiChatMetadata getChannelInfo(String guildId, String channelId, String userId) {
        if (chat.containsKey(channelId)) {
            if (chat.get(channelId).containsKey(userId)) {
                return chat.get(channelId).get(userId);
            }
        }
        return null;
    }

    @Override
    public AiChatMetadata createChannelInfo(AiChatMetadata item) {
        if (!chat.containsKey(item.getChannelId())) {
            chat.put(item.getChannelId(), new HashMap<>());
        }
        if (!chat.get(item.getChannelId()).containsKey(item.getMemberId())) {
            chat.get(item.getChannelId()).put(item.getMemberId(), item);
        }
        return chat.get(item.getChannelId()).get(item.getMemberId());
    }

    @Override
    public AiChatMetadata putChannelInfo(AiChatMetadata item) {
        chat.get(item.getChannelId()).put(item.getMemberId(), item);
        return item;
    }

    @Override
    public AiChatMetadata appendHistory(AiChatMetadata item) {
        AiChatMetadata info = chat.get(item.getChannelId()).get(item.getMemberId());
        info.getHistory().add(item.getHistory().get(item.getHistory().size() - 1));
        return item;
    }

    @Override
    public AiChatMetadata deleteChannelInfo(String guildId, String channelId, String userId) {
        return chat.get(channelId).put(userId, new AiChatMetadata());
    }

    @Override
    public AiChatSession getChannelMemberMetadata(String guildId, String channelId, String userId) {
        if (metadata.containsKey(channelId) && metadata.get(channelId).containsKey(userId)) {
            return metadata.get(channelId).get(userId);
        }
        return null;
    }

    @Override
    public AiChatSession createChannelMemberMetadata(AiChatSession item) {
        if (!metadata.containsKey(item.getChannelId())) {
            metadata.put(item.getChannelId(), new HashMap<>());
        }
        metadata.get(item.getChannelId()).put(item.getMemberId(), item);
        return metadata.get(item.getChannelId()).get(item.getMemberId());
    }

    @Override
    public AiChatSession putChannelMemberMetadata(AiChatSession item) {
        metadata.get(item.getChannelId()).put(item.getMemberId(), item);
        return getChannelMemberMetadata(item.getGuildId(), item.getChannelId(), item.getMemberId());
    }

    @Override
    public void deleteChannelMemberMetadata(String guildId, String channelId, String userId) {
        metadata.get(channelId).remove(userId);
    }
}
