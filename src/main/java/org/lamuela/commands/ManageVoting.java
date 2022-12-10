package org.lamuela.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ManageVoting extends ListenerAdapter{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){

        String[] getButtonId = event.getButton().getId().split("_");

        if(getButtonId[0].equals("start") && getButtonId[1].equals("voting")){

            startVoting(Integer.parseInt(getButtonId[2]), event);

            List<LayoutComponent> list =  event.getMessage().getComponents();
            ActionRow ar = ActionRow.of(list.get(0).getButtons().get(1), list.get(0).getButtons().get(2));
            List<LayoutComponent> list2 = new ArrayList<>();
            list2.add(ar);
            event.getMessage().editMessageComponents(list2).queue();

        }

        if(getButtonId[0].equals("end") && getButtonId[1].equals("voting")){

            endVoting(Integer.parseInt(getButtonId[2]), event);

            List<LayoutComponent> list =  event.getMessage().getComponents();
            ActionRow ar = ActionRow.of(list.get(0).getButtons().get(1));
            List<LayoutComponent> list2 = new ArrayList<>();
            list2.add(ar);
            event.getMessage().editMessageComponents(list2).queue();

        }

    }

    public void startVoting(Integer votingId, ButtonInteractionEvent event){

        Voting voting = DecideAPI.getVotingById(votingId);
        String question = voting.getQuestion().getDesc();
        List<Option> answers= voting.getQuestion().getOptions();
        EmbedBuilder vote = new EmbedBuilder().setTitle(question).setDescription("Escoja una de las siguientes opciones");

        List<ActionRow> actionRows = new ArrayList<>();
        List<Button> actual = new ArrayList<>();

        for(int i=0; i<answers.size(); i++) {
            if(i%5 == 0 && !actual.isEmpty()) {
                actionRows.add(ActionRow.of(actual));
                actual = new ArrayList<>();
            }
            actual.add(Button.secondary(String.format("Voting_%d_option_%d", votingId, answers.get(i).getNumber()), answers.get(i).getOption()));
        }
        DecideAPI.updateVoting(voting, "start");

        event.reply("Se ha iniciado la votación").setEphemeral(true).queue();
        event.getChannel().sendMessageEmbeds(vote.build()).setContent(question).setComponents(actionRows).queue();

    }

    public void endVoting(Integer votingId, ButtonInteractionEvent event){

        Logger logger = Logger.getLogger(ManageVoting.class.getName());

        TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messages = history.getRetrievedHistory();

        Message msgToDel = null;

        for(Message msg : messages){

            if(msg.getContentRaw().equals(DecideAPI.getVotingById(votingId).getQuestion().getDesc())){

                msgToDel = msg;

            }

        }

        try {
            
            msgToDel.delete().queue();

        } catch (Exception e) {

            logger.log(Level.INFO, "Ha ocurrido un error al terminar la votación", e);

        }

        DecideAPI.updateVoting(DecideAPI.getVotingById(votingId), "stop");
        DecideAPI.updateVoting(DecideAPI.getVotingById(votingId), "tally");

        String ephimeralmsg = "Se ha terminado la votación";
        event.reply(ephimeralmsg).setEphemeral(true).queue();

    }

}

