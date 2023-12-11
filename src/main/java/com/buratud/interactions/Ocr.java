package com.buratud.interactions;

import com.buratud.Service;
import com.buratud.services.ComputerVision;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Ocr implements Handler {
    private static final Logger logger = LogManager.getLogger(Ocr.class);
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

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        try {
            ocr(event);
        } catch (IOException | InterruptedException e) {
            event.reply("Something went wrong, try again later.").complete();
            logger.error(e);
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        }

    }

    public void ocr(MessageContextInteractionEvent event) throws IOException, InterruptedException {
        String url = event.getTarget().getAttachments().get(0).getUrl();
        event.deferReply().complete();
        String[] result = vision.extractText(url);
        event.getHook().sendMessage("```\n" + String.join("\n", result) + "\n```").complete();
    }
}
