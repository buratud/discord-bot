package com.buratud.services;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

import com.buratud.data.openai.chat.ChatCompletionRequest;
import com.buratud.data.openai.chat.ChatCompletionRequestBuilder;
import com.buratud.data.openai.chat.ChatCompletionResponse;
import com.buratud.data.openai.chat.ChatMessage;
import com.buratud.data.openai.chat.Role;
import com.buratud.data.openai.moderation.ModerationResponse;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatGPT {
    private static final Logger logger = LogManager.getLogger(ChatGPT.class);
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

    public String sendStreamEnabled(String channelId, String userId, String message) throws IOException, InterruptedException, ExecutionException {
        List<ChatMessage> history = store.get(channelId, userId);
        if (history == null) {
            history = store.create(channelId, userId);
            if (system != null) {
                history.add(system);
            }
        }
        history.add(new ChatMessage(Role.USER, message));
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(DEFAULT_MODEL, history).withStream(true).build();
        EventStreamSubscriber subscriber = new EventStreamSubscriber();
        client.sendChatCompletionRequestWithStreamEnabled(request, subscriber);
        String messageRes = subscriber.getContent();
        messageRes = messageRes.replace("\n\n", "\n");
        history.add(new ChatMessage(Role.ASSISTANT, messageRes));
        store.save(channelId, userId, history);
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

    class EventStreamSubscriber implements Flow.Subscriber<String> {
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

            content = content.substring(content.indexOf(':') + 2);
            if (content.contentEquals("[DONE]")) {
                return;
            }
            ChatCompletionResponse item = gson.fromJson(content, ChatCompletionResponse.class);
            if (item.choices.get(0).message.content != null)
                builder.append(item.choices.get(0).message.content);
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
            return builder.toString();
        }
    }
}
