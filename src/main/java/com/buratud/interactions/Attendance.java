package com.buratud.interactions;

import com.buratud.Service;
import com.buratud.data.attendance.AttendanceEvent;
import com.buratud.data.attendance.AttendanceEventInfo;
import com.buratud.services.AttendanceService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Attendance implements Handler {
    private static final Logger logger = LogManager.getLogger(Attendance.class);
    private static Attendance instance;
    private final AttendanceService service;

    private Attendance() throws IOException {
        Service service = Service.getInstance();
        this.service = service.attendance;
    }

    public static synchronized Attendance getInstance() throws IOException {
        // Create the instance if it doesn't exist yet
        if (instance == null) {
            instance = new Attendance();
        }
        return instance;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            String subName = event.getSubcommandName();
            switch (Objects.requireNonNull(subName)) {
                case "start" -> start(event);
                case "stop" -> stop(event);
                case "now" -> now(event);
                default -> event.reply("Subcommand doesn't exist.").complete();
            }
        } catch (Exception e) {
            if (event.isAcknowledged()) {
                event.getHook().sendMessage("Something went wrong, try again later.").complete();
            } else {
                event.reply("Something went wrong, try again later.").setEphemeral(true).complete();
            }
            logger.error(e);
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        try {
            AttendanceEvent attendanceEvent = event.getChannelJoined() != null ? AttendanceEvent.IN : AttendanceEvent.OUT;
            AttendanceEventInfo info = new AttendanceEventInfo(event.getMember().getId(), attendanceEvent);
            String channelId;
            if (event.getChannelJoined() != null) {
                channelId = event.getChannelJoined().getId();
            } else if (event.getChannelLeft() != null) {
                channelId = event.getChannelLeft().getId();
            } else {
                return;
            }
            service.AddEvent(event.getGuild().getId(), channelId, event.getMember().getId(), info);
        } catch (Exception e) {
            logger.error(e);
            for (StackTraceElement s : e.getStackTrace()) {
                logger.error(s);
            }
        }
    }

    private void start(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Channel channel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
        OptionMapping option = event.getOption("channel");
        if (option != null) {
            if (option.getChannelType() != ChannelType.VOICE) {
                event.reply(String.format("%s is not a voice channel", option.getAsMentionable().getAsMention())).complete();
                return;
            }
            channel = option.getAsChannel();
        }
        if (channel == null) {
            event.reply("You haven't joined a voice channel nor provided option.").complete();
            return;
        }
        VoiceChannel voiceChannel = (VoiceChannel) channel;
        String session = service.StartAttendance(Objects.requireNonNull(guild).getId(), channel.getId(), event.getMember().getId(), voiceChannel.getMembers());
        if (session != null) {
            event.reply("Attendance session started.").complete();
        } else {
            event.reply("Attendance session already in progress.").complete();
        }
    }

    private void stop(SlashCommandInteractionEvent event) throws Exception {
        Guild guild = event.getGuild();
        Channel channel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
        OptionMapping option = event.getOption("channel");
        if (option != null) {
            if (option.getChannelType() != ChannelType.VOICE) {
                event.reply(String.format("%s is not a voice channel", option.getAsMentionable().getAsMention())).complete();
                return;
            }
            channel = option.getAsChannel();
        }
        if (channel == null) {
            event.reply("You haven't joined a voice channel nor provided option.").complete();
            return;
        }
        com.buratud.data.attendance.Attendance attendance = service.GetCurrentAttendance(Objects.requireNonNull(guild).getId(), channel.getId());
        if (attendance == null) {
            event.reply("No current attendance session.").complete();
            return;
        }
        if (!Objects.equals(attendance.getInitiatorId(), event.getMember().getId())) {
            event.reply("You did not start this session. This incident will be reported.").complete();
            return;
        }
        String session = service.EndAttendance(attendance);
        if (session != null) {
            event.reply("Attendance session stopped.").complete();
            Path path = service.GenerateAttendanceHistory(event.getJDA(), attendance);
            if (path != null) {
                event.getHook().sendFiles(FileUpload.fromData(path.toFile())).complete();
            } else {
                event.getHook().sendMessage("Couldn't generate file.").complete();
            }
        } else {
            event.reply("No current attendance session.").complete();
        }
    }

    private void now(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Channel channel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
        OptionMapping option = event.getOption("channel");
        if (option != null) {
            if (option.getChannelType() != ChannelType.VOICE) {
                event.reply(String.format("%s is not a voice channel", option.getAsMentionable().getAsMention())).complete();
                return;
            }
            channel = option.getAsChannel();
        }
        if (channel == null) {
            event.reply("You haven't joined a voice channel nor provided option.").complete();
            return;
        }
        event.deferReply().complete();
        VoiceChannel voiceChannel = (VoiceChannel) channel;
        voiceChannel.getMembers();
        Path path = service.GenerateCurrentAttendance(voiceChannel);
        if (path != null) {
            event.getHook().sendFiles(FileUpload.fromData(path.toFile())).complete();
        } else {
            event.reply("Couldn't generate file.").complete();
        }
    }
}
