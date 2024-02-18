package com.buratud.entity.ai;

import com.buratud.entity.MemberChannelData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public final class AiChatSession extends MemberChannelData {
    public static final String type = "ai-session";

    public static String getPartitionKey(String guildId, String channelId, String memberId) {
        return String.join("_", guildId, channelId, memberId, type);
    }

    public AiChatSession(String guildId, String channelId, String memberId, UUID id) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
        this.id = id;
        this.history = new ArrayList<>();
    }

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("model")
    private String model;

    @JsonProperty("system_message")
    private String systemMessage;

    @JsonProperty("history")
    private List<ChatMessage> history;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition_key")
    public String getPartitionKey() {
        return getPartitionKey(guildId, channelId, memberId);
    }

    public void setPartitionKey(String partitionKey) {
    }

    @DynamoDbSortKey
    public String getId() {
        return this.id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    @DynamoDbAttribute("model")
    public String getModel() {
        return model;
    }

    @DynamoDbAttribute("system_message")
    public String getSystemMessage() {
        return systemMessage;
    }

    @DynamoDbAttribute("history")
    public List<ChatMessage> getHistory() {
        return history;
    }
}
