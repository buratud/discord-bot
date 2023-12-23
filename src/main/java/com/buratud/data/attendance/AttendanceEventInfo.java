package com.buratud.data.attendance;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@DynamoDbBean
@Getter
@Setter
public class AttendanceEventInfo {
    public AttendanceEventInfo() {

    }
    public AttendanceEventInfo(String userId, AttendanceEvent event) {
        this.userId = userId;
        this.event = event;
        this.dateTime = Instant.now();
    }
    @SerializedName("discord_id")
    public String userId;
    @SerializedName("event")
    public AttendanceEvent event;
    @SerializedName("datetime")
    public Instant dateTime;
}
