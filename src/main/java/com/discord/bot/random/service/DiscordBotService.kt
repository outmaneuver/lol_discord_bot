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
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@Service
class DiscordBotService : ListenerAdapter() {

    companion object {
        private const val ROLL_MESSAGE = "top: {0}\njg: {1}\nmid: {2}\nadc: {3}\nsup: {4}"
    }

    @Value("\${riot.api.key}")
    private lateinit var riotApiKey: String

    @Value("\${riot.api.url}")
    private lateinit var riotApiUrl: String

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

        val rankCommand = Commands.slash("rank", "現在のランクを取得する")
            .addOption(OptionType.STRING, "discord_id", "DiscordユーザーID")
        jda.updateCommands().addCommands(rankCommand).queue()
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
        } else if (command == "rank") {
            val discordId = event.getOption("discord_id")?.asString
            if (discordId != null) {
                val riotAccountId = getRiotAccountId(discordId)
                if (riotAccountId != null) {
                    val rankInfo = getRankInfo(riotAccountId)
                    if (rankInfo != null) {
                        event.reply(rankInfo).setEphemeral(false).queue()
                    } else {
                        event.reply("ランク情報を取得できませんでした。").setEphemeral(true).queue()
                    }
                } else {
                    event.reply("RiotアカウントIDを取得できませんでした。").setEphemeral(true).queue()
                }
            } else {
                event.reply("DiscordユーザーIDが指定されていません。").setEphemeral(true).queue()
            }
        }
    }

    private fun getRiotAccountId(discordId: String): String? {
        // ユーザテーブルからdiscordIdに紐づいたriot_account_idを取得する処理を実装する
        // ここでは仮に固定のriot_account_idを返す
        return "sampleRiotAccountId"
    }

    private fun getRankInfo(riotAccountId: String): String? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Riot-Token", riotApiKey)

        val entity = HttpEntity<String>(headers)
        val url = "$riotApiUrl/lol/league/v4/entries/by-summoner/$riotAccountId"

        val response: ResponseEntity<Array<RiotRankInfo>> = restTemplate.exchange(url, HttpMethod.GET, entity, Array<RiotRankInfo>::class.java)
        val rankInfo = response.body

        return if (rankInfo != null && rankInfo.isNotEmpty()) {
            rankInfo[0].toString()
        } else {
            null
        }
    }

    data class RiotRankInfo(
        val leagueId: String,
        val queueType: String,
        val tier: String,
        val rank: String,
        val summonerId: String,
        val summonerName: String,
        val leaguePoints: Int,
        val wins: Int,
        val losses: Int,
        val veteran: Boolean,
        val inactive: Boolean,
        val freshBlood: Boolean,
        val hotStreak: Boolean
    )
}
