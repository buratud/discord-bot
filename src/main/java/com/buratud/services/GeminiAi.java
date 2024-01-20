package com.buratud.services;

import com.buratud.Utility;
import com.buratud.entity.ai.FinishReason;
import com.buratud.entity.ai.PromptResponse;
import com.buratud.entity.openai.ChatGptChannelInfo;
import com.buratud.entity.openai.chat.ChatMessage;
import com.buratud.entity.googleai.ChatCompletionRequest;
import com.buratud.entity.googleai.ChatCompletionRequestBuilder;
import com.buratud.entity.googleai.ChatCompletionStreamResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class GeminiAi {
    private static final Logger logger = LogManager.getLogger(GeminiAi.class);
    private final GeminiHttp client;

    public GeminiAi(String key) throws IOException {
        client = new GeminiHttp(key);
    }

    public PromptResponse sendStreamEnabled(ChatGptChannelInfo info, List<ChatMessage> messages) throws InterruptedException, ExecutionException, IOException {
        ChatCompletionRequest request = new ChatCompletionRequestBuilder().withMessages(messages).build();
        EventStreamSubscriber subscriber = new EventStreamSubscriber();
        client.sendChatCompletionRequestWithStreamEnabled(request, subscriber);
        ChatCompletionStreamResponse[] responses = subscriber.getContent();
        StringBuilder builder = new StringBuilder();
        FinishReason finishReason = FinishReason.NORMAL;
        for (ChatCompletionStreamResponse response : responses) {
            if (response.getPromptFeedback() != null && Objects.equals(response.getPromptFeedback().getBlockReason(), "SAFETY")) {
                String reason = "";
                for (ChatCompletionStreamResponse.SafetyRating safetyRating : response.getPromptFeedback().getSafetyRatings()) {
                    if (Objects.equals(safetyRating.getProbability(), "HIGH")) {
                        reason = safetyRating.getCategory();
                        break;
                    }
                }
                return new PromptResponse(true, String.format("Message was blocked due to %s", reason), FinishReason.VIOLATION);
            }
            if (response.getCandidates()[0].getContent() != null) {
                builder.append(response.getCandidates()[0].getContent().getParts()[0].getText());
            }
            if (response.getCandidates()[0].getFinishReason() != null) {
                finishReason = switch (response.getCandidates()[0].getFinishReason()) {
                    case "MAX_TOKENS" -> FinishReason.MAX_TOKENS;
                    case "RECITATION" -> FinishReason.RECITATION;
                    case "OTHER" -> FinishReason.UNKNOWN;
                    default -> finishReason;
                };
            }
        }

        return new PromptResponse(false, builder.toString().replace("\n\n", "\n"), finishReason);
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
            builder.append(content);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            logger.error(throwable);
        }

        @Override
        public void onComplete() {
        }

        public ChatCompletionStreamResponse[] getContent() throws JsonProcessingException {
            return Utility.mapper.readValue(builder.toString(), ChatCompletionStreamResponse[].class);
        }
    }
}
