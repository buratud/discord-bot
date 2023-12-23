package com.buratud.data.openai.chat;

import com.buratud.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class ChatCompletionResponse {
    public String id;
    public String object;
    public long created;
    public String model;
    public List<Choice> choices;
    public Usage usage;

    public static ChatCompletionResponse fromJson(String json) throws JsonProcessingException {
        return Utility.mapper.readValue(json, ChatCompletionResponse.class);
    }
}