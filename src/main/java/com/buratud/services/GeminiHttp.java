package com.buratud.services;

import com.buratud.entity.googleai.ChatCompletionRequest;

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

public class GeminiHttp {
    private final HttpClient client;
    private final String apiKey;

    private final String streamChatUrl;
    private static final int timeoutSeconds = 5;
    private final String streamImageChatUrl;

    public GeminiHttp(String apiKey) {
        this.client = HttpClient.newBuilder().build();
        this.apiKey = apiKey;
        this.streamChatUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:streamGenerateContent?key=%s", apiKey);
        this.streamImageChatUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.0-pro-vision-latest:streamGenerateContent?key=%s", apiKey);
    }

    public HttpResponse<Object> sendChatCompletionRequestWithStreamEnabled(ChatCompletionRequest body, Flow.Subscriber<? super String> subscriber) throws InterruptedException, IOException {
        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(streamChatUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return client.send(request, BodyHandlers.fromLineSubscriber(subscriber, s -> null, "\n"));
    }

    public HttpResponse<Object> sendImageChatCompletionRequestWithStreamEnabled(ChatCompletionRequest body, Flow.Subscriber<? super String> subscriber) throws InterruptedException, IOException {
        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(streamImageChatUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return client.send(request, BodyHandlers.fromLineSubscriber(subscriber, s -> null, "\n"));
    }

    public HttpResponse<Object> sendChatCompletionWithImageRequestWithStreamEnabled(ChatCompletionRequest body, Flow.Subscriber<? super String> subscriber) throws InterruptedException, IOException {
        String requestBody = body.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(streamChatUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return client.send(request, BodyHandlers.fromLineSubscriber(subscriber, s -> null, "\n"));
    }
}