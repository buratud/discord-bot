package com.buratud.stores.dynamodb;

import com.buratud.entity.ai.AiChatMetadata;
import com.buratud.entity.ai.AiChatSession;
import com.buratud.entity.ai.ChatMessage;
import com.buratud.stores.ChatGpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AiStore implements ChatGpt {
    static final Logger logger = LogManager.getLogger(AiStore.class);
    static final DynamoDbEnhancedClient edb = Client.getInstance().getEdb();
    static final DynamoDbClient db = Client.getInstance().getDb();
    static final DynamoDbTable<AiChatMetadata> aiChatMetaTable =
            edb.table("discord-bot", TableSchema.fromBean(AiChatMetadata.class));
    static final DynamoDbTable<AiChatSession> aiChatSessionTable =
            edb.table("discord-bot", TableSchema.fromBean(AiChatSession.class));

    @Override
    public AiChatMetadata getMetadata(String guildId, String channelId, String userId) {
        Key key = Key.builder()
                .partitionValue(AiChatMetadata.type)
                .sortValue(AiChatMetadata.getSortKey(guildId, channelId, userId))
                .build();
        return aiChatMetaTable.getItem(key);
    }

    @Override
    public void createMetadata(AiChatMetadata item) {
        aiChatMetaTable.putItem(item);
    }

    @Override
    public AiChatMetadata updateMetadata(AiChatMetadata item) {
        return aiChatMetaTable.updateItem(item);
    }

    @Override
    public void deleteMetadata(String guildId, String channelId, String userId) {
        Key key = Key.builder()
                .partitionValue(AiChatMetadata.type)
                .sortValue(AiChatMetadata.getSortKey(guildId, channelId, userId))
                .build();
        aiChatMetaTable.deleteItem(key);
    }

    @Override
    public AiChatSession getSession(String guildId, String channelId, String userId, String id) {
        Key key = Key.builder()
                .partitionValue(AiChatSession.getPartitionKey(guildId, channelId, userId))
                .sortValue(id)
                .build();
        return aiChatSessionTable.getItem(key);
    }

    @Override
    public void createSession(AiChatSession item) {
        aiChatSessionTable.putItem(item);
    }

    @Override
    public AiChatSession appendHistory(AiChatSession item) {
        Map<String, AttributeValue> map = new HashMap<>();
        int last = item.getHistory().size() - 1;
        map.put("role", AttributeValue.builder().s(item.getHistory().get(last).getRole().getRole()).build());
        map.put("content", AttributeValue.builder().s(item.getHistory().get(last).getContent()).build());
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName("discord-bot")
                .key(Map.of("id", AttributeValue.builder().s(item.getId()).build(), "partition_key", AttributeValue.builder().s(item.getPartitionKey()).build()))
                .updateExpression("SET #ri = list_append(#ri, :val)")
                .expressionAttributeNames(Map.of("#ri", "history"))
                .expressionAttributeValues(Map.of(":val", AttributeValue.fromL(List.of(AttributeValue.fromM(map)))))
                .returnValues(ReturnValue.ALL_NEW)
                .build();
        UpdateItemResponse outcome = db.updateItem(updateItemRequest);
        // Convert outcome to Attendance
        return fromOutcomeAttribute(outcome.attributes());
    }

    @Override
    public AiChatSession updateSession(AiChatSession item) {
        return aiChatSessionTable.updateItem(item);
    }

    @Override
    public void deleteSession(String guildId, String channelId, String userId, String id) {
        Key key = Key.builder()
                .partitionValue(AiChatSession.getPartitionKey(guildId, channelId, userId))
                .sortValue(id)
                .build();
        aiChatSessionTable.deleteItem(key);
    }

    private AiChatSession fromOutcomeAttribute(Map<String, AttributeValue> map) {
        AiChatSession aiChatSession = new AiChatSession();
        aiChatSession.setId(map.get("id").s());
        aiChatSession.setGuildId(map.get("guild_id").s());
        aiChatSession.setChannelId(map.get("channel_id").s());
        aiChatSession.setMemberId(map.get("member_id").s());
        aiChatSession.setModel(map.get("model").s());
        aiChatSession.setSystemMessage(map.get("system_message").s());
        aiChatSession.setHistory(map.get("history").l().stream().map(
                s -> new ChatMessage(
                        s.m().get("role").s(),
                        s.m().get("content").s())).collect(Collectors.toList())
        );
        return aiChatSession;
    }
}
