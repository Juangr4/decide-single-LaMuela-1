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
        EmbedBuilder vote = new EmbedBuilder().setDescription("Escoja una de las siguientes opciones");

        List<ActionRow> ar = new ArrayList<>();
        int k = answers.size() / 5;
        int r = (answers.size()%5);

        if(r != 0){ //Si el número de opciones no es múltiplo de 5 tengo q tener en cuenta la franja de menos de 5 botones

            for (int i = 0; i < k + 1; i++){ //Bucle for para recorrer cada bloque de 5 opciones

                if(i == k){ //Compruebo si es el bloque de menos de 5 opciones

                    List<Button> list = new ArrayList<>();

                    for (int j = i*5; j < r + (5 * i); j++){ //Recorro las opciones, al ser menos de 5, uso r en la comparación para q no se pase del número de opciones

                        String optionId = "Voting_" + voting.getId() +"_option_" + answers.get(j).getNumber().toString();
                        Button button = Button.secondary(optionId, answers.get(j).getOption());
                        list.add(button);

                    }

                    ActionRow buttonAr = ActionRow.of(list);
                    ar.add(buttonAr);

                }else{

                    List<Button> list = new ArrayList<>();

                    for (int j = i*5; j < 5 + (5 * i); j++){

                        String optionId = "Voting_" + voting.getId() +"_option_" + answers.get(j).getNumber().toString();
                        Button button = Button.secondary(optionId, answers.get(j).getOption());
                        list.add(button);

                    }

                    ActionRow buttonAr = ActionRow.of(list);
                    ar.add(buttonAr);

                }

            }

        }else{

            for (int i = 0; i < k; i++){

                List<Button> list = new ArrayList<>();

                for (int j = i*5; j < 5 + i; j++){

                    String optionId = "Voting_" + voting.getId() +"_option_" + answers.get(j).getNumber().toString();
                    Button button = Button.secondary(optionId, answers.get(j).getOption());
                    list.add(button);

                }

                ActionRow buttonAr = ActionRow.of(list);
                ar.add(buttonAr);

            }

        }

        String ephimeralmsg = "Se ha iniciado la votación";
        event.reply(ephimeralmsg).setEphemeral(true).queue();

        DecideAPI.updateVoting(voting, "start");
        event.getGuild().getTextChannelById(event.getChannel().getId()).sendMessageEmbeds(vote.build()).setContent(question).setComponents(ar).queue();

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

