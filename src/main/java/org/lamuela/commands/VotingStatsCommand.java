package org.lamuela.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.lamuela.api.DecideAPI;
import org.lamuela.statistics.ChartType;
import org.lamuela.statistics.StatisticsManager;

import java.util.Arrays;

public class VotingStatsCommand extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] splittedId = event.getButton().getId().split("_");
        if(splittedId[0].equals("stats") && splittedId[1].equals("voting") ){   //Si es un button graphic
            StatisticsManager.sendGraphTypeSelector(event, splittedId);
        }
        if(splittedId[0].equals("show") && splittedId[2].equals("graph")){
            ChartType chartType = Arrays.asList(ChartType.values()).stream().filter(type -> type.name().equalsIgnoreCase(splittedId[1])).findFirst().get();
            StatisticsManager.showStatistic(event, DecideAPI.getVotingById(Integer.valueOf(splittedId[3])), chartType);
        }
    }

}
