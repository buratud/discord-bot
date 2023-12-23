package com.buratud.stores.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.buratud.data.attendance.Attendance;
import com.buratud.data.attendance.AttendanceEventInfo;
import com.buratud.data.attendance.ChannelMetadata;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;
import java.util.stream.Collectors;

public class AttendanceStore implements com.buratud.stores.AttendanceStore {
    static final Logger logger = LogManager.getLogger(Attendance.class);
    static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new com.amazonaws.auth.profile.ProfileCredentialsProvider("discord-bot")).build();
    static final DynamoDB db = new DynamoDB(client);
    static final Table table = db.getTable("discord-bot");
    static final DynamoDbClient edbc = DynamoDbClient.builder().credentialsProvider(ProfileCredentialsProvider.create("discord-bot")).build();
    static final DynamoDbEnhancedClient edb = DynamoDbEnhancedClient.builder().dynamoDbClient(edbc).build();
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
        Map<String, String> map = new HashMap<>();
        AttendanceEventInfo info = item.getLog().get(item.getLog().size()-1);
        map.put("datetime", info.getDateTime().toString());
        map.put("event", info.getEvent().toString());
        map.put("user_id", info.getUserId());
        List<Map<String, String>> list = List.of(map);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("id", item.getId(), "partition_key", item.getPartitionKey())
                .withUpdateExpression("SET #ri = list_append(#ri, :val)").withNameMap(new NameMap().with("#ri", "log"))
                .withValueMap(new ValueMap().withList(":val", map)).withReturnValues(ReturnValue.ALL_NEW);
        UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        return Attendance.fromJson(outcome.getItem().toJSON());
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
}
