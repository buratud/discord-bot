package com.buratud.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Setter
public class MemberChannelData extends ChannelData {
    @JsonProperty("member_id")
    protected String memberId;

    @DynamoDbAttribute("member_id")
    public String getMemberId() {
        return memberId;
    }
}
