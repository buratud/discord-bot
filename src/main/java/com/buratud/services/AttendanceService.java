package com.buratud.services;

import com.buratud.data.attendance.Attendance;
import com.buratud.data.attendance.AttendanceEventInfo;
import com.buratud.data.attendance.ChannelMetadata;
import com.buratud.stores.dynamodb.AttendanceStore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.apache.hc.core5.http.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

import java.io.File;
import java.nio.channels.Channel;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

    public Path GenerateCurrentAttendance(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        List<Member> members = channel.getMembers();
        try (OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            OdfTable table = document.getSpreadsheetTables().get(0);
            table.setTableName("Attendance");
            table.getCellByPosition(0, 0).setStringValue("Guild ID");
            table.getCellByPosition(1, 0).setStringValue(guild.getId());
            table.getCellByPosition(0, 1).setStringValue("Guild Name");
            table.getCellByPosition(1, 1).setStringValue(guild.getName());
            table.getCellByPosition(0, 2).setStringValue("Channel ID");
            table.getCellByPosition(1, 2).setStringValue(channel.getId());
            table.getCellByPosition(0, 3).setStringValue("Channel Name");
            table.getCellByPosition(1, 3).setStringValue(channel.getName());
            table.getCellByPosition(0, 4).setStringValue("Timestamp");
            table.getCellByPosition(1, 4).setStringValue(LocalDateTime.now().toString());
            table.getCellByPosition(0, 5).setStringValue("User ID");
            table.getCellByPosition(1, 5).setStringValue("Username");
            table.getCellByPosition(2, 5).setStringValue("Nickname");
            for (int i = 0; i < members.size(); i++) {
                int row = i + 6;
                Member member = members.get(i);
                table.getCellByPosition(0, row).setStringValue(member.getId());
                table.getCellByPosition(1, row).setStringValue(member.getEffectiveName());
                table.getCellByPosition(2, row).setStringValue(member.getNickname());
            }
            File file = File.createTempFile(String.format("%s_", guild.getId()), ".ods");
            document.save(file);
            return file.toPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
