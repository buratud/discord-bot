package com.buratud.interactions;

import com.buratud.Env;
import com.buratud.services.ComputerVision;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MessageInteractions {
    private final ComputerVision vision;

    public MessageInteractions() {
        vision = new ComputerVision(Env.AZURE_VISION_ENDPOINT, Env.AZURE_VISION_KEY);
    }

    public void ocr(MessageContextInteractionEvent event) throws IOException, InterruptedException {
        String url = event.getTarget().getAttachments().get(0).getUrl();
        event.deferReply().queue();
        String[] result = vision.extractText(url);
        event.getHook().sendMessage(String.join("\n", result)).queue();
    }
}
