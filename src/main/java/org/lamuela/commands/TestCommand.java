package org.lamuela.commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TestCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equals("test")) return;
        String content = "Hello World";
        if(event.getOption("content") != null) {
            content += ": " + event.getOption("content").getAsString();
        }
        event.reply(content).setEphemeral(true).queue();
    }

}


