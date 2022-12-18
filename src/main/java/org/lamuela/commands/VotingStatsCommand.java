package org.lamuela.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Postproc;
import org.lamuela.api.models.Voting;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VotingStatsCommand extends ListenerAdapter {

    private final Pattern statsButtonPattern = Pattern.compile("^stats_voting_(\\d+)$");
    private final Pattern graphButtonPattern = Pattern.compile("^show_graph_(\\w+)_(\\d+)");

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Matcher statsButton = statsButtonPattern.matcher(event.getButton().getId());
        if(statsButton.matches()) {
            String votingId = statsButton.group(1);
            Voting voting = DecideAPI.getVotingById(Integer.parseInt(votingId));
            if(Objects.isNull(voting.getPostproc())) {
                event.reply("No se pueden ver los resultados de esta votación").setEphemeral(true).queue();
                return;
            }
            event.reply("Seleccione el tipo de gráfico a mostrar:").addActionRow(
                    Button.success("show_graph_bar_" + votingId, "Barras"),
                    Button.success("show_graph_pie_" + votingId, "Tarta"),
                    Button.success("show_graph_horizontalbar_" + votingId, "Barras horizontales"),
                    Button.success("show_graph_doughnut_" + votingId, "Donut"),
                    Button.success("show_graph_polar_" + votingId, "Polar")
            ).setEphemeral(true).queue();
        }
        Matcher graphButton = graphButtonPattern.matcher(event.getButton().getId());
        if(graphButton.matches()) {
            String type = graphButton.group(1);
            int votingId = Integer.parseInt(graphButton.group(2));
            AtomicReference<ChartType> chartType = new AtomicReference<>(ChartType.BAR);
            Arrays.stream(ChartType.values()).filter(t -> t.name().equalsIgnoreCase(type)).findFirst().ifPresent(chartType::set);
            try(InputStream img = getImage(DecideAPI.getVotingById(votingId), chartType.get())) {
                event.reply("Datos").addFiles(FileUpload.fromData(img, "data.jpg")).setEphemeral(true).complete();
            } catch (IOException e) {
                event.reply("No se han podido mostrar los datos").setEphemeral(true).queue();
            }
        }
    }

    private Pair<List<String>, List<Integer>> getVotingData(Voting voting) {
        List<String> labels = new ArrayList<>();
        List<Integer> votes = new ArrayList<>();
        for(Postproc op: voting.getPostproc()) {
            labels.add(op.getOption());
            votes.add(op.getVotes());
        }
        return Pair.of(labels, votes);
    }

    private InputStream getImage(Voting voting, ChartType type) throws IOException {
        String url;
        Pair<List<String>, List<Integer>> data = getVotingData(voting);
        switch (type) {
            case PIE -> url = String.format(
                    "https://quickchart.io/chart?width=500&height=300&bkg=white&c={type:'pie',data:{labels:%s,datasets:[{label:'%s',data:%s}]}}",
                    data.getLeft().stream().map(v -> "'"+v+"'").toList(),
                    voting.getQuestion().getDesc(),
                    data.getRight()
            );
            case HORIZONTALBAR -> url = String.format(
                    "https://quickchart.io/chart?width=500&height=300&bkg=white&c={type:'horizontalBar',data:{labels:%s,datasets:[{label:'%s',data:%s}]}}",
                    data.getLeft().stream().map(v -> "'"+v+"'").toList(),
                    voting.getQuestion().getDesc(),
                    data.getRight()
            );
            case POLAR -> url = String.format(
                    "https://quickchart.io/chart?width=500&height=300&bkg=white&c={type:'polarArea',data:{labels:%s,datasets:[{label:'%s',data:%s}]}}",
                    data.getLeft().stream().map(v -> "'"+v+"'").toList(),
                    voting.getQuestion().getDesc(),
                    data.getRight()
            );
            case DOUGHNUT -> url = String.format(
                    "https://quickchart.io/chart?width=500&height=300&bkg=white&c={type:'doughnut',data:{labels:%s,datasets:[{label:'%s',data:%s}]},options:{plugins:{doughnutlabel:{labels:[{text:'Total',font:{size:20}},{text:'%d',font:{size:20}}]}}}}",
                    data.getLeft().stream().map(v -> "'"+v+"'").toList(),
                    voting.getQuestion().getDesc(),
                    data.getRight(),
                    data.getRight().stream().reduce(0, Integer::sum)
            );
            default -> url = String.format(
                    "https://quickchart.io/chart?width=500&height=300&bkg=white&c={type:'bar',data:{labels:%s,datasets:[{label:'%s',data:%s}]}}",
                    data.getLeft().stream().map(v -> "'"+v+"'").toList(),
                    voting.getQuestion().getDesc(),
                    data.getRight()
            );
        }
        return new URL(url.replace(" ", "%20")).openStream();
    }

    private enum ChartType {
        BAR, PIE, HORIZONTALBAR, DOUGHNUT, POLAR;
    }

}
