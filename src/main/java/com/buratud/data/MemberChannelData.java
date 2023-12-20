package com.buratud.data;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Setter
public class MemberChannelData extends ChannelData {
    @SerializedName("member_id")
    protected String memberId;

    @DynamoDbAttribute("member_id")
    public String getMemberId() {
        return memberId;
    }
}
