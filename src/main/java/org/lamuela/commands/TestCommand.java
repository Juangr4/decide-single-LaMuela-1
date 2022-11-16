package org.lamuela.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class TestCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println(event.getName());
        if(!event.getName().equals("test")) return;
        String content = "Hello World";
        if(event.getOption("content") != null) {
            content += ": " + event.getOption("content").getAsString();
        }
        event.reply(content).setEphemeral(true).queue();
    }

}


