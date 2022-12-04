package org.lamuela.commands;

import java.util.ArrayList;
import java.util.List;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageHistory.MessageRetrieveAction;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ManageVoting extends ListenerAdapter{

    public static String al = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
    public static String[] alphabet = al.split(",");

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){

        String[] getButtonId = event.getButton().getId().split("_");

        if(getButtonId[0].equals("start") && getButtonId[1].equals("voting")){

            startVoting(Integer.parseInt(getButtonId[2]), event);

        }

        if(getButtonId[0].equals("end") && getButtonId[1].equals("voting")){

            event.getMessage().delete().queue();
            String messageId = event.getMessageId();
            endVoting(messageId, event);

        }

    }

    public void startVoting(Integer votingId, ButtonInteractionEvent event){

        Voting voting = DecideAPI.getVotingById(votingId);
        String question = voting.getQuestion().getDesc();
        List<Option> answers= voting.getQuestion().getOptions();
        EmbedBuilder vote = new EmbedBuilder().setTitle(voting.getName()).setDescription(question);

        List<ActionRow> ar = new ArrayList<>();
        int k = answers.size() / 5;

        for (int i = 0; i < k + 1; i++){

            List<Button> list = new ArrayList<>();

            for (int j = i*5; j < 5 + (i * 5); j++){

                String optionId = "option " + (j + 1);
                Button button = Button.secondary(optionId, answers.get(j).getOption()).withEmoji(Emoji.fromFormatted(":regional_indicator_" + alphabet[j].toLowerCase() + ":"));
                list.add(button);

            }

            ActionRow buttonAr = ActionRow.of(list);
            ar.add(buttonAr);

        }

        event.getGuild().getTextChannelById(event.getChannel().getId()).sendMessageEmbeds(vote.build()).setComponents(ar).queue();

    }

    public void endVoting(String messageId, ButtonInteractionEvent event){
        TextChannel channel =  event.getGuild().getTextChannelById(event.getChannel().getId());
        MessageRetrieveAction messages = channel.getHistoryAfter(Long.parseLong(messageId), 100);
        
        
    }

}
