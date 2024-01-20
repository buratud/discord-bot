package com.buratud.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Getter
@Setter
public class ChannelData extends GuildData {
    @JsonProperty("channel_id")
    protected String channelId;

    @DynamoDbAttribute("channel_id")
    public String getChannelId() {
        return channelId;
    }
}
