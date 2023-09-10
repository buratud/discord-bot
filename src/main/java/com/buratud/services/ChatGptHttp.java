package com.buratud.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.buratud.data.openai.chat.ChatCompletionRequest;
import com.buratud.data.openai.chat.ChatCompletionResponse;
import com.buratud.data.openai.chat.Role;
import com.buratud.data.openai.chat.RoleSerializer;
import com.buratud.data.openai.moderation.ModerationResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatGptHttp {

    private final HttpClient client;
    private final String apiKey;

    Gson gson;

    private static final String chatUrl = "https://api.openai.com/v1/chat/completions";
    private static final String moderationUrl = "https://api.openai.com/v1/moderations";

    public ChatGptHttp(String apiKey) {
        this.client = HttpClient.newBuilder().build();
        this.apiKey = apiKey;
        gson = new GsonBuilder().registerTypeAdapter(Role.class, new RoleSerializer()).create();
    }

    public ChatCompletionResponse sendChatCompletionRequest(ChatCompletionRequest body)
            throws IOException, InterruptedException {

        String requestBody = gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(chatUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> responseStr = client.send(request, BodyHandlers.ofString());
        ChatCompletionResponse response = ChatCompletionResponse.fromJson(responseStr.body());

        return response;
    }

    public ModerationResponse moderateMessage(String message) throws IOException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input", message);
        String jsonResult = jsonObject.toString();

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(moderationUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(BodyPublishers.ofString(jsonResult))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        ModerationResponse moderationResponse = gson.fromJson(response.body(), ModerationResponse.class);
        return moderationResponse;
    }
}