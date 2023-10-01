package com.buratud.data.openai.moderation;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModerationRequest {
    static {
        gson = new Gson();
    }
    private static final Gson gson;
    @Expose
    @SerializedName("input")
    public String input;

    public String toJsonString() {
        return gson.toJson(this);
    }
}
