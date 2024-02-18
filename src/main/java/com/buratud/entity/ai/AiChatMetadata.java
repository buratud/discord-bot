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

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public final class AiChatMetadata extends MemberChannelData {
    public static final String type = "ai-metadata";

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition_key")
    public String getPartitionKey() {
        return type;
    }

    public void setPartitionKey(String partitionKey) {
    }

    public static String getSortKey(String guildId, String channelId, String memberId) {
        return String.join("_", guildId, channelId, memberId);
    }

    public AiChatMetadata(String guildId, String channelId, String memberId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
        this.activated = false;
        this.oneShot = false;
        this.currentSession = null;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("id")
    public String getId() {
        return AiChatMetadata.getSortKey(guildId, channelId, memberId);
    }

    public void setId(String id) {
    }

    @JsonProperty("system_message")
    private String systemMessage;

    @JsonProperty("model")
    private String model;

    @JsonProperty("activated")
    private boolean activated;

    @JsonProperty("one_shot")
    private boolean oneShot;

    @JsonProperty("current_session")
    private UUID currentSession;

    @DynamoDbAttribute("one_shot")
    public Boolean getOneShot() {
        return oneShot;
    }

    public void setOneShot(Boolean oneShot) {
        this.oneShot = oneShot;
    }

    @DynamoDbAttribute("current_session")
    public UUID getCurrentSession() {
        return currentSession;
    }
}
