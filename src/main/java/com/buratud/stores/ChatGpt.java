package com.buratud.stores;

import com.buratud.entity.openai.AiChatMetadata;
import com.buratud.entity.openai.AiChatSession;

public interface ChatGpt {
    AiChatMetadata getMetadata(String guildId, String channelId, String userId);
    void createMetadata(AiChatMetadata item);
    AiChatMetadata updateMetadata(AiChatMetadata item);
    void deleteMetadata(String guildId, String channelId, String userId);
    AiChatSession getSession(String guildId, String channelId, String userId, String id);
    void createSession(AiChatSession item);
    AiChatSession appendHistory(AiChatSession item);
    AiChatSession updateSession(AiChatSession item);
    void deleteSession(String guildId, String channelId, String userId, String id);
}
