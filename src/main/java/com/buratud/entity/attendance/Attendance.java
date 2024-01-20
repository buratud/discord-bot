package com.buratud.entity.attendance;

import com.buratud.Utility;
import com.buratud.entity.ChannelData;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
public class Attendance extends ChannelData {
    public static String getPartitionKey(String guildId, String channelId) {
        return String.join("_", guildId, channelId, type);
    }

    public static final String type = "attendance";

    public static Attendance fromJson(String json) throws JsonProcessingException {
        return Utility.mapper.readValue(json, Attendance.class);
    }

    public Attendance(String guildId, String channelId, UUID id) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.id = id;
        this.log = new ArrayList<>();
        this.startTime = Instant.now();
    }

    @JsonProperty("id")
    private UUID id;

    @DynamoDbAttribute("log")
    public List<AttendanceEventInfo> getLog() {
        return log;
    }

    @DynamoDbAttribute("start_time")
    public Instant getStartTime() {
        return startTime;
    }

    @DynamoDbAttribute("end_time")
    public Instant getEndTime() {
        return endTime;
    }

    @JsonProperty("initiator_id")
    private String initiatorId;
    @JsonProperty("log")
    private List<AttendanceEventInfo> log;
    @JsonProperty("start_time")
    private Instant startTime;
    @JsonProperty("end_time")
    private Instant endTime;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partition_key")
    @JsonProperty("partition_key")
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
}
