package com.buratud.data.openai.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Choice {
    public int index;
    public ChatMessage message;
    @JsonProperty("finish_reason")
    public String finishReason;
}