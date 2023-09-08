package com.buratud.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.buratud.data.openai.chat.ChatCompletionRequest;
import com.buratud.data.openai.chat.ChatCompletionRequestBuilder;
import com.buratud.data.openai.chat.ChatCompletionResponse;
import com.buratud.data.openai.chat.Message;
import com.buratud.data.openai.chat.Role;
import com.buratud.data.openai.moderation.ModerationResponse;

public class ChatGPT {
    private ChatGptHttp client;
    private Message system;
    private com.buratud.stores.ChatGPT store;
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    public ChatGPT(String key) {
        client = new ChatGptHttp(key);
        system = new Message(Role.SYSTEM, "You are assistance.");
        store = new com.buratud.stores.ephemeral.ChatGPT();
    }

    public String send(String channelId, String userId, String message) throws IOException, InterruptedException {
        List<Message> history = store.get(channelId, userId);
        if (history == null) {
            history = store.create(channelId, userId);
            history.add(system);
        }
        history.add(new Message(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(DEFAULT_MODEL, history).build();
        ChatCompletionResponse response = client.sendChatCompletionRequest(request);
        String messageRes = response.choices.get(0).message.content;
        history.add(new Message(Role.ASSISTANT, messageRes));
        store.save(channelId, userId, history);
        return messageRes;
    }

    public String moderationCheck(String message) throws IOException, InterruptedException {
        ModerationResponse response = client.moderateMessage(message);
        if (response.results.get(0).flagged) {
            for (Map.Entry<String, Boolean> item : response.results.get(0).categories.entrySet()) {
                if (item.getValue()) {
                    return String.format("Message was block due to %s", item.getKey());
                }
            }
        }
        return null;
    }

    public void reset(String channelId, String userId) {
        List<Message> history = store.get(channelId, userId);
        if (history == null) {
            history = store.create(channelId, userId);
        } else {
            history = store.clear(channelId, userId);
        }
        history.add(system);
        store.save(channelId, userId, history);
    }
}
