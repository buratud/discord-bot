package com.buratud;

import com.buratud.interactions.MessageInteraction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class Main extends ListenerAdapter {
    private final MessageInteraction message;

    private Main() {
        message = new MessageInteraction();
    }

    public static void main(String[] args) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(Env.DISCORD_TOKEN)
                .addEventListeners(new Main())
                .build();
        jda.awaitReady();
//        jda.updateCommands().addCommands(
//                Commands.message("OCR")
//        ).queue();
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        switch (event.getName()) {
            case "OCR" -> {
                try {
                    message.ocr(event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        SelfUser user = event.getJDA().getSelfUser();
        System.out.printf("Logged in as \"%s\"", user.getName());
    }
}

