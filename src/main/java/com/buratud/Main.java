package com.buratud;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.buratud.interactions.ChatGpt;

import com.buratud.interactions.Ocr;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Main extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private final ExecutorService executor;
    private final ChatGpt chatGPT;
    private final Ocr ocr;

    private Main() throws IOException {
        executor = Executors.newCachedThreadPool();
        chatGPT = ChatGpt.getInstance();
        ocr = Ocr.getInstance();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        JDA jda = JDABuilder.createDefault(Env.DISCORD_TOKEN).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new Main()).build();
        jda.awaitReady();
        jda.updateCommands().addCommands(
                        Commands.message("OCR"),
                        Commands.slash("chatgpt", "ChatGPT related command.")
                                .addSubcommands(new SubcommandData("reset", "Reset chat history."),
                                        new SubcommandData("model", "Switch model.")
                                                .addOptions(new OptionData(OptionType.STRING, "model", "Set model")
                                                        .addChoice("gpt-3.5-turbo-1106", "gpt-3.5-turbo-1106")
                                                        .addChoice("gpt-4-1106-preview", "gpt-4-1106-preview")
                                                )
                                )
                )
                .queue();
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