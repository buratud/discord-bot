package com.buratud.services;

import com.buratud.entity.attendance.Attendance;
import com.buratud.entity.attendance.AttendanceEvent;
import com.buratud.entity.attendance.AttendanceEventInfo;
import com.buratud.entity.attendance.ChannelMetadata;
import com.buratud.stores.dynamodb.AttendanceStore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    public String StartAttendance(String guildId, String channelId, String initiatorId, List<Member> members) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        UUID id = UUID.randomUUID();
        if (metadata == null) {
            metadata = new ChannelMetadata(guildId, channelId);
        }
        if (metadata.getCurrentSession() == null) {
            metadata.setCurrentSession(id);
            Attendance attendance = new Attendance(guildId, channelId, id);
            attendance.setInitiatorId(initiatorId);
            for (Member member : members) {
                AttendanceEventInfo info = new AttendanceEventInfo(member.getId(), AttendanceEvent.IN);
                info.setDateTime(attendance.getStartTime());
                attendance.getLog().add(info);
            }
            store.createAttendanceMetadata(metadata);
            store.createAttendance(attendance);
            return id.toString();
        } else {
            return null;
        }
    }

    public Path GenerateCurrentAttendance(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        List<Member> members = guild.getMembers();
        ZonedDateTime currentDatetime = ZonedDateTime.now(ZoneId.of("Asia/Bangkok"));
        String currentDate = currentDatetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String currentTime = currentDatetime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        try (OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            OdfTable.newTable(document);
            OdfTable attTable = document.getSpreadsheetTables().get(0);
            attTable.setTableName("Attendance");
            attTable.getCellByPosition(0, 0).setStringValue("Guild ID");
            attTable.getCellByPosition(1, 0).setStringValue(guild.getId());
            attTable.getCellByPosition(0, 1).setStringValue("Guild Name");
            attTable.getCellByPosition(1, 1).setStringValue(guild.getName());
            attTable.getCellByPosition(0, 2).setStringValue("Channel ID");
            attTable.getCellByPosition(1, 2).setStringValue(channel.getId());
            attTable.getCellByPosition(0, 3).setStringValue("Channel Name");
            attTable.getCellByPosition(1, 3).setStringValue(channel.getName());
            attTable.getCellByPosition(0, 4).setStringValue("Date");
            attTable.getCellByPosition(1, 4).setStringValue(currentDate);
            attTable.getCellByPosition(2, 4).setStringValue("Time");
            attTable.getCellByPosition(3, 4).setStringValue(currentTime);
            attTable.getCellByPosition(0, 5).setStringValue("User ID");
            attTable.getCellByPosition(1, 5).setStringValue("Username");
            attTable.getCellByPosition(2, 5).setStringValue("Nickname");
            OdfTable absTable = document.getSpreadsheetTables().get(1);
            absTable.setTableName("Absence");
            absTable.getCellByPosition(0, 0).setStringValue("Guild ID");
            absTable.getCellByPosition(1, 0).setStringValue(guild.getId());
            absTable.getCellByPosition(0, 1).setStringValue("Guild Name");
            absTable.getCellByPosition(1, 1).setStringValue(guild.getName());
            absTable.getCellByPosition(0, 2).setStringValue("Channel ID");
            absTable.getCellByPosition(1, 2).setStringValue(channel.getId());
            absTable.getCellByPosition(0, 3).setStringValue("Channel Name");
            absTable.getCellByPosition(1, 3).setStringValue(channel.getName());
            absTable.getCellByPosition(0, 4).setStringValue("Date");
            absTable.getCellByPosition(1, 4).setStringValue(currentDate);
            absTable.getCellByPosition(2, 4).setStringValue("Time");
            absTable.getCellByPosition(3, 4).setStringValue(currentTime);
            absTable.getCellByPosition(0, 5).setStringValue("User ID");
            absTable.getCellByPosition(1, 5).setStringValue("Username");
            absTable.getCellByPosition(2, 5).setStringValue("Nickname");
            int attIndex = 6, absRow = 6;
            for (Member member : members) {
                if (member.getUser().isBot()) {
                    continue;
                }
                GuildVoiceState state = member.getVoiceState();
                if (state == null || !Objects.equals(state.getChannel(), channel)) {
                    absTable.getCellByPosition(0, absRow).setStringValue(member.getId());
                    absTable.getCellByPosition(1, absRow).setStringValue(member.getEffectiveName());
                    absTable.getCellByPosition(2, absRow).setStringValue(member.getNickname());
                    absRow++;
                } else {
                    attTable.getCellByPosition(0, attIndex).setStringValue(member.getId());
                    attTable.getCellByPosition(1, attIndex).setStringValue(member.getEffectiveName());
                    attTable.getCellByPosition(2, attIndex).setStringValue(member.getNickname());
                    attIndex++;
                }
            }
            Path path = Path.of(System.getProperty("java.io.tmpdir"), String.format("%s_%s.ods", channel.getName(), currentDatetime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))));
            document.save(path.toFile());
            document.close();
            return path;
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
        attendance.setEndTime(Instant.now());
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
        attendance.setEndTime(Instant.now());
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

    public Path GenerateAttendanceHistory(JDA jda, Attendance attendance) throws Exception {
        Guild guild = Objects.requireNonNull(jda.getGuildById(attendance.getGuildId()));
        VoiceChannel channel = Objects.requireNonNull(guild.getChannelById(VoiceChannel.class, attendance.getChannelId()));
        OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument();
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
        table.getCellByPosition(0, 4).setStringValue("Session ID");
        table.getCellByPosition(1, 4).setStringValue(attendance.getId());
        table.getCellByPosition(0, 5).setStringValue("Start Date");
        table.getCellByPosition(1, 5).setStringValue(ZonedDateTime.ofInstant(attendance.getStartTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        table.getCellByPosition(0, 5).setStringValue("Start Time");
        table.getCellByPosition(1, 5).setStringValue(ZonedDateTime.ofInstant(attendance.getStartTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        table.getCellByPosition(0, 6).setStringValue("End Time");
        table.getCellByPosition(1, 6).setStringValue(ZonedDateTime.ofInstant(attendance.getEndTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        table.getCellByPosition(0, 6).setStringValue("End Time");
        table.getCellByPosition(1, 6).setStringValue(ZonedDateTime.ofInstant(attendance.getEndTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        table.getCellByPosition(0, 7).setStringValue("Date");
        table.getCellByPosition(1, 7).setStringValue("Time");
        table.getCellByPosition(2, 7).setStringValue("User ID");
        table.getCellByPosition(3, 7).setStringValue("Username");
        table.getCellByPosition(3, 7).setStringValue("Nickname");
        table.getCellByPosition(5, 7).setStringValue("Event");
        for (int i = 0; i < attendance.getLog().size(); i++) {
            int row = i + 8;
            AttendanceEventInfo info = attendance.getLog().get(i);
            Member member = guild.getMemberById(info.getUserId());
            if (member == null) {
                guild.loadMembers().get();
            }
            member = Objects.requireNonNull(guild.getMemberById(info.getUserId()));
            table.getCellByPosition(0, row).setStringValue(ZonedDateTime.ofInstant(info.getDateTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            table.getCellByPosition(1, row).setStringValue(ZonedDateTime.ofInstant(info.getDateTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            table.getCellByPosition(2, row).setStringValue(member.getId());
            table.getCellByPosition(3, row).setStringValue(member.getEffectiveName());
            table.getCellByPosition(4, row).setStringValue(member.getNickname());
            table.getCellByPosition(5, row).setStringValue(info.getEvent() == AttendanceEvent.IN ? "Joined" : "Left");
        }
        Path path = Path.of(System.getProperty("java.io.tmpdir"), String.format("%s_%s.ods", channel.getName(), (ZonedDateTime.ofInstant(attendance.getEndTime(), ZoneId.of("Asia/Bangkok")).format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")))));
        document.save(path.toFile());
        document.close();
        return path;
    }

    public boolean AddEvent(String guildId, String channelId, String userId, AttendanceEventInfo event) {
        ChannelMetadata metadata = store.readAttendanceMetadata(guildId, channelId);
        if (metadata == null || metadata.getCurrentSession() == null) {
            return false;
        }
        UUID id = metadata.getCurrentSession();
        Attendance attendance = store.readAttendance(guildId, channelId, id.toString());
        attendance.getLog().add(event);
        store.addEvent(attendance);
        return true;
    }
}
