package com.buratud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.buratud.interactions.Attendance;
import com.buratud.interactions.ChatGpt;

import com.buratud.interactions.Ocr;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Main extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private final ExecutorService executor;
    private final ChatGpt chatGPT;
    private final Ocr ocr;
    private final Attendance attendance;

    private Main() throws IOException {
        executor = Executors.newCachedThreadPool();
        chatGPT = ChatGpt.getInstance();
        ocr = Ocr.getInstance();
        attendance = Attendance.getInstance();
    }

    private void updateCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();
        chatGPT.AddCommand(commands);
        ocr.AddCommand(commands);
        attendance.AddCommand(commands);
        jda.updateCommands().addCommands(commands).complete();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Main main = new Main();
        JDA jda = JDABuilder.createDefault(Env.DISCORD_TOKEN).enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS).setMemberCachePolicy(MemberCachePolicy.ALL).addEventListeners(main).build();
        jda.awaitReady();
        main.updateCommands(jda);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        executor.submit(() -> {
            switch (event.getName()) {
                case "OCR" -> ocr.onMessageContextInteraction(event);
            }
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        executor.submit(() -> {
            switch (event.getName()) {
                case "chatgpt" -> chatGPT.onSlashCommandInteraction(event);
                case "attendance" -> attendance.onSlashCommandInteraction(event);
            }
        });
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        executor.submit(() -> {
            attendance.onGuildVoiceUpdate(event);
        });
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        executor.submit(() -> {
            String id = event.getModalId();
            String module = id.substring(0, id.indexOf('_'));
            switch (module) {
                case "chatgpt" -> chatGPT.onModalInteraction(event);
            }
        });
    }

    @Override
    public void onReady(ReadyEvent event) {
        SelfUser user = event.getJDA().getSelfUser();
        logger.info("Logged in as {}", user.getName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        executor.submit(() -> chatGPT.onMessageReceived(event));
    }
}