package com.buratud.data.openai.chat;

import com.buratud.Utility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;


public class ChatCompletionRequest {
    
    @JsonProperty("model")
    public String model;

    
    @JsonProperty("messages")
    public List<ChatMessage> messages;

    
    @JsonProperty("temperature")
    public Float temperature;

    
    @JsonProperty("top_p")
    public Float topP;

    
    @JsonProperty("n")
    public Float n;

    
    @JsonProperty("stream")
    public Boolean stream;

    
    @JsonProperty("max_tokens")
    public Integer maxTokens;

    
    @JsonProperty("presence_penalty")
    public Integer presencePenalty;

    
    @JsonProperty("frequency_penalty")
    public Integer frequencyPenalty;

    
    @JsonProperty("user")
    public String user;

    public String toJson() throws JsonProcessingException {
       return  Utility.mapper.writeValueAsString(this);
    }
}