package org.lamuela;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
    }

    private static void setupDiscordBot() {
        JDABuilder jdaBuilder = JDABuilder.createDefault(env.get("TOKEN"));

        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        jdaBuilder.setActivity(Activity.watching("Programing myself"));

        jda = jdaBuilder.build();
    }

}
