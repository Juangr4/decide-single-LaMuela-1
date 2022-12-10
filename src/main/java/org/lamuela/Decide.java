package org.lamuela;

import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;

import org.lamuela.commands.CommandManager;
import org.lamuela.commands.CreateChannel;
import org.lamuela.commands.CreateVoting;
import org.lamuela.commands.LoginCommand;
import org.lamuela.commands.ManageVoting;
import org.lamuela.commands.PerformVoting;
import org.lamuela.commands.TestCommand;
import org.lamuela.sqlite3.SQLMethods;


public class Decide {

    public static Dotenv env;

    public static JDA jda;

    public static void main(String[] args) {
        setupEnviroment();
        setupDiscordBot();
        registerCommands();
        SQLMethods.initDB();
    }

    private static void registerCommands() {
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new TestCommand());
        jda.addEventListener(new CreateChannel());
        jda.addEventListener(new CreateVoting());
        jda.addEventListener(new LoginCommand());
        jda.addEventListener(new ManageVoting());
        jda.addEventListener(new PerformVoting());
    }

    private static void setupEnviroment() {
        env = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
        Unirest.config().defaultBaseUrl(env.get("decide_host", "http://localhost:8000"));
    }

    private static void setupDiscordBot() {
        JDABuilder jdaBuilder = JDABuilder.createDefault(env.get("TOKEN"));

        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        jdaBuilder.setActivity(Activity.watching("Programing myself"));

        jda = jdaBuilder.build();
    }

}
