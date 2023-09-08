package com.buratud.data.openai.chat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class ChatCompletionResponse {
    @Expose
    @JsonProperty("id")
    public String id;
    @Expose
    @JsonProperty("object")
    public String object;
    @Expose
    @JsonProperty("created")
    public long created;
    @Expose
    @JsonProperty("model")
    public String model;
    @Expose
    @JsonProperty("choices")
    public List<Choice> choices;
    @Expose
    @JsonProperty("usage")
    public Usage usage;

    public static ChatCompletionResponse fromJson(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Role.class, new RoleSerializer()).create();
        return gson.fromJson(json, ChatCompletionResponse.class);
    }
}