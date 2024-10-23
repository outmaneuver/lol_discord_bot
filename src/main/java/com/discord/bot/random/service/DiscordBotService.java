package com.discord.bot.random.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Service;

@Service
public class DiscordBotService extends ListenerAdapter {

  private static final String ROLL_MESSAGE = "top: {0}\njg: {1}\nmid: {2}\nadc: {3}\nsup: {4}";

  /**
   * ボットにコマンドを追加する。
   *
   * @param jda 実行対象のbot
   */
  public void setCommand(JDA jda) {

    SlashCommandData testCommand = Commands.slash("シャッフル", "ロールをランダムで決めるやつ")
        .addOption(OptionType.STRING, "member1", "参加メンバー1")
        .addOption(OptionType.STRING, "member2", "参加メンバー2")
        .addOption(OptionType.STRING, "member3", "参加メンバー3")
        .addOption(OptionType.STRING, "member4", "参加メンバー4")
        .addOption(OptionType.STRING, "member5", "参加メンバー5");
    jda.updateCommands().addCommands(testCommand).queue();

  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    String command = event.getName();
    if (command.equals("シャッフル")) {
      List<String> members = event.getOptions().stream().map(OptionMapping::getAsString).toList();

      var memberRollList = new ArrayList<>(members);
      for (int i = members.size(); i < 5; i++) {
        memberRollList.add("野良");
      }

      var suffledList = memberRollList.stream().collect(toShuffledList());
      String result = MessageFormat.format(ROLL_MESSAGE, suffledList.get(0), suffledList.get(1), suffledList.get(2),
          suffledList.get(3), suffledList.get(4));
      event.reply(result).setEphemeral(false).queue();
    }
  }

  private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
      Collectors.toCollection(ArrayList::new),
      list -> {
        Collections.shuffle(list);
        return list;
      }
  );

  @SuppressWarnings("unchecked")
  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return (Collector<T, ?, List<T>>) SHUFFLER;
  }
}
