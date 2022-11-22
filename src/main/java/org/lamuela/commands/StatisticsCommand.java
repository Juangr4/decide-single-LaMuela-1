package org.lamuela.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

import org.lamuela.statistics.ChartType;
import org.lamuela.statistics.StatisticsManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

public class StatisticsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        System.out.println(event.getName());
        if (!event.getName().equals("showgraph"))
            return;
        try {
            String urlString = StatisticsManager.getGraphOfVotingId(1, Arrays.asList(ChartType.values()).stream().filter(c -> c.toString().toLowerCase().equals(event.getOption("type").getAsString().toLowerCase())).findFirst().get());
            System.out.println(urlString);
            File file = new File("src/main/java/org/lamuela/statistics/temp.jpg");
            URL url = new URL(urlString.replace(" ", "%20"));
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(file);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }

            is.close();
            os.close();
            event.reply("¡Aquí está tu gráfico!").addFiles(FileUpload.fromData(file)).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("Error mostrando imagen.").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

}
