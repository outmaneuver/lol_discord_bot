package com.discord.bot.random

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class RandomRollApplication

fun main(args: Array<String>) {
	SpringApplicationBuilder(RandomRollApplication::class.java).web(WebApplicationType.NONE).run(*args)
}
