package com.buratud.data.attendance;

import com.buratud.Utility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
public class AttendanceEventInfo {
    public AttendanceEventInfo(String userId, AttendanceEvent event) {
        this.userId = userId;
        this.event = event;
        this.dateTime = Instant.now();
    }
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("event")
    private AttendanceEvent event;
    
    @JsonProperty("datetime")
    private Instant dateTime;

    @DynamoDbAttribute("user_id")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("datetime")
    public Instant getDateTime() {
        return dateTime;
    }

    public String toJson() throws JsonProcessingException {
        return Utility.mapper.writeValueAsString(this);
    }
}
