package com.buratud.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Getter
@Setter
public class GuildData {
    @JsonProperty("guild_id")
    protected String guildId;

    @DynamoDbAttribute("guild_id")
    public String getGuildId() {
        return guildId;
    }
}
