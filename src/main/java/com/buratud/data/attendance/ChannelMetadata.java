package com.buratud.data.attendance;

import com.buratud.data.ChannelData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
public class ChannelMetadata extends ChannelData {
    public static String getPartitionKey(String guildId, String channelId) {
        return type;
    }

    public static String getSortKey(String guildId, String channelId) {
        return String.join("_", guildId, channelId);
    }

    @Expose(serialize = false)
    public static final String type = "attendance-metadata";

    public ChannelMetadata() {

    }

    public ChannelMetadata(String guildId, String channelId) {
        this.guildId = guildId;
        this.channelId = channelId;
    }


    @SerializedName("current_session")
    private UUID currentSession;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition_key")
    @SerializedName("partition_key")
    public String getPartitionKey() {
        return ChannelMetadata.getPartitionKey(guildId, channelId);
    }
    public void setPartitionKey(String partitionKey) {
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("id")
    public String getId() {
        return ChannelMetadata.getSortKey(guildId, channelId);
    }
    public void setId(String id) {
    }

    @DynamoDbAttribute("current_session")
    public UUID getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(UUID currentSession) {
        this.currentSession = currentSession;
    }
}
