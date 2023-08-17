package com.buratud.interactions;

import com.buratud.Env;
import com.buratud.services.ComputerVision;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class MessageInteraction {
    private final ComputerVision vision;

    public MessageInteraction() {
        vision = new ComputerVision(Env.AZURE_VISION_ENDPOINT, Env.AZURE_VISION_KEY);
    }

    public void ocr(MessageContextInteractionEvent event) throws Exception {
        String url = event.getTarget().getAttachments().get(0).getUrl();
        event.deferReply().queue();
        String[] result = vision.extractText(url);
        event.getHook().sendMessage("```\n" + String.join("\n", result) + "\n```").queue();
    }
}
