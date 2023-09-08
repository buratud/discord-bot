package com.buratud.stores;

import java.util.List;

import com.buratud.data.openai.chat.Message;

public interface ChatGPT {
    public List<Message> get(String channelId, String userId);

    public List<Message> create(String channelId, String userId);

    public List<Message> put(String channelId, String userId, Message message);

    public List<Message> clear(String channelId, String userId);

    public void save(String channelId, String userId, List<Message> message);
}
