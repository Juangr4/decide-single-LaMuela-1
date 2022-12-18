package org.lamuela.commands;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;
import org.lamuela.sqlite3.SQLMethods;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PerformVoting extends ListenerAdapter {

    private final Pattern voteButtonPattern = Pattern.compile("^voting_(\\d+)_option_(\\d+)$");

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Matcher matcher = voteButtonPattern.matcher(event.getButton().getId());

        if(!matcher.matches())  return;

        String token = SQLMethods.getTokenByDiscUser(event.getMember().getUser().getName());
        if(Objects.isNull(token)) {
            event.reply("Tienes que estar logeado para votar").setEphemeral(true).queue();
            return;
        }

        int votingId = Integer.parseInt(matcher.group(1));
        int optionNumber = Integer.parseInt(matcher.group(2));

        Voting voting = DecideAPI.getVotingById(votingId);

        Optional<Option> option = voting.getQuestion().getOptions().stream()
                .filter(op -> op.getNumber() == optionNumber)
                .findFirst();

        option.ifPresent(value -> {
            DecideAPI.addCensus(voting, List.of(DecideAPI.getUser(token)));
            DecideAPI.performVote(voting, value, token);
        });

        event.reply("Votacion realizada correctamente").setEphemeral(true).queue();

    }

}

