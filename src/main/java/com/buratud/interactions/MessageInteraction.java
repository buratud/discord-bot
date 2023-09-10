package com.buratud.interactions;

import java.io.IOException;

import com.buratud.Service;
import com.buratud.services.ComputerVision;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class MessageInteraction {
    private final ComputerVision vision;

    public MessageInteraction() throws IOException {
        Service service = Service.getInstance();
        vision = service.vision;
    }

    public void ocr(MessageContextInteractionEvent event) throws IOException, InterruptedException {
        String url = event.getTarget().getAttachments().get(0).getUrl();
        event.deferReply().queue();
        String[] result = vision.extractText(url);
        event.getHook().sendMessage("```\n" + String.join("\n", result) + "\n```").queue();
    }
}
