package org.lamuela;

import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.lamuela.commands.CommandManager;
import org.lamuela.commands.TestCommand;


public class Decide {

    public static Dotenv env;

    public static JDA jda;

    public static void main(String[] args) {
        setupEnviroment();
        setupDiscordBot();
        registerCommands();
    }

    private static void registerCommands() {
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new TestCommand());
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
