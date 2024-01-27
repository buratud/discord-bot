package com.buratud.stores;

import com.buratud.entity.openai.AiChatMetadata;
import com.buratud.entity.openai.AiChatSession;

public interface ChatGpt {
    AiChatMetadata getChannelInfo(String guildId, String channelId, String userId);

    AiChatMetadata createChannelInfo(AiChatMetadata item);

    AiChatMetadata putChannelInfo(AiChatMetadata item);
    AiChatMetadata appendHistory(AiChatMetadata item);

    AiChatMetadata deleteChannelInfo(String guildId, String channelId, String userId);

    AiChatSession getChannelMemberMetadata(String guildId, String channelId, String userId);

    AiChatSession createChannelMemberMetadata(AiChatSession item);

    AiChatSession putChannelMemberMetadata(AiChatSession item);

    void deleteChannelMemberMetadata(String guildId, String channelId, String userId);
}
