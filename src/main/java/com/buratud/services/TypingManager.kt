package com.buratud.services

import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import java.util.concurrent.atomic.AtomicInteger

class TypingManager private constructor() {
    companion object {
        val instance: TypingManager by lazy { TypingManager() }
    }
    private val map = mutableMapOf<GuildMessageChannel, TypingMetadata>();
    fun increase(channel: GuildMessageChannel) {
        if (map[channel] == null) {
            map[channel] = TypingMetadata()
        }
        val count = map[channel]!!.count.incrementAndGet();
        if (count == 1) {
            map[channel]!!.job?.cancel()
            map[channel]!!.job = CoroutineScope(Dispatchers.Default).launch {
               while (isActive) {
                   channel.sendTyping().complete()
                   delay(9000)
               }
            }
        }
    }

    fun decrease(channel: GuildMessageChannel) {
        if (map[channel] == null) {
            return
        }
        var count = map[channel]!!.count.get()
        if (count < 1) {
            return
        }
        count = map[channel]!!.count.decrementAndGet()
        if (count == 0) {
            map[channel]!!.job?.cancel()
        }
    }

    class TypingMetadata {
        val count = AtomicInteger(0);
        var job: Job? = null
    }
}