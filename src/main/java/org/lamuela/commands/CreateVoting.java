package org.lamuela.commands;

import java.util.List;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.lamuela.Storage;
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
        if (!event.getName().equals("createvoting")) return;
        if (event.getMember().getRoles().stream().noneMatch(r -> r.getId().equals(Storage.getAdminRoleId())) ||
            !event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("No puedes usar este comando.").setEphemeral(true).queue();
            return;
        }

        TextInput name = TextInput.create("name", "Nombre", TextInputStyle.SHORT)
                .setPlaceholder("Nombre que identifique la votación.")
                .setMinLength(1)
                .setRequired(true)
                .build();

        TextInput description = TextInput.create("description", "Descripción", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descripción de la votación que desea crear.")
                .setMinLength(1)
                .setRequired(true)
                .build();

        TextInput question = TextInput.create("question", "Pregunta", TextInputStyle.SHORT)
                .setPlaceholder("Pregunta de la votación")
                .setMinLength(1)
                .setRequired(true)
                .build();

        TextInput option = TextInput.create("options", "Respuestas", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Respuestas a la pregunta (una respuesta por linea)")
                .setMinLength(1)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("create-voting", "Voting")
                .addActionRows(ActionRow.of(name),ActionRow.of(description),ActionRow.of(question), ActionRow.of(option))
                .build();

        event.replyModal(modal).queue();
    }
    
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event){
        if(event.getModalId().equals("create-voting")){
            String name = event.getValue("name").getAsString();
            String description = event.getValue("description").getAsString();
            String question = event.getValue("question").getAsString();
            String options = event.getValue("options").getAsString();

            DecideAPI.createVoting(name, description, question, List.of(options.split("\n")));
            event.reply("Votación creada correctamente").setEphemeral(true).queue();
            CreateChannel.updateVoting(event.getGuild());
        }
        
    }

}
