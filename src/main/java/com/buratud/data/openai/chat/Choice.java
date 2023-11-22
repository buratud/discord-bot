package com.buratud.data.openai.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Choice {
    @Expose
    public int index;
    @Expose
    public ChatMessage message;
    @Expose
    @SerializedName("finish_reason")
    public String finishReason;
}