package com.buratud.services;

import com.buratud.data.attendance.Attendance;
import com.buratud.data.attendance.AttendanceEventInfo;
import com.buratud.data.attendance.ChannelMetadata;
import com.buratud.stores.dynamodb.AttendanceStore;
import org.apache.hc.core5.http.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

public class AttendanceService {
    private static final Logger logger = LogManager.getLogger(AttendanceService.class);
    private final AttendanceStore store;

    public AttendanceService() {
        store = new AttendanceStore();
    }

    // Return session id if success
    public String StartAttendance(String guildId, String channelId, String initiatorId) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        UUID id = UUID.randomUUID();
        if (metadata == null) {
            metadata = new ChannelMetadata(guildId, channelId);
        }
        if (metadata.getCurrentSession() == null) {
            logger.debug(guildId, channelId);
            metadata.setCurrentSession(id);
            Attendance attendance = new Attendance(guildId, channelId, id);
            attendance.setInitiatorId(initiatorId);
            store.createAttendanceMetadata(metadata);
            store.createAttendance(attendance);
            return id.toString();
        } else {
            return null;
        }
    }

    public Path GenerateCurrentAttendance(String guildId, String channelId) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        if (metadata == null || metadata.getCurrentSession() == null) {
            return null;
        }
        Attendance attendance = store.readAttendance(guildId, channelId, metadata.getCurrentSession().toString());
        return null;
    }

    // Return session id if success
    public String EndAttendance(String guildId, String channelId) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        if (metadata == null || metadata.getCurrentSession() == null) {
            return null;
        }
        UUID id = metadata.getCurrentSession();
        metadata.setCurrentSession(null);
        Attendance attendance = store.readAttendance(guildId, channelId, id.toString());
        attendance.setEndTime(LocalDateTime.now());
        store.updateAttendanceMetadata(metadata);
        store.updateAttendance(attendance);
        return id.toString();
    }

    public String EndAttendance(Attendance attendance) {
        ChannelMetadata metadata = store.readAttendanceMetadata(attendance.getGuildId(), attendance.getChannelId());
        if (metadata == null || metadata.getCurrentSession() == null) {
            return null;
        }
        UUID id = metadata.getCurrentSession();
        metadata.setCurrentSession(null);
        attendance.setEndTime(LocalDateTime.now());
        store.updateAttendanceMetadata(metadata);
        store.updateAttendance(attendance);
        return id.toString();
    }

    public Attendance GetCurrentAttendance(String guildId, String channelId) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        if (metadata == null || metadata.getCurrentSession() == null) {
            return null;
        }
        return store.readAttendance(guildId, channelId, metadata.getCurrentSession().toString());
    }

    public Path GenerateAttendanceHistory(String guildId, String channelId) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean AddEvent(String guildId, String channelId, String userId, AttendanceEventInfo event) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        if (metadata == null || metadata.getCurrentSession() == null) {
            return false;
        }
        UUID id = metadata.getCurrentSession();
        Attendance attendance = store.readAttendance(guildId, channelId, id.toString());
        attendance.getLog().add(event);
        store.updateAttendance(attendance);
        return true;
    }
}
