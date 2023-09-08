package com.buratud.data.openai.moderation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.google.gson.Gson;

public class ModerationResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("model")
    public String model;

    @SerializedName("results")
    public List<Result> results;

    public static ModerationResponse fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ModerationResponse.class);
    }
}
