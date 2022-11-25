package org.lamuela.commands;

import org.lamuela.User.User;
import org.lamuela.User.UserService;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.LoginResponse;

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
        User user = new User();
        user.setDiscUser(discUser);
        user.setPassword(password);
        user.setToken(token);
        user.setUsername(username);
        UserService.save(user);
    }
}
