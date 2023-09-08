package com.buratud.data.openai.moderation;

import com.google.gson.annotations.SerializedName;

public class Categories {
    @SerializedName("sexual")
    public boolean sexual;

    @SerializedName("hate")
    public boolean hate;

    @SerializedName("harassment")
    public boolean harassment;

    @SerializedName("self-harm")
    public boolean selfHarm;

    @SerializedName("sexual/minors")
    public boolean sexualMinors;

    @SerializedName("hate/threatening")
    public boolean hateThreatening;

    @SerializedName("violence/graphic")
    public boolean violenceGraphic;

    @SerializedName("self-harm/intent")
    public boolean selfHarmIntent;

    @SerializedName("self-harm/instructions")
    public boolean selfHarmInstructions;

    @SerializedName("harassment/threatening")
    public boolean harassmentThreatening;

    @SerializedName("violence")
    public boolean violence;
}