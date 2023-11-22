package com.buratud.interactions;

import com.buratud.Service;
import com.buratud.services.ComputerVision;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import java.io.IOException;

public class Ocr implements Handler {
    private final ComputerVision vision;
    private static Ocr instance;

    public Ocr() throws IOException {
        Service service = Service.getInstance();
        vision = service.vision;
    }

    public static synchronized Ocr getInstance() throws IOException {
        // Create the instance if it doesn't exist yet
        if (instance == null) {
            instance = new Ocr();
        }
        return instance;
    }

    public void ocr(MessageContextInteractionEvent event) throws IOException, InterruptedException {
        String url = event.getTarget().getAttachments().get(0).getUrl();
        event.deferReply().queue();
        String[] result = vision.extractText(url);
        event.getHook().sendMessage("```\n" + String.join("\n", result) + "\n```").queue();
    }
}
