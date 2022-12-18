package org.lamuela.commands;

import java.util.Objects;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.lamuela.Storage;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Voting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import static org.lamuela.commands.ManageVoting.createVotingChannel;

public class CreateChannel extends ListenerAdapter {

    private static final String ROLE_NAME = "voting-admin";

    private static final String CATEGORY_NAME = "decide-voting";

    private static final String CHANNEL_NAME = "voting";

    private static int lastId = -1;

    private static void updateLastIndex(int newIndex) {
        lastId = Math.max(lastId, newIndex);
    }

    private static void initBot(Guild guild) {
        for(Role role: guild.getRoles()) {
            if(role.getName().equals(ROLE_NAME) || role.getName().matches("voting-(\\d+)")) {
                role.delete().queue();
            }
        }
        guild.getCategoriesByName(CATEGORY_NAME, true).forEach(category -> {
            category.getChannels().forEach(c -> c.delete().queue());
            category.delete().queue();
        });

        Role role = guild.createRole().setName(ROLE_NAME).complete();
        Storage.setAdminRoleId(role.getId());
        Category category = guild.createCategory(CATEGORY_NAME).complete();
        Storage.setVotingCategoryId(category.getId());
        TextChannel channel = category.createTextChannel(CHANNEL_NAME).addPermissionOverride(
                guild.getPublicRole(),
                0,
                Permission.VIEW_CHANNEL.getRawValue()
        ).addPermissionOverride(
                role,
                Permission.VIEW_CHANNEL.getRawValue(),
                0
        ).complete();
        Storage.setAdminChannelId(channel.getId());

        for (Voting voting: DecideAPI.getAllVotings()) {
            Message msg = channel.sendMessageEmbeds(generateEmbed(voting)).addActionRow(
                    Button.primary(String.format("start_voting_%d", voting.getId()), "Empezar votacion"),
                    Button.danger(String.format("stop_voting_%d", voting.getId()), "Terminar votacion"),
                    Button.success(String.format("stats_voting_%d", voting.getId()), "Obtener resultados")
            ).complete();
            if(Objects.nonNull(voting.getStartDate()) && Objects.isNull(voting.getEndDate())) {
                createVotingChannel(guild, voting, msg);
            }
            updateLastIndex(voting.getId());
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

    public static void updateVoting(Guild guild){
        int lastSend = lastId;
        TextChannel channel = guild.getTextChannelById(Storage.getAdminChannelId());
        for(Voting voting: DecideAPI.getAllVotings()) {
            if(voting.getId() > lastSend) {
                channel.sendMessageEmbeds(generateEmbed(voting)).addActionRow(
                        Button.primary(String.format("start_voting_%d", voting.getId()), "Empezar votacion"),
                        Button.danger(String.format("stop_voting_%d", voting.getId()), "Terminar votacion"),
                        Button.success(String.format("stats_voting_%d", voting.getId()), "Obtener resultados")
                ).queue();
                updateLastIndex(voting.getId());
            }
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