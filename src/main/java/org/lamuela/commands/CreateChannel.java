package org.lamuela.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.lamuela.api.DecideAPI;
import org.lamuela.api.models.Option;
import org.lamuela.api.models.Voting;
import org.lamuela.statistics.ChartType;
import org.lamuela.statistics.StatisticsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CreateChannel extends ListenerAdapter{
    
    Boolean sendChannelBoolean=true;
    Boolean sendRoleBoolean=true;

    @Override
    public void onGuildReady(GuildReadyEvent event){
        List<Role> listRole=event.getGuild().getRolesByName("Votaciones", false);
        if(listRole.isEmpty()){
            event.getGuild().createRole().setName("Votaciones").queue();
        }
        else{
            Integer listRoleSize=listRole.size();
            for(Integer i=0; i<listRoleSize; i++){
                Role role=listRole.get(i);
                role.delete().reason("deber").queue();
            }
            event.getGuild().createRole().setName("Votaciones").queue();
        }
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event){
        List<TextChannel> listChannel=event.getGuild().getTextChannelsByName("votaciones", false);
        if(!channelCreateVotaciones(event.getGuild().getTextChannels())){
            event.getGuild().createTextChannel("votaciones").addPermissionOverride(event.getGuild().getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue()).addRolePermissionOverride(event.getGuild().getRolesByName("Votaciones", false).get(0).getIdLong(), Permission.VIEW_CHANNEL.getRawValue(), 0).queue();
        }
        else{
            Integer listChannelSize=listChannel.size();
            if(sendRoleBoolean){
                for(Integer i=0; i<listChannelSize; i++){
                    TextChannel channel=listChannel.get(i);
                    channel.delete().reason("deber").queue();
                }
                event.getGuild().createTextChannel("votaciones").addPermissionOverride(event.getGuild().getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue()).addRolePermissionOverride(event.getGuild().getRolesByName("Votaciones", false).get(0).getIdLong(), Permission.VIEW_CHANNEL.getRawValue(), 0).queue();
                sendRoleBoolean= false;
            }
        }
    }

    public Boolean channelCreateVotaciones(List<TextChannel> listChannel){
        Boolean channelName= false;
        for(GuildChannel channel: listChannel){
            if(channel.getName().equals("votaciones")){
                channelName= true;
            }
        }
        return channelName;
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event){
        Voting[] list=DecideAPI.getAllVotings();
        String message="";
        if(sendChannelBoolean){
            createAllVoting(list, message, event.getGuild(), event.getChannel().getId());
            sendChannelBoolean=false;
        }
    }

    public void createAllVoting(Voting[] list, String message, Guild eventGuild, String id){
        for (Integer i=0; i<list.length; i++){
            message+="`Voting`\n";
            message+="`"+list[i].getQuestion().getDesc()+":`"+"\n";
            for (String im: list[i].getQuestion().getOptions().stream().map(Option::getOption).collect(Collectors.toList())){
                message+="`"+im+"`"+"\n";
            }
        EmbedBuilder button= new EmbedBuilder().setDescription(message);
        Button buttonStart = Button.primary("start_voting_"+list[i].getId(), "Start Button");
        Button buttonEnd = Button.danger("end_voting_"+list[i].getId(), "End Button");
        Button buttonGraphic = Button.success("start_graphic_"+list[i].getId(), "Graphic Button");
        eventGuild.getTextChannelById(id).sendMessageEmbeds(button.build()).setActionRow(buttonStart, buttonEnd, buttonGraphic).queue();
        message="";
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] splittedId = event.getButton().getId().split("_");
        if(splittedId[0].equals("start") && splittedId[1].equals("graphic") ){   //Si es un button graphic
            StatisticsManager.sendGraphTypeSelector(event, splittedId);
        }
        if(splittedId[0].equals("show") && splittedId[2].equals("graph")){
            ChartType chartType = Arrays.asList(ChartType.values()).stream().filter(type -> type.name().equalsIgnoreCase(splittedId[1])).findFirst().get();
            StatisticsManager.showStatistic(event, DecideAPI.getVotingById(Integer.valueOf(splittedId[3])), chartType);
        } 
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        Voting[] list= DecideAPI.getAllVotings();
        String message="";
        if(event.getAuthor().isBot() && !event.getMessage().getContentRaw().equals("")){  //Posibles cambios si se añade moderación
            List<TextChannel> listChannel=event.getGuild().getTextChannelsByName("votaciones", false);
            Integer listChannelSize=listChannel.size();
            if(listChannelSize>1){
                sendChannelBoolean=true;
                for(Integer i=0; i<listChannelSize; i++){
                TextChannel channel=listChannel.get(i);
                channel.delete().reason("deber").queue();                
                }
                event.getGuild().createTextChannel("votaciones").addPermissionOverride(event.getGuild().getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue()).addRolePermissionOverride(event.getGuild().getRolesByName("Votaciones", false).get(0).getIdLong(), Permission.VIEW_CHANNEL.getRawValue(), 0).queue();
            }
            else{
                String id= listChannel.get(0).getId();
                createAllVoting(list, message, event.getGuild(), id);
            }
        }
    }
}