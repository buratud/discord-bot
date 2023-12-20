package com.buratud.data.openai;

import com.buratud.data.MemberChannelData;
import com.google.gson.annotations.SerializedName;

public class ChatGptMetadata extends MemberChannelData {
    @SerializedName("system_message")
    public String systemMessage;
}
