package com.buratud.entity.openai.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Result {
    @JsonProperty("flagged")
    public boolean flagged;

    @JsonProperty("categories")
    public Map<String, Boolean> categories;

    @JsonProperty("category_scores")
    public Map<String, Float> categoryScores;
}