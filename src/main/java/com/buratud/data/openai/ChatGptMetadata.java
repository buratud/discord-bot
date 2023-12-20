package com.buratud.data.openai;

import com.buratud.data.MemberChannelData;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ChatGptMetadata extends MemberChannelData {
    public ChatGptMetadata(String guildId, String channelId, String memberId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
    }
    @SerializedName("system_message")
    private String systemMessage;

    @SerializedName("one_shot")
    private boolean oneShot;
}
