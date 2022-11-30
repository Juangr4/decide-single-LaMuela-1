package org.lamuela.commands;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.LoginResponse;
import org.lamuela.sqlite3.SQLMethods;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LoginCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String username = "";
        String password = "";
        if(event.getOption("login") != null){
            username = event.getOption("login").getAsString();
        }
        if(event.getOption("password") != null){
            password = event.getOption("password").getAsString();
        }
        LoginResponse login = DecideAPI.login(username, password);
        String token = login.getToken();
        String discUser = event.getMember().getEffectiveName();
        SQLMethods.insertUser(discUser, token, username, password);
        String ephimeralmsg = "Se ha iniciado sesión con éxito";
        event.reply(ephimeralmsg).setEphemeral(true); 
    }
}
