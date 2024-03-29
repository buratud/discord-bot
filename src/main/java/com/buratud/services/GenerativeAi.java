package com.buratud.services;

import com.buratud.Env;
import com.buratud.entity.ai.PromptResponse;
import com.buratud.entity.ai.AiChatMetadata;
import com.buratud.entity.ai.AiChatSession;
import com.buratud.entity.ai.ChatMessage;
import com.buratud.entity.ai.Role;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GenerativeAi {
    private final com.buratud.stores.ChatGpt store;
    private ChatMessage system;
    private static String DEFAULT_MODEL = "gpt-3.5-turbo-0125";
    private static final String SYSTEM_MESSAGE_FILE = "./system_message.txt";

    public ChatGpt chatGpt = null;
    public GeminiAi gemini = null;

    public GenerativeAi() throws IOException {
        store = new com.buratud.stores.dynamodb.AiStore();
        readSystemMessage();
        if (Env.OPENAI_API_KEY != null) {
            chatGpt = new ChatGpt(Env.OPENAI_API_KEY);
        }
        if (Env.GEMINI_API_KEY != null) {
            gemini = new GeminiAi(Env.GEMINI_API_KEY);
        }
    }

    public void setDefaultModel(String model) {
        DEFAULT_MODEL = model;
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

    public AiChatMetadata getInfo(String guildId, String channelId, String userId) {
        return store.getMetadata(guildId, channelId, userId);
    }

    public AiChatMetadata reset(String guildId, String channelId, String userId) {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        if (metadata == null) {
            metadata = new AiChatMetadata(guildId, channelId, userId);
            metadata.setModel(DEFAULT_MODEL);
        }
        metadata.setCurrentSession(null);
        if (metadata.getModel().startsWith("gpt-4")) {
            metadata.setModel(DEFAULT_MODEL);
        }
        store.createMetadata(metadata);
        return metadata;
    }

    public void SwitchModel(String guildId, String channelId, String userId, String model) {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        if (metadata == null) {
            metadata = reset(guildId, channelId, userId);
        }
        metadata.setModel(model);
        if (metadata.getCurrentSession() != null) {
            AiChatSession session = store.getSession(guildId, channelId, userId, metadata.getCurrentSession().toString());
            session.setModel(model);
            store.updateSession(session);
        }
        store.updateMetadata(metadata);
    }

    public void SetActivation(String guildId, String channelId, String userId, Boolean activation) {
        AiChatMetadata metadata = store.getMetadata(null, channelId, userId);
        if (metadata == null) {
            metadata = reset(guildId, channelId, userId);
        }
        metadata.setActivated(activation);
        store.updateMetadata(metadata);
    }

    public void SetOneShot(String guildId, String channelId, String userId, Boolean activation) {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        if (metadata == null) {
            metadata = reset(guildId, channelId, userId);
        }
        metadata.setOneShot(activation);
        store.updateMetadata(metadata);
    }

    public String getSystemMessage(String guildId, String channelId, String userId) {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        if (metadata != null) {
            return metadata.getSystemMessage();
        }
        return null;
    }

    public void setSystemMessage(String guildId, String channelId, String userId, String message) {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        if (metadata == null) {
            metadata = reset(guildId, channelId, userId);
        }
        metadata.setSystemMessage(message.isEmpty() ? null : message);
        store.updateMetadata(metadata);
    }

    public PromptResponse sendStreamEnabled(String guildId, String channelId, String userId, String message) throws IOException, ExecutionException, InterruptedException {
        return sendStreamEnabled(guildId, channelId, userId, message, null);
    }

    public PromptResponse sendStreamEnabled(String guildId, String channelId, String userId, String message, List<Message.Attachment> attachments) throws IOException, ExecutionException, InterruptedException {
        AiChatMetadata metadata = store.getMetadata(guildId, channelId, userId);
        AiChatSession session;
        boolean isNewSession = false;
        if (metadata == null) {
            metadata = reset(guildId, channelId, userId);
        }
        if (metadata.getCurrentSession() == null || metadata.getModel().equals("gemini-1.0-pro-vision-latest")) {
            isNewSession = true;
            UUID id = UUID.randomUUID();
            metadata.setCurrentSession(id);
            session = new AiChatSession(guildId, channelId, userId, id);
            session.setModel(metadata.getModel());
            if (metadata.getSystemMessage() != null) {
                session.setSystemMessage(metadata.getSystemMessage());
                session.getHistory().add(new ChatMessage(Role.SYSTEM, metadata.getSystemMessage()));
            }
        } else {
            session = store.getSession(guildId, channelId, userId, metadata.getCurrentSession().toString());
        }
        List<ChatMessage> messages = session.getHistory();
        if (attachments != null) {
            messages.add(new ChatMessage(Role.USER, message, attachments.stream().map(Message.Attachment::getUrl).toList()));
            session.setHasImage(true);
        } else {
            messages.add(new ChatMessage(Role.USER, message));
        }
        PromptResponse response;
        if (session.getModel().startsWith("gpt")) {
            response = chatGpt.sendStreamEnabled(session);
        } else {
            response = gemini.sendStreamEnabled(session);
        }
        if (response.isFlagged()) {
            return response;
        }
        messages.add(new ChatMessage(Role.ASSISTANT, response.getMessage()));
        if (!metadata.getOneShot() && !session.getModel().equals("gemini-1.0-pro-vision-latest")) {
            if (isNewSession) {
                store.updateMetadata(metadata);
            }
            store.createSession(session);
        }
        return response;
    }
}
