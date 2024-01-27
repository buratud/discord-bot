package com.buratud.entity.openai;

import com.buratud.entity.MemberChannelData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class AiChatSession extends MemberChannelData {
    public AiChatSession(String guildId, String channelId, String memberId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
    }
    @JsonProperty("system_message")
    private String systemMessage;

    @JsonProperty("one_shot")
    private boolean oneShot;
}
