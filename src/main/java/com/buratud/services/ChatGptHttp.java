package com.buratud.services;

import com.buratud.entity.openai.chat.ChatCompletionRequest;
import com.buratud.entity.openai.chat.ChatCompletionResponse;
import com.buratud.entity.openai.moderation.ModerationRequest;
import com.buratud.entity.openai.moderation.ModerationResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class ChatGptHttp {
    private final HttpClient client;
    private final String apiKey;

    private static final String chatUrl = "https://api.openai.com/v1/chat/completions";
    private static final String moderationUrl = "https://api.openai.com/v1/moderations";
    private static final int timeoutSeconds = 5;
    public ChatGptHttp(String apiKey) {
        this.client = HttpClient.newBuilder().build();
        this.apiKey = apiKey;
    }

    public ChatCompletionResponse sendChatCompletionRequest(ChatCompletionRequest body) throws IOException, InterruptedException {

        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(chatUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> responseStr = client.send(request, BodyHandlers.ofString());

        return ChatCompletionResponse.fromJson(responseStr.body());
    }

    public HttpResponse<Object> sendChatCompletionRequestWithStreamEnabled(ChatCompletionRequest body, Flow.Subscriber<? super String> subscriber) throws InterruptedException, ExecutionException, IOException {

        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(chatUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "text/event-stream")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return client.send(request, BodyHandlers.fromLineSubscriber(subscriber, s -> null, "\n\n"));
    }

    public ModerationResponse moderateMessage(String message) throws IOException, InterruptedException {
        ModerationRequest moderationRequest = new ModerationRequest();
        moderationRequest.input = message;
        String jsonResult = moderationRequest.toJsonString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(moderationUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(BodyPublishers.ofString(jsonResult))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return ModerationResponse.fromJson(response.body());
    }
}