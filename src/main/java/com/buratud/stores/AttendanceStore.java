package com.buratud.stores;

import com.buratud.entity.attendance.Attendance;
import com.buratud.entity.attendance.ChannelMetadata;

import java.util.List;

public interface AttendanceStore {
    void createAttendance(Attendance item);

    Attendance readAttendance(String guildId, String channelId, String id);
    List<Attendance> readAllAttendance(String guildId, String channelId);

    Attendance updateAttendance(Attendance item);

    Attendance addEvent(Attendance item);

    void deleteAttendance(String guildId, String channelId, String id);

    void createAttendanceMetadata(ChannelMetadata item);

    ChannelMetadata readAttendanceMetadata(String guildId, String channelId);

    ChannelMetadata updateAttendanceMetadata(ChannelMetadata item);

    void deleteAttendance(String guildId, String channelId);
}
