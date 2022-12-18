package org.lamuela.commands;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.lamuela.Storage;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ManageVoting extends ListenerAdapter {
    private final Pattern votingPattern = Pattern.compile("^\\w+_voting_(\\d+)$");
    private final Pattern startVotingPattern = Pattern.compile("^start_voting_\\d+$");
    private final Pattern stopVotingPattern = Pattern.compile("^stop_voting_\\d+$");

    private static final Map<String, Store> channelsData = new HashMap<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        String buttonId = event.getButton().getId();
        Matcher matcher = votingPattern.matcher(buttonId);
        if(!matcher.find()) return;
        String votingId = matcher.group(1);

        if(startVotingPattern.matcher(buttonId).matches()) {
            InteractionHook interaction = event.deferReply().setEphemeral(true).complete();
            if(startVotingChannel(votingId, event.getGuild(), event.getMessage())) {
                interaction.editOriginal("Votacion " + votingId + " comenzada.").queue();
            } else {
                interaction.editOriginal("Esta votación ya fue empezada.").queue();
            }
        }

        if(stopVotingPattern.matcher(buttonId).matches()) {
            InteractionHook interaction = event.deferReply().setEphemeral(true).complete();
            if(deleteVotingChannel(votingId, event.getGuild())) {
                interaction.editOriginal("Votacion " + votingId + " terminada.").queue();
            } else {
                interaction.editOriginal("Esta votación ya fue terminada o no esta empezada.").queue();
            }
        }

    }

    public static boolean startVotingChannel(String votingId, Guild guild, Message message) {
        String nameId = String.format("voting-%s", votingId);

        Voting voting = DecideAPI.getVotingById(Integer.parseInt(votingId));
        if(Objects.nonNull(voting.getStartDate())) return false;

        DecideAPI.updateVoting(voting, "start");

        Category category = guild.getCategoryById(Storage.getVotingCategoryId());
        Role votingRole = guild.createRole().setName(nameId).complete();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(voting.getName());
        builder.setDescription(voting.getQuestion().getDesc() + "\n" + votingRole.getAsMention());

        List<Option> answers= voting.getQuestion().getOptions();
        List<ActionRow> actionRows = new ArrayList<>();
        List<Button> actual = new ArrayList<>();

        for(int i=0; i<answers.size(); i++) {
            if(i%5 == 0 && !actual.isEmpty()) {
                actionRows.add(ActionRow.of(actual));
                actual = new ArrayList<>();
            }
            actual.add(Button.secondary(String.format("voting_%s_option_%d", votingId, answers.get(i).getNumber()), answers.get(i).getAnswer()));
        }
        if(!actual.isEmpty()){
            actionRows.add(ActionRow.of(actual));
        }

        TextChannel channel = category.createTextChannel(nameId)
                .addPermissionOverride(guild.getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue())
                .addPermissionOverride(
                        guild.getRoleById(Storage.getAdminRoleId()),
                        Permission.VIEW_CHANNEL.getRawValue(),
                        Permission.MESSAGE_SEND.getRawValue())
                .addPermissionOverride(
                        votingRole,
                        Permission.VIEW_CHANNEL.getRawValue(),
                        Permission.MESSAGE_SEND.getRawValue()
                ).complete();
        channelsData.put(votingId, new Store(channel.getId(), votingRole.getId(), message.getId()));
        channel.sendMessageEmbeds(builder.build()).addComponents(actionRows).queue();
        message.editMessageEmbeds(
                CreateChannel.generateEmbed(DecideAPI.getVotingById(Integer.parseInt(votingId)))
        ).queue();

        return true;
    }

    private boolean deleteVotingChannel(String votingId, Guild guild) {

        if(!channelsData.containsKey(votingId))    return false;

        Voting voting = DecideAPI.getVotingById(Integer.parseInt(votingId));
        if(Objects.isNull(voting.getStartDate()) || Objects.nonNull(voting.getEndDate()))   return false;

        Store data = channelsData.get(votingId);

        DecideAPI.updateVoting(voting, "stop");
        guild.getTextChannelById(data.channelId).delete().queue();
        guild.getRoleById(data.roleId).delete().queue();
        guild.getTextChannelById(Storage.getAdminChannelId()).retrieveMessageById(data.messageId).complete()
                .editMessageEmbeds(
                    CreateChannel.generateEmbed(DecideAPI.getVotingById(Integer.parseInt(votingId)))
                ).queue();
        DecideAPI.updateVoting(voting, "tally");

        return true;
    }

    private static class Store {
        private String channelId;
        private String roleId;
        private String messageId;

        public Store(String channelId, String roleId, String messageId) {
            this.channelId = channelId;
            this.roleId = roleId;
            this.messageId = messageId;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getRoleId() {
            return roleId;
        }

        public String getMessageId() {
            return messageId;
        }
    }

}

