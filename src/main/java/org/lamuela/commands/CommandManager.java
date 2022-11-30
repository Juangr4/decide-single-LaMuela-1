package org.lamuela.commands;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(
                Commands.slash("test", "this is a test command")
                        .addOption(OptionType.STRING, "content", "Show the content on a message"),
                Commands.slash("login","command to log in decide platform")
                        .addOption(OptionType.STRING, "username", "Username to log in decide platform")
                        .addOption(OptionType.STRING, "password", "Password to log in decide platform"),
                Commands.slash("createvoting", "Command to create a voting")
        ).queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getGuild().updateCommands().addCommands(
                Commands.slash("test", "this is a test command")
                        .addOption(OptionType.STRING, "content", "Show the content on a message"),
                Commands.slash("login","command to log in decide platform")
                        .addOption(OptionType.STRING, "username", "Username to log in decide platform")
                        .addOption(OptionType.STRING, "password", "Password to log in decide platform"),
                Commands.slash("createvoting", "Command to create a voting")
        ).queue();
    }
}
