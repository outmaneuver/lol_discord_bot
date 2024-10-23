package com.discord.bot.random.component

import com.discord.bot.random.service.DiscordBotService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DiscordBotRunner(
    private val discordBotService: DiscordBotService
) : ApplicationRunner {
    companion object {
        private const val discordToken = ""
    }

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        val jda: JDA = JDABuilder.createDefault(discordToken)
            .setRawEventsEnabled(true)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(discordBotService)
            .setActivity(Activity.playing("作成中"))
            .build()
        // ログインが完了するまで待つ
        jda.awaitReady()
        discordBotService.setCommand(jda)
    }
}