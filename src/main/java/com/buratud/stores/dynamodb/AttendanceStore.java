package com.buratud.stores.dynamodb;

import com.buratud.data.attendance.Attendance;
import com.buratud.data.attendance.ChannelMetadata;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.stream.Collectors;

public class AttendanceStore implements com.buratud.stores.AttendanceStore {
    static final DynamoDbClient db = DynamoDbClient.builder().credentialsProvider(ProfileCredentialsProvider.create("discord-bot")).build();
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
