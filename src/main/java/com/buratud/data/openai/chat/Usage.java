package com.buratud.data.openai.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usage {
    @JsonProperty("prompt_tokens")
    public int promptTokens;
    @JsonProperty("completion_tokens")
    public int completionTokens;
    @JsonProperty("total_tokens")
    public int totalTokens;
}
    