package org.lamuela.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.LoginResponse;
import org.lamuela.sqlite3.SQLMethods;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LoginCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equals("login")) return;
        OptionMapping username = event.getOption("username");
        OptionMapping password = event.getOption("password");
        if(username == null || password == null){
            event.reply("Para poder iniciar sesión es necesario que indiques tu usuario y tu contraseña al utilizar el comando.").setEphemeral(true).queue();
            return;
        }
        LoginResponse login = DecideAPI.login(username.getAsString(), password.getAsString());
        if(login.getToken() == null) {
            event.reply("Fallo al iniciar sesión. Usuario o contraseña incorrecto").setEphemeral(true).queue();
            return;
        }
        SQLMethods.insertUser(event.getMember().getEffectiveName(), login.getToken(), username.getAsString(), password.getAsString());
        event.reply("Sesión iniciada con exito.").setEphemeral(true).queue();
    }
}
