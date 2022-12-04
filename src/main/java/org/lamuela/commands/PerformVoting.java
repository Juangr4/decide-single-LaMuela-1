package org.lamuela.commands;

import org.lamuela.api.DecideAPI;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.EventListener;

public class PerformVoting extends ListenerAdapter{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){

        String username = event.getMember().getEffectiveName();

        DecideAPI.performVote(null, null, null);

    }

}
