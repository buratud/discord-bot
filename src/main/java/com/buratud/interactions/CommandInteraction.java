package com.buratud.interactions;

import java.io.IOException;

import com.buratud.Service;
import com.buratud.services.ChatGPT;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandInteraction {
    private final ChatGPT chatgpt;

    public CommandInteraction() throws IOException {
        Service service = Service.getInstance();
        chatgpt = service.chatgpt;
    }

    public void resetChatHistory(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        chatgpt.reset(channelId, userId);
        event.reply("Chat history reset.").queue();
    }
}
