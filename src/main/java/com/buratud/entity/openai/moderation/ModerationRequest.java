package com.buratud.entity.openai.moderation;


import com.buratud.Utility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ModerationRequest {
    @JsonProperty("input")
    public String input;

    public String toJsonString() throws JsonProcessingException {
        return Utility.mapper.writeValueAsString(this);
    }
}
