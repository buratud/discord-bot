package com.buratud.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuildData {
    @SerializedName("guild_id")
    protected String guildId;
}
