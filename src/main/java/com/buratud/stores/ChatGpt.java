package com.buratud.stores;

import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.chat.ChatMessage;

public interface ChatGpt {
    ChatGptChannelInfo getChannelInfo(String guildId, String channelId, String userId);

    ChatGptChannelInfo createChannelInfo(ChatGptChannelInfo item);

    ChatGptChannelInfo putChannelInfo(ChatGptChannelInfo item);
    ChatGptChannelInfo appendHistory(ChatGptChannelInfo item);

    ChatGptChannelInfo deleteChannelInfo(String guildId, String channelId, String userId);
}
