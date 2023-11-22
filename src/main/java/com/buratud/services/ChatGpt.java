package com.buratud.services;

import com.buratud.data.ChatGptChannelInfo;
import com.buratud.data.openai.chat.*;
import com.buratud.data.openai.moderation.ModerationResponse;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
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
        return store.get(channelId, userId);
    }

    public String send(String channelId, String userId, String message) throws IOException, InterruptedException {
        ChatGptChannelInfo info = store.get(channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.history.add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(info.model, info.history).build();
        ChatCompletionResponse response = client.sendChatCompletionRequest(request);
        String messageRes = response.choices.get(0).message.content;
        messageRes = messageRes.replace("\n\n", "\n");
        info.history.add(new ChatMessage(Role.ASSISTANT, messageRes));
        store.save(channelId, userId, info);
        return String.format("%s\n\nTotal tokens: %d", messageRes, response.usage.totalTokens);
    }

    public String sendStreamEnabled(String channelId, String userId, String message) throws InterruptedException, ExecutionException {
        ChatGptChannelInfo info = store.get(channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.history.add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(info.model, info.history).withStream(true).build();
        EventStreamSubscriber subscriber = new EventStreamSubscriber();
        client.sendChatCompletionRequestWithStreamEnabled(request, subscriber);
        String messageRes = subscriber.getContent();
        messageRes = messageRes.replace("\n\n", "\n");
        info.history.add(new ChatMessage(Role.ASSISTANT, messageRes));
        store.save(channelId, userId, info);
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
        ChatGptChannelInfo info = store.get(channelId, userId);
        boolean activated = false;
        if (info == null) {
            info = store.create(channelId, userId);
        } else {
            activated = info.activated;
            info = store.clear(channelId, userId);
        }
        info.model = DEFAULT_MODEL;
        info.activated = activated;
        info.history = new ArrayList<>();
        if (system != null) {
            info.history.add(system);
        }
        store.save(channelId, userId, info);
        return info;
    }

    public void SwitchModel(String channelId, String userId, String model) {
        ChatGptChannelInfo info = store.get(channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.model = model;
    }

    public void SetActivation(String channelId, String userId, Boolean activation) {
        ChatGptChannelInfo info = store.get(channelId, userId);
        if (info == null) {
            info = reset(channelId, userId);
        }
        info.activated = activation;
    }

    public class EventStreamSubscriber implements Flow.Subscriber<String> {
        private static final Gson gson = new Gson();
        private StringBuilder builder;
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.builder = new StringBuilder();
            subscription.request(1);
        }

        @Override
        public void onNext(String content) {
            logger.info(content);
            content = content.substring(content.indexOf(':') + 2);
            if (content.contentEquals("[DONE]")) {
                return;
            }
            ChatCompletionStreamResponse item = gson.fromJson(content, ChatCompletionStreamResponse.class);
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
            logger.info(builder.toString());
            return builder.toString();
        }
    }
}
