package com.buratud.data.openai.chat;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatCompletionRequest {
    @Expose
    @SerializedName("model")
    public String model;

    @Expose
    @SerializedName("messages")
    public List<Message> messages;

    @Expose
    @SerializedName("temperature")
    public Float temperature;

    @Expose
    @SerializedName("top_p")
    public Float topP;

    @Expose
    @SerializedName("n")
    public Float n;

    @Expose
    @SerializedName("stream")
    public Boolean stream;

    @Expose
    @SerializedName("maxTokens")
    public Integer maxTokens;

    @Expose
    @SerializedName("presencePenalty")
    public Integer presencePenalty;

    @Expose
    @SerializedName("frequencyPenalty")
    public Integer frequencyPenalty;

    @Expose
    @SerializedName("user")
    public String user;

    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Role.class, new RoleSerializer()).create();
        return gson.toJson(this);
    }
}