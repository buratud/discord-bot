package com.buratud.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Getter
@Setter
public class GuildData {
    @SerializedName("guild_id")
    protected String guildId;

    @DynamoDbAttribute("guild_id")
    public String getGuildId() {
        return guildId;
    }
}
