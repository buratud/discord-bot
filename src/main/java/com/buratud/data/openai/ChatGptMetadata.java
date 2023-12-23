package com.buratud.data.openai;

import com.buratud.data.MemberChannelData;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("system_message")
    private String systemMessage;

    @JsonProperty("one_shot")
    private boolean oneShot;
}
