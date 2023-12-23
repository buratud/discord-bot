package com.buratud.services;

import com.buratud.Utility;
import com.buratud.data.openai.ChatGptChannelInfo;
import com.buratud.data.openai.ChatGptMetadata;
import com.buratud.data.openai.chat.*;
import com.buratud.data.openai.moderation.ModerationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class ChatGpt {
    private static final Logger logger = LogManager.getLogger(ChatGpt.class);
    private final ChatGptHttp client;
    private ChatMessage system;
    private final com.buratud.stores.ChatGpt store;
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo-1106";
    private static final String SYSTEM_MESSAGE_FILE = "./system_message.txt";

    public ChatGpt(String key) throws IOException {
        client = new ChatGptHttp(key);
        store = new com.buratud.stores.ephemeral.ChatGpt();
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

    public ChatGptChannelInfo getInfo(String channelId, String userId) {
        return store.getChannelInfo(null, channelId, userId);
    }

    public String send(String channelId, String userId, String message) throws IOException, InterruptedException {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.getHistory().add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(info.getModel(), info.getHistory()).build();
        ChatCompletionResponse response = client.sendChatCompletionRequest(request);
        String messageRes = response.choices.get(0).message.content;
        messageRes = messageRes.replace("\n\n", "\n");
        info.getHistory().add(new ChatMessage(Role.ASSISTANT, messageRes));
        store.putChannelInfo(info);
        return String.format("%s\n\nTotal tokens: %d", messageRes, response.usage.totalTokens);
    }

    public String sendStreamEnabled(String channelId, String userId, String message) throws InterruptedException, ExecutionException, JsonProcessingException {
        ChatGptChannelInfo info = store.getChannelInfo(null, channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        List<ChatMessage> messages = new ArrayList<>(List.copyOf(info.getHistory()).stream().toList());
        messages.add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(info.getModel(), messages).withStream(true).build();
        EventStreamSubscriber subscriber = new EventStreamSubscriber();
        client.sendChatCompletionRequestWithStreamEnabled(request, subscriber);
        String messageRes = subscriber.getContent();
        messageRes = messageRes.replace("\n\n", "\n");
        messages.add(new ChatMessage(Role.ASSISTANT, messageRes));
        ChatGptMetadata metadata = store.getChannelMemberMetadata(null, channelId, userId);
        if (metadata == null || !metadata.isOneShot()) {
            info.setHistory(messages);
            store.putChannelInfo(info);
        }
        return String.format("%s", messageRes);
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

    public class EventStreamSubscriber implements Flow.Subscriber<String> {
        private StringBuilder builder;
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.builder = new StringBuilder();
            subscription.request(1);
        }

        @SneakyThrows
        @Override
        public void onNext(String content) {
            logger.debug(content);
            content = content.substring(content.indexOf(':') + 2);
            if (content.contentEquals("[DONE]")) {
                return;
            }
            ChatCompletionStreamResponse item = Utility.mapper.readValue(content, ChatCompletionStreamResponse.class);
            if (item.choices[0].finishReason != null && item.choices[0].finishReason.equals("length")) {
                builder.append("\nMessage is cut due to exceed of token.");
            } else if (item.choices[0].delta.content != null)
                builder.append(item.choices[0].delta.content);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            logger.error(throwable);
        }

        @Override
        public void onComplete() {
        }

        public String getContent() {
            logger.debug(builder.toString());
            return builder.toString();
        }
    }
}
