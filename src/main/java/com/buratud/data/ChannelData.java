package com.buratud.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelData extends GuildData {
    @SerializedName("channel_id")
    protected String channelId;
}
