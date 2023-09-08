package com.buratud.data.openai.moderation;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("flagged")
    public boolean flagged;

    @SerializedName("categories")
    public Map<String, Boolean> categories;

    @SerializedName("category_scores")
    public Map<String, Float> categoryScores;
}