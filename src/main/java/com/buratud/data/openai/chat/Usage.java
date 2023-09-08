package com.buratud.data.openai.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Usage {
    @Expose
    @SerializedName("prompt_tokens")
    public int promptTokens;
    @Expose
    @SerializedName("completion_tokens")
    public int completionTokens;
    @Expose
    @SerializedName("total_tokens")
    public int totalTokens;
}
    