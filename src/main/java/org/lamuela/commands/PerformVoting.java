package org.lamuela.commands;

import java.util.List;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;
import org.lamuela.sqlite3.SQLMethods;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PerformVoting extends ListenerAdapter{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){

        String[] optionName = event.getButton().getId().split("_");

        if(optionName[0].equals("Voting") && optionName[2].equals("option")){

            Integer votingId = Integer.parseInt(optionName[1]);
            Voting voting = DecideAPI.getVotingById(votingId);
        
            Integer optionId = Integer.parseInt(optionName[3]);
            List<Option> options = voting.getQuestion().getOptions();

            Option option = null;

            for(Option op : options){

                if (op.getNumber() == optionId){

                    option = op;

                }

            }

            String discUser = event.getMember().getUser().getName();
            String token = SQLMethods.getTokenByDiscUser(discUser);

            DecideAPI.performVote(voting, option, token);

            String ephimeralmsg = "Se ha realizado la votación con éxito";
            event.reply(ephimeralmsg).setEphemeral(true).queue();

        }

    }

}

