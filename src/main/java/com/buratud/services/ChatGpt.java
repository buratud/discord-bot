package com.buratud.services;

import com.buratud.Utility;
import com.buratud.entity.ai.FinishReason;
import com.buratud.entity.ai.PromptResponse;
import com.buratud.entity.openai.AiChatMetadata;
import com.buratud.entity.openai.chat.*;
import com.buratud.entity.openai.moderation.ModerationResponse;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class ChatGpt {
    private static final Logger logger = LogManager.getLogger(ChatGpt.class);
    private final ChatGptHttp client;

    public ChatGpt(String key) throws IOException {
        client = new ChatGptHttp(key);
    }

    public String moderationCheck(String message) throws IOException, InterruptedException {
        ModerationResponse response = client.moderateMessage(message);
        if (response.results.get(0).flagged) {
            for (Map.Entry<String, Boolean> item : response.results.get(0).categories.entrySet()) {
                if (item.getValue()) {
                    return item.getKey();
                }
            }
        }
        return null;
    }

    public PromptResponse sendStreamEnabled(AiChatMetadata info, List<ChatMessage> messages) throws InterruptedException, ExecutionException, IOException {
        String message = messages.get(messages.size()-1).content;
        String flagged = moderationCheck(message);
        if (flagged != null) {
            return new PromptResponse(true, String.format("Message was blocked due to %s", flagged), FinishReason.VIOLATION);
        }
        ChatCompletionRequest request = new ChatCompletionRequestBuilder(info.getModel(), messages).withStream(true).build();
        EventStreamSubscriber subscriber = new EventStreamSubscriber();
        client.sendChatCompletionRequestWithStreamEnabled(request, subscriber);
        String messageRes = subscriber.getContent();
        messageRes = messageRes.replace("\n\n", "\n");
        return new PromptResponse(false, String.format("%s", messageRes), FinishReason.NORMAL);
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
