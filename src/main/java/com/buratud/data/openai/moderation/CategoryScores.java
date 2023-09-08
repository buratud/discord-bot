package com.buratud.data.openai.moderation;

import com.google.gson.annotations.SerializedName;

public class CategoryScores {
    @SerializedName("sexual")
    public double sexual;

    @SerializedName("hate")
    public double hate;

    @SerializedName("harassment")
    public double harassment;

    @SerializedName("self-harm")
    public double selfHarm;

    @SerializedName("sexual/minors")
    public double sexualMinors;

    @SerializedName("hate/threatening")
    public double hateThreatening;

    @SerializedName("violence/graphic")
    public double violenceGraphic;

    @SerializedName("self-harm/intent")
    public double selfHarmIntent;

    @SerializedName("self-harm/instructions")
    public double selfHarmInstructions;

    @SerializedName("harassment/threatening")
    public double harassmentThreatening;

    @SerializedName("violence")
    public double violence;
}