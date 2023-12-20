package com.buratud.interactions;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.hc.core5.http.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public interface Handler {
    default void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event)  throws NotImplementedException {
        throw new NotImplementedException();
    }

    default void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) throws NotImplementedException {
        throw new NotImplementedException();
    }

    default void onMessageReceived(@NotNull MessageReceivedEvent event) throws NotImplementedException {
        throw new NotImplementedException();
    }

    default void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) throws NotImplementedException {
        throw new NotImplementedException();
    }

    default void onModalInteraction(ModalInteractionEvent event) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
