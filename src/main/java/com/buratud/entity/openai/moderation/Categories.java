package com.buratud.entity.openai.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Categories {
    @JsonProperty("sexual")
    public boolean sexual;

    @JsonProperty("hate")
    public boolean hate;

    @JsonProperty("harassment")
    public boolean harassment;

    @JsonProperty("self-harm")
    public boolean selfHarm;

    @JsonProperty("sexual/minors")
    public boolean sexualMinors;

    @JsonProperty("hate/threatening")
    public boolean hateThreatening;

    @JsonProperty("violence/graphic")
    public boolean violenceGraphic;

    @JsonProperty("self-harm/intent")
    public boolean selfHarmIntent;

    @JsonProperty("self-harm/instructions")
    public boolean selfHarmInstructions;

    @JsonProperty("harassment/threatening")
    public boolean harassmentThreatening;

    @JsonProperty("violence")
    public boolean violence;
}