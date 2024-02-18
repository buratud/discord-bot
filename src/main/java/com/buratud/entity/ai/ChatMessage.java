package com.buratud.entity.ai;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnoreNulls;


@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public class ChatMessage {
    @JsonSerialize(using = RoleSerializer.class)
    private Role role;
    private String content;

    @DynamoDbIgnoreNulls
    public List<String> getImages() {
        return images;
    }

    @Setter
    private List<String> images;


    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatMessage(String role, String content) {
        this.role = Role.valueOf(role);
        this.content = content;
    }

    public ChatMessage(Role role, String content, List<String> images) {
        this.role = role;
        this.content = content;
        this.images = images;
    }

    public ChatMessage(String role, String content, List<String> images) {
        this.role = Role.valueOf(role);
        this.content = content;
        this.images = images;
    }
}