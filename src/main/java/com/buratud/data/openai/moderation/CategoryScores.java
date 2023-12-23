package com.buratud.data.openai.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryScores {
    @JsonProperty("sexual")
    public double sexual;

    @JsonProperty("hate")
    public double hate;

    @JsonProperty("harassment")
    public double harassment;

    @JsonProperty("self-harm")
    public double selfHarm;

    @JsonProperty("sexual/minors")
    public double sexualMinors;

    @JsonProperty("hate/threatening")
    public double hateThreatening;

    @JsonProperty("violence/graphic")
    public double violenceGraphic;

    @JsonProperty("self-harm/intent")
    public double selfHarmIntent;

    @JsonProperty("self-harm/instructions")
    public double selfHarmInstructions;

    @JsonProperty("harassment/threatening")
    public double harassmentThreatening;

    @JsonProperty("violence")
    public double violence;
}