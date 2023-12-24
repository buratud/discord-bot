package com.buratud.services;

import com.buratud.data.openai.chat.ChatCompletionResponse;
import com.buratud.data.openai.moderation.ModerationRequest;
import com.buratud.data.openai.moderation.ModerationResponse;
import com.buratud.googleai.ChatCompletionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class GeminiHttp {
    private final HttpClient client;
    private final String apiKey;

    private final String streamChatUrl;
    private static final int timeoutSeconds = 5;
    public GeminiHttp(String apiKey) {
        this.client = HttpClient.newBuilder().build();
        this.apiKey = apiKey;
        this.streamChatUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:streamGenerateContent?key=%s", apiKey);
    }

    public CompletableFuture<HttpResponse<Object>> sendChatCompletionRequestWithStreamEnabled(ChatCompletionRequest body, Flow.Subscriber<? super String> subscriber) throws InterruptedException, ExecutionException, JsonProcessingException {
        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(streamChatUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        CompletableFuture<HttpResponse<Object>> responseFuture = client.sendAsync(request, BodyHandlers.fromLineSubscriber(subscriber, s -> null, "\n"));
        responseFuture.get();
        return responseFuture;
    }
}