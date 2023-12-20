package com.buratud.stores;

import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.chat.ChatMessage;

public interface ChatGpt {
    ChatGptChannelInfo get(String channelId, String userId);

    ChatGptChannelInfo create(String channelId, String userId);

    ChatGptChannelInfo put(String channelId, String userId, ChatMessage message);

    ChatGptChannelInfo clear(String channelId, String userId);

    void save(String channelId, String userId, ChatGptChannelInfo message);
}
