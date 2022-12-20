package org.lamuela;

import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.lamuela.commands.*;
import org.lamuela.sqlite3.SQLMethods;

import java.util.Objects;


public class Decide {

    private static Dotenv env;

    private static JDA jda;

    public static void main(String[] args) {
        setupEnviroment();
        setupDiscordBot();
        registerCommands();
        SQLMethods.initDB();
    }

    private static void registerCommands() {
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new CreateChannel());
        jda.addEventListener(new CreateVoting());
        jda.addEventListener(new LoginCommand());
        jda.addEventListener(new ManageVoting());
        jda.addEventListener(new PerformVoting());
        jda.addEventListener(new VotingStatsCommand());
    }

    private static void setupEnviroment() {
        env = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
        Unirest.config().defaultBaseUrl(getEnvVariable("DECIDE_HOST"));
    }

    private static void setupDiscordBot() {
        JDABuilder jdaBuilder = JDABuilder.createDefault(getEnvVariable("TOKEN"));

        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        jdaBuilder.setActivity(Activity.watching("Hola clase: Programing myself"));

        jda = jdaBuilder.build();
    }

    public static String getEnvVariable(String key) {
        String value = System.getenv(key);
        if(Objects.isNull(value))
            value = env.get(key);
        return value;
    }

}
