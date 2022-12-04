package org.lamuela.commands;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.lamuela.api.DecideAPI;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CreateVoting extends ListenerAdapter{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("createvoting")) {
            TextInput name = TextInput.create("name", "Name", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput description = TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();
            
            TextInput question = TextInput.create("question", "Question", TextInputStyle.SHORT)
                    .setPlaceholder("Question of the voting")
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput option = TextInput.create("options", "Options", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Options")
                    .setMinLength(1)
                    .setRequired(true)
                    .build();            

            Modal modal = Modal.create("voting", "Voting")
                    .addActionRows(ActionRow.of(name),ActionRow.of(description),ActionRow.of(question), ActionRow.of(option))
                    .build();

            event.replyModal(modal).queue();
        }
    }
    
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event){
        if(event.getModalId().equals("voting")){
            String name = event.getValue("name").getAsString();
            String description = event.getValue("description").getAsString();
            String question = event.getValue("question").getAsString();
            String options = event.getValue("options").getAsString();


            event.reply("Nombre: "+name+" Descripción: "+description+" Pregunta: "+ question+" Opciones: "+options).queue();
            DecideAPI.createVoting(name, description, question, List.of(options.split(",")));         
            
        }
        
    }

}
