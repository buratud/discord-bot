package com.buratud.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.buratud.data.openai.chat.ChatCompletionRequest;
import com.buratud.data.openai.chat.ChatCompletionRequestBuilder;
import com.buratud.data.openai.chat.ChatCompletionResponse;
import com.buratud.data.openai.chat.ChatMessage;
import com.buratud.data.openai.chat.Role;
import com.buratud.data.openai.moderation.ModerationResponse;

public class ChatGPT {
    private final ChatGptHttp client;
    private ChatMessage system;
    private final com.buratud.stores.ChatGPT store;
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final String SYSTEM_MESSAGE_FILE = "./system_message.txt";

    public ChatGPT(String key) throws IOException {
        client = new ChatGptHttp(key);
        store = new com.buratud.stores.ephemeral.ChatGPT();
        readSystemMessage();
    }

    private void readSystemMessage() throws IOException {
        system = null;
        Path path = Paths.get(SYSTEM_MESSAGE_FILE);
        if (Files.exists(path)) {
            String content = Files.readString(path);
            if (!content.isEmpty()) {
                system = new ChatMessage(Role.SYSTEM, content);
            }
        }
    }

    public String send(String channelId, String userId, String message) throws IOException, InterruptedException {
        List<ChatMessage> history = store.get(channelId, userId);
        if (history == null) {
            history = store.create(channelId, userId);
            if (system != null) {
                history.add(system);
            }
        }
        history.add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(DEFAULT_MODEL, history).build();
        ChatCompletionResponse response = client.sendChatCompletionRequest(request);
        String messageRes = response.choices.get(0).message.content;
        messageRes = messageRes.replace("\n\n", "\n");
        history.add(new ChatMessage(Role.ASSISTANT, messageRes));
        store.save(channelId, userId, history);
        return String.format("%s\n\nTotal tokens: %d", messageRes, response.usage.totalTokens);
    }

    public String moderationCheck(String message) throws IOException, InterruptedException {
        ModerationResponse response = client.moderateMessage(message);
        if (response.results.get(0).flagged) {
            for (Map.Entry<String, Boolean> item : response.results.get(0).categories.entrySet()) {
                if (item.getValue()) {
                    return String.format("Message was blocked due to %s", item.getKey());
                }
            }
        }
        return null;
    }

    public void reset(String channelId, String userId) {
        List<ChatMessage> history = store.get(channelId, userId);
        if (history == null) {
            history = store.create(channelId, userId);
        } else {
            history = store.clear(channelId, userId);
        }
        if (system != null) {
            history.add(system);
        }
        store.save(channelId, userId, history);
    }
}
