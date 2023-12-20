package com.buratud.stores;

import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.ChatGptMetadata;
import com.buratud.data.openai.chat.ChatMessage;

public interface ChatGpt {
    ChatGptChannelInfo getChannelInfo(String guildId, String channelId, String userId);

    ChatGptChannelInfo createChannelInfo(ChatGptChannelInfo item);

    ChatGptChannelInfo putChannelInfo(ChatGptChannelInfo item);
    ChatGptChannelInfo appendHistory(ChatGptChannelInfo item);

    ChatGptChannelInfo deleteChannelInfo(String guildId, String channelId, String userId);

    ChatGptMetadata getChannelMemberMetadata(String guildId, String channelId, String userId);

    ChatGptMetadata createChannelMemberMetadata(ChatGptMetadata item);

    ChatGptMetadata putChannelMemberMetadata(ChatGptMetadata item);

    void deleteChannelMemberMetadata(String guildId, String channelId, String userId);
}
