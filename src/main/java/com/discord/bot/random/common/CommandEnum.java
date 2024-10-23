package com.discord.bot.random.common;

import lombok.Getter;

@Getter
public enum CommandEnum {
    SUFFLE("suffle", "ロールをランダムで決めるやつ");

    private final String command;
    private final String discription;

    CommandEnum(String command, String discription) {
        this.command = command;
        this.discription = discription;
    }
}
