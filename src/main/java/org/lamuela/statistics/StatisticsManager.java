package org.lamuela.statistics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Question;
import org.lamuela.api.models.Voting;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class StatisticsManager {

    public static void showStatistic(ButtonInteractionEvent event, Voting voting, ChartType type){

        try {
            String urlString = getGraphOfVotingId(voting.getId(), type);
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

    public static String getGraphOfVotingId(int id, ChartType type) throws Exception{
        Voting voting = DecideAPI.getVotingById(id);
        //Voting voting = getExampleVoting();
        switch (type) {
            case BAR:
                return getBarGraph(voting);
            case PIE:
                return getPieGraph(voting);
            case HORIZONTALBAR:
                return getHorizontalBarGraph(voting);
            case DOUGHNUT:
                return getDoughnutGraph(voting);
            case POLAR:
                return getPolarGraph(voting);
            default:
                throw new Exception();
        }
    }

    public static void sendGraphTypeSelector(ButtonInteractionEvent event, String[] splittedId){
        MessageCreateBuilder selectGraphMessageBuilder = new MessageCreateBuilder().addContent("Seleccione el tipo de gráfico a mostrar:");
            List<Button> buttonList = new ArrayList<>();
            buttonList.add(Button.success("show_bar_graph_"+splittedId[2], "Barras"));
            buttonList.add(Button.success("show_pie_graph_"+splittedId[2], "Tarta"));
            buttonList.add(Button.success("show_horizontalbar_graph_"+splittedId[2], "Barras horizontales"));
            buttonList.add(Button.success("show_doughnut_graph_"+splittedId[2], "Donut"));
            buttonList.add(Button.success("show_polar_graph_"+splittedId[2], "Polar"));
            MessageCreateData selectGraphMessage = selectGraphMessageBuilder.addActionRow(buttonList).build();
            event.reply(selectGraphMessage).setEphemeral(true).queue();
    }


    private static String getPolarGraph(Voting voting) {
        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        return String.format("https://quickchart.io/chart?c={type:'polarArea',data:{labels:[%s], datasets:[{data:[%s]}]}}", options, datasets);
    }


    private static String getHorizontalBarGraph(Voting voting) {
        String datasets = "";
        for(Option o : voting.getQuestion().getOptions()){
            String dataset = String.format("{label:'%s',data:[%d]},", o.getOption(), o.getNumber());
            datasets+=dataset;
        }
        datasets = datasets.substring(0, datasets.length()-1);

        return String.format("https://quickchart.io/chart?c={type:'horizontalBar',data:{labels:['%s'], datasets:[%s]}}", voting.getQuestion().getDesc(), datasets);
    }

    private static String getDoughnutGraph(Voting voting) {
        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        Integer total = voting.getQuestion().getOptions().stream().map(o -> o.getNumber()).reduce(0, Integer::sum);
        return String.format("https://quickchart.io/chart?c={type:'doughnut',data:{labels:[%s], datasets:[{data:[%s]}]},options:{plugins:{doughnutlabel:{labels:[{text:'%d',font:{size:20}},{text:'total'}]}}}}", options, datasets, total);
    }


    private static String getPieGraph(Voting voting) {

        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        return String.format("https://quickchart.io/chart?c={type:'pie',data:{labels:[%s], datasets:[{data:[%s]}]}}", options, datasets);
    }


    private static String getBarGraph(Voting voting) {
        String datasets = "";
        for(Option o : voting.getQuestion().getOptions()){
            String dataset = String.format("{label:'%s',data:[%d]},", o.getOption(), o.getNumber());
            datasets+=dataset;
        }
        datasets = datasets.substring(0, datasets.length()-1);

        return String.format("https://quickchart.io/chart?c={type:'%s',data:{labels:['%s'], datasets:[%s]}}", ChartType.BAR.toString().toLowerCase(), voting.getQuestion().getDesc(), datasets);
    }


    public static Voting getExampleVoting(){
        Voting voting = new Voting();
        Question q = new Question();
        q.setDesc("Descripción de prueba");
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        o1.setNumber(1);
        o1.setOption("Option 1");
        o2.setNumber(2);
        o2.setOption("Option 2");
        o3.setNumber(3);
        o3.setOption("Option 3");

        List<Option> options = List.of(o1, o2, o3);
        q.setOptions(options);
        voting.setQuestion(q);
        return voting;
    }
}
