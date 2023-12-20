package com.buratud.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Getter
@Setter
public class ChannelData extends GuildData {
    @SerializedName("channel_id")
    protected String channelId;

    @DynamoDbAttribute("channel_id")
    public String getChannelId() {
        return channelId;
    }
}
