package com.buratud.stores.dynamodb;

import com.buratud.Env;
import com.buratud.data.attendance.Attendance;
import com.buratud.data.attendance.AttendanceEvent;
import com.buratud.data.attendance.AttendanceEventInfo;
import com.buratud.data.attendance.ChannelMetadata;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceStore implements com.buratud.stores.AttendanceStore {
    static final Logger logger = LogManager.getLogger(Attendance.class);
    static final DynamoDbClient db = DynamoDbClient.builder().region(Region.of(Env.AWS_REGION)).credentialsProvider(() -> AwsBasicCredentials.create(Env.AWS_ACCESS_KEY, Env.AWS_SECRET_ACCESS_KEY)).build();
    static final DynamoDbEnhancedClient edb = DynamoDbEnhancedClient.builder().dynamoDbClient(db).build();
    static final DynamoDbTable<Attendance> attendanceTable =
            edb.table("discord-bot", TableSchema.fromBean(Attendance.class));
    static final DynamoDbTable<ChannelMetadata> attendanceMetadataTable =
            edb.table("discord-bot", TableSchema.fromBean(ChannelMetadata.class));

    @Override
    public void createAttendance(Attendance item) {
        attendanceTable.putItem(item);
    }

    @Override
    public Attendance readAttendance(String guildId, String channelId, String id) {
        Key key = Key.builder()
                .partitionValue(Attendance.getPartitionKey(guildId, channelId))
                .sortValue(id)
                .build();
        return attendanceTable.getItem(key);
    }

    @Override
    public List<Attendance> readAllAttendance(String guildId, String channelId) {
        PageIterable<Attendance> pages = attendanceTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(Attendance.getPartitionKey(guildId, channelId)).build()));
        return pages.stream().flatMap(s -> s.items().stream()).collect(Collectors.toList());
    }

    @Override
    public Attendance updateAttendance(Attendance item) {
        return attendanceTable.updateItem(item);
    }

    @SneakyThrows
    @Override
    public Attendance addEvent(Attendance item) {
        Map<String, AttributeValue> map = new HashMap<>();
        AttendanceEventInfo info = item.getLog().get(item.getLog().size() - 1);
        map.put("datetime", AttributeValue.builder().s(info.getDateTime().toString()).build());
        map.put("event", AttributeValue.builder().s(info.getEvent().toString()).build());
        map.put("user_id", AttributeValue.builder().s(info.getUserId()).build());
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName("discord-bot")
                .key(Map.of("id", AttributeValue.builder().s(item.getId()).build(), "partition_key", AttributeValue.builder().s(item.getPartitionKey()).build()))
                .updateExpression("SET #ri = list_append(#ri, :val)")
                .expressionAttributeNames(Map.of("#ri", "log"))
                .expressionAttributeValues(Map.of(":val", AttributeValue.fromL(List.of(AttributeValue.fromM(map)))))
                .returnValues(ReturnValue.ALL_NEW)
                .build();
        UpdateItemResponse outcome = db.updateItem(updateItemRequest);
        // Convert outcome to Attendance
        return fromOutcomeAttribute(outcome.attributes());
    }

    @Override
    public void deleteAttendance(String guildId, String channelId, String id) {
        Key key = Key.builder().partitionValue(Attendance.getPartitionKey(guildId, channelId)).sortValue(id).build();
        attendanceTable.deleteItem(key);
    }

    @Override
    public void createAttendanceMetadata(ChannelMetadata item) {
        attendanceMetadataTable.putItem(item);
    }

    @Override
    public ChannelMetadata readAttendanceMetadata(String guildId, String channelId) {
        Key key = Key.builder().partitionValue(ChannelMetadata.getPartitionKey(guildId, channelId)).sortValue(ChannelMetadata.getSortKey(guildId, channelId)).build();
        return attendanceMetadataTable.getItem(key);
    }

    @Override
    public ChannelMetadata updateAttendanceMetadata(ChannelMetadata item) {
        return attendanceMetadataTable.updateItem(item);
    }

    @Override
    public void deleteAttendance(String guildId, String channelId) {
        Key key = Key.builder().partitionValue(ChannelMetadata.getPartitionKey(guildId, channelId)).build();
        attendanceMetadataTable.deleteItem(key);
    }

    private Attendance fromOutcomeAttribute(Map<String, AttributeValue> map) {
        Attendance attendance = new Attendance();
        attendance.setId(map.get("id").s());
        attendance.setGuildId(map.get("guild_id").s());
        attendance.setChannelId(map.get("channel_id").s());
        attendance.setInitiatorId(map.get("initiator_id").s());
        attendance.setStartTime(Instant.parse(map.get("start_time").s()));
        if (map.containsKey("end_time")) {
            attendance.setEndTime(Instant.parse(map.get("end_time").s()));
        }
        attendance.setLog(map.get("log").l().stream().map(s -> {
            AttendanceEventInfo info = new AttendanceEventInfo();
            info.setDateTime(Instant.parse(s.m().get("datetime").s()));
            info.setEvent(AttendanceEvent.valueOf(s.m().get("event").s()));
            info.setUserId(s.m().get("user_id").s());
            return info;
        }).collect(Collectors.toList()));
        return attendance;
    }
}
