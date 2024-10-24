package com.discord.bot.random.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RiotApiService(
    @Value("\${riot.api.key}") private val apiKey: String,
    @Value("\${riot.api.url.account}") private val accountApiUrl: String,
    @Value("\${riot.api.url.summoner}") private val summonerApiUrl: String
) {

    private val restTemplate = RestTemplate()

    fun getPuuidByRiotId(gameName: String, tagLine: String): String? {
        val url = "$accountApiUrl/by-riot-id/$gameName/$tagLine?api_key=$apiKey"
        val response = restTemplate.getForObject(url, Map::class.java)
        return response?.get("puuid") as String?
    }

    fun getAccountIdByPuuid(puuid: String): String? {
        val url = "$summonerApiUrl/by-puuid/$puuid?api_key=$apiKey"
        val response = restTemplate.getForObject(url, Map::class.java)
        return response?.get("accountId") as String?
    }
}
