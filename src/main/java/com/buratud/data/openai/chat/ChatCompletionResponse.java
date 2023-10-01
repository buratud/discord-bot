package com.buratud.data.openai.chat;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatCompletionResponse {
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("object")
    public String object;
    @Expose
    @SerializedName("created")
    public long created;
    @Expose
    @SerializedName("model")
    public String model;
    @Expose
    @SerializedName("choices")
    public List<Choice> choices;
    @Expose
    @SerializedName("usage")
    public Usage usage;

    public static ChatCompletionResponse fromJson(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Role.class, new RoleSerializer()).create();
        return gson.fromJson(json, ChatCompletionResponse.class);
    }
}