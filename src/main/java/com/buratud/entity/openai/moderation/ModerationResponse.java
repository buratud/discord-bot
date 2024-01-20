package com.buratud.entity.openai.moderation;

import com.buratud.Utility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;


public class ModerationResponse {
    @JsonProperty("id")
    public String id;

    @JsonProperty("model")
    public String model;

    @JsonProperty("results")
    public List<Result> results;

    public static ModerationResponse fromJson(String json) throws JsonProcessingException {
        return Utility.mapper.readValue(json, ModerationResponse.class);
    }
}
