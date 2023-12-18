package com.buratud.data.attendance;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import com.buratud.data.ChannelData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
public class Attendance extends ChannelData {
    public static String getPartitionKey(String guildId, String channelId) {
        return String.join("_", guildId, channelId, type);
    }

    public static final String type = "attendance";

    public Attendance() {

    }

    public Attendance(String guildId, String channelId, UUID id) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.id = id;
        this.log = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    @SerializedName("id")
    private UUID id;

    @DynamoDbAttribute("log")
    public List<AttendanceEventInfo> getLog() {
        return log;
    }

    @DynamoDbAttribute("start_time")
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @DynamoDbAttribute("end_time")
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @SerializedName("initiator_id")
    private String initiatorId;
    @SerializedName("log")
    private List<AttendanceEventInfo> log;
    @SerializedName("start_time")
    private LocalDateTime startTime;
    @SerializedName("end_time")
    private LocalDateTime endTime;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition_key")
    @SerializedName("partition_key")
    public String getPartitionKey() {
        return String.join("_", guildId, channelId, type);
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

    @DynamoDbAttribute("initiator_id")

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId =  initiatorId;
    }
}
