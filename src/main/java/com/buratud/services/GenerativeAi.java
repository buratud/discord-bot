package com.buratud.services;

import com.buratud.Env;
import com.buratud.entity.ai.PromptResponse;
import com.buratud.entity.openai.AiChatMetadata;
import com.buratud.entity.openai.AiChatSession;
import com.buratud.entity.openai.chat.ChatMessage;
import com.buratud.entity.openai.chat.Role;

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

    public final ChatGpt chatGpt;
    private final GeminiAi gemini;
    public GenerativeAi() throws IOException {
        store = new com.buratud.stores.ephemeral.ChatGpt();
        readSystemMessage();
        chatGpt = new ChatGpt(Env.OPENAI_API_KEY);
        gemini = new GeminiAi(Env.GEMINI_API_KEY);
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

    public AiChatMetadata getInfo(String channelId, String userId) {
        return store.getChannelInfo(null, channelId, userId);
    }

    public AiChatMetadata reset(String channelId, String userId) {
        AiChatMetadata info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = new AiChatMetadata(null, channelId, userId);
            info.setModel(DEFAULT_MODEL);
            if (system != null) {
                info.getHistory().add(system);
            }
            return store.createChannelInfo(info);
        }
        if (Objects.equals(info.getModel(), "gpt-4-1106-preview")){
            info.setModel("gpt-3.5-turbo-1106");
        }
        info.setHistory(new ArrayList<>());
        String userSystemMessage = getSystemMessage(channelId, userId);
        if (userSystemMessage != null) {
            info.getHistory().add(new ChatMessage(Role.SYSTEM, userSystemMessage));
        }
        return store.putChannelInfo(info);
    }

    public void SwitchModel(String channelId, String userId, String model) {
        AiChatMetadata info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.setModel(model);
        store.putChannelInfo(info);
    }

    public void SetActivation(String channelId, String userId, Boolean activation) {
        AiChatMetadata info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.setActivated(activation);
        store.putChannelInfo(info);
    }

    public void SetOneShot(String channelId, String userId, Boolean activation) {
        AiChatSession metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null) {
            metadata = new AiChatSession(null, channelId, userId);
        }
        metadata.setOneShot(activation);
        store.createChannelMemberMetadata(metadata);
    }

    public String getSystemMessage(String channelId, String userId) {
        AiChatSession metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata != null) {
            return metadata.getSystemMessage();
        }
        return null;
    }

    public void setSystemMessage(String channelId, String userId, String message) {
        AiChatSession metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null) {
            metadata = new AiChatSession(null, channelId, userId);
        }
        if (Objects.equals(message, "")) {
            metadata.setSystemMessage(null);
        } else {
            metadata.setSystemMessage(message);
        }
        store.createChannelMemberMetadata(metadata);
    }

    public PromptResponse sendStreamEnabled(String channelId, String userId, String message) throws IOException, ExecutionException, InterruptedException {
        AiChatMetadata info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        List<ChatMessage> messages = new ArrayList<>(List.copyOf(info.getHistory()).stream().toList());
        messages.add(new ChatMessage(Role.USER, message));
        PromptResponse response;
        if (info.getModel().startsWith("gpt")) {
            response = chatGpt.sendStreamEnabled(info, messages);
        } else {
            response = gemini.sendStreamEnabled(info, messages);
        }
        if (response.isFlagged()) {
            return response;
        }
        messages.add(new ChatMessage(Role.ASSISTANT, response.getMessage()));
        AiChatSession metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null || !metadata.isOneShot()) {
            info.setHistory(messages);
            store.putChannelInfo(info);
        }
        return response;
    }
}
