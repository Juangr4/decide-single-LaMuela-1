package org.lamuela.commands;

import java.util.Arrays;
import java.util.Objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.lamuela.Storage;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Voting;
import org.lamuela.statistics.ChartType;
import org.lamuela.statistics.StatisticsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CreateChannel extends ListenerAdapter {

    private static final String ROLE_NAME = "voting-admin";

    private static final String CATEGORY_NAME = "decide-voting";

    private static final String CHANNEL_NAME = "voting";

    private static int lastId = -1;

    private static void initBot(Guild guild) {
        guild.getRolesByName(ROLE_NAME, true).forEach(v -> v.delete().queue());
        guild.getCategoriesByName(CATEGORY_NAME, true).forEach(category -> {
            category.getChannels().forEach(c -> c.delete().queue());
            category.delete().queue();
        });

        Role role = guild.createRole().setName(ROLE_NAME).complete();
        Storage.ADMIN_VOTING_ROLE = role.getId();
        Category category = guild.createCategory(CATEGORY_NAME).complete();
        Storage.VOTING_CATEGORY = category.getId();
        TextChannel channel = category.createTextChannel(CHANNEL_NAME).addPermissionOverride(
                guild.getPublicRole(),
                0,
                Permission.VIEW_CHANNEL.getRawValue()
        ).addPermissionOverride(
                role,
                Permission.VIEW_CHANNEL.getRawValue(),
                0
        ).complete();
        Storage.ADMIN_VOTING_CHANNEL = channel.getId();

        for (Voting voting: DecideAPI.getAllVotings()) {
            channel.sendMessageEmbeds(generateEmbed(voting)).addActionRow(
                    Button.primary(String.format("start_voting_%d", voting.getId()), "Empezar votacion"),
                    Button.danger(String.format("stop_voting_%d", voting.getId()), "Terminar votacion"),
                    Button.success(String.format("stats_voting_%d", voting.getId()), "Obtener resultados")
            ).queue();
            lastId = Math.max(lastId, voting.getId());
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event){
        initBot(event.getGuild());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        initBot(event.getGuild());
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event){
        if(!event.getModalId().equals("voting"))    return;
        int lastSend = lastId;
        TextChannel channel = event.getGuild().getTextChannelById(Storage.ADMIN_VOTING_CHANNEL);
        for(Voting voting: DecideAPI.getAllVotings()) {
            if(voting.getId() > lastSend) {
                channel.sendMessageEmbeds(generateEmbed(voting)).addActionRow(
                        Button.primary(String.format("start_voting_%d", voting.getId()), "Empezar votacion"),
                        Button.danger(String.format("stop_voting_%d", voting.getId()), "Terminar votacion"),
                        Button.success(String.format("stats_voting_%d", voting.getId()), "Obtener resultados")
                ).queue();
                lastId = Math.max(lastId, voting.getId());
            }
        }
    }

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

    public static MessageEmbed generateEmbed(Voting voting) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(voting.getName());
        builder.setDescription(voting.getDesc());
        if(Objects.nonNull(voting.getStartDate())) {
            builder.addField("Fecha comienzo", voting.getStartDate(), true);
        } else {
            builder.addField("Fecha comienzo", "No empezada", true);
        }
        if(Objects.nonNull(voting.getEndDate())) {
            builder.addField("Fecha final", voting.getEndDate(), true);
        } else {
            builder.addField("Fecha final", "No terminada", true);
        }
        return builder.build();
    }
}