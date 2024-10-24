package com.discord.bot.random.service

import java.text.MessageFormat
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
class DiscordBotService @Autowired constructor(
    private val riotApiService: RiotApiService,
    private val userService: UserService
) : ListenerAdapter() {

    companion object {
        private const val ROLL_MESSAGE = "top: {0}\njg: {1}\nmid: {2}\nadc: {3}\nsup: {4}"
    }

    /**
     * ボットにコマンドを追加する。
     *
     * @param jda 実行対象のbot
     */
    fun setCommand(jda: JDA) {
        val testCommand = Commands.slash(CommandEnum.SUFFLE.command, CommandEnum.SUFFLE.description)
            .addOption(OptionType.STRING, "member1", "参加メンバー1")
            .addOption(OptionType.STRING, "member2", "参加メンバー2")
            .addOption(OptionType.STRING, "member3", "参加メンバー3")
            .addOption(OptionType.STRING, "member4", "参加メンバー4")
            .addOption(OptionType.STRING, "member5", "参加メンバー5")
        jda.updateCommands().addCommands(testCommand).queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val command = event.name
        if (command == CommandEnum.SUFFLE.command) {
            // List<String> members = event.getOptions().stream().map(OptionMapping::getAsString).toList();

            val members = event.options.map { it.getAsString() }.toList()

            val memberRollList = members.toMutableList()
            while (memberRollList.size < 5) {
                memberRollList.add("野良")
            }

            val suffledList = memberRollList.shuffled()
            val result = MessageFormat.format(ROLL_MESSAGE, suffledList[0], suffledList[1], suffledList[2],
                suffledList[3], suffledList[4])
            event.reply(result).setEphemeral(false).queue()
        }
    }

    fun linkDiscordAccountWithRiotId(discordId: String, discordServer: String, gameName: String, tagLine: String) {
        val puuid = riotApiService.getPuuidByRiotId(gameName, tagLine)
        val accountId = puuid?.let { riotApiService.getAccountIdByPuuid(it) }
        if (puuid != null && accountId != null) {
            userService.saveUser(discordId, discordServer, puuid, accountId)
        }
    }
}
