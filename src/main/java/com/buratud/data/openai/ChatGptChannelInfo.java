package com.buratud.data.openai;

import com.buratud.data.MemberChannelData;
import com.buratud.data.openai.chat.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class ChatGptChannelInfo extends MemberChannelData {
    public ChatGptChannelInfo(String guildId, String channelId, String memberId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
        this.activated = false;
        this.history = new ArrayList<>();
    }
    private boolean activated;
    private String model;
    private List<ChatMessage> history;
}
