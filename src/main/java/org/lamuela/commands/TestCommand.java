package org.lamuela.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getContentRaw());
    }
}
