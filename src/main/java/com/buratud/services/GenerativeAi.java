package com.buratud.services;

import com.buratud.Env;
import com.buratud.data.ai.PromptResponse;
import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.ChatGptMetadata;
import com.buratud.data.openai.chat.ChatMessage;
import com.buratud.data.openai.chat.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class GenerativeAi {
    private final com.buratud.stores.ChatGpt store;
    private ChatMessage system;
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo-1106";
    private static final String SYSTEM_MESSAGE_FILE = "./system_message.txt";

    private final ChatGpt chatGpt;
    public GenerativeAi() throws IOException {
        store = new com.buratud.stores.ephemeral.ChatGpt();
        readSystemMessage();
        chatGpt = new ChatGpt(Env.OPENAI_API_KEY);
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

    public ChatGptChannelInfo getInfo(String channelId, String userId) {
        return store.getChannelInfo(null, channelId, userId);
    }

    public ChatGptChannelInfo reset(String channelId, String userId) {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = new ChatGptChannelInfo(null, channelId, userId);
            info.setModel(DEFAULT_MODEL);
            if (system != null) {
                info.getHistory().add(system);
            }
            return store.createChannelInfo(info);
        }
        info.setModel(DEFAULT_MODEL);
        info.setHistory(new ArrayList<>());
        String userSystemMessage = getSystemMessage(channelId, userId);
        if (userSystemMessage != null) {
            info.getHistory().add(new ChatMessage(Role.SYSTEM, userSystemMessage));
        }
        return store.putChannelInfo(info);
    }

    public void SwitchModel(String channelId, String userId, String model) {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.setModel(model);
        store.putChannelInfo(info);
    }

    public void SetActivation(String channelId, String userId, Boolean activation) {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.setActivated(activation);
        store.putChannelInfo(info);
    }

    public void SetOneShot(String channelId, String userId, Boolean activation) {
        ChatGptMetadata metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null) {
            metadata = new ChatGptMetadata(null, channelId, userId);
        }
        metadata.setOneShot(activation);
        store.createChannelMemberMetadata(metadata);
    }

    public String getSystemMessage(String channelId, String userId) {
        ChatGptMetadata metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata != null) {
            return metadata.getSystemMessage();
        }
        return null;
    }

    public void setSystemMessage(String channelId, String userId, String message) {
        ChatGptMetadata metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null) {
            metadata = new ChatGptMetadata(null, channelId, userId);
        }
        if (Objects.equals(message, "")) {
            metadata.setSystemMessage(null);
        } else {
            metadata.setSystemMessage(message);
        }
        store.createChannelMemberMetadata(metadata);
    }

    public PromptResponse sendStreamEnabled(String channelId, String userId, String message) throws IOException, ExecutionException, InterruptedException {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        List<ChatMessage> messages = new ArrayList<>(List.copyOf(info.getHistory()).stream().toList());
        messages.add(new ChatMessage(Role.USER, message));
        PromptResponse response = chatGpt.sendStreamEnabled(info, messages);
        if (response.isFlagged()) {
            return response;
        }
        messages.add(new ChatMessage(Role.ASSISTANT, response.getMessage()));
        ChatGptMetadata metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null || !metadata.isOneShot()) {
            info.setHistory(messages);
            store.putChannelInfo(info);
        }
        return new PromptResponse(false, String.format("%s", response.getMessage()));
    }
}
