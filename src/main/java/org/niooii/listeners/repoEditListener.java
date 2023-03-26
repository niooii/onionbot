package org.niooii.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.*;
import org.niooii.githubUtils.gitList;
import org.niooii.githubUtils.repoEditUser;

import java.awt.*;
import java.io.IOException;

public class repoEditListener extends ListenerAdapter {

    public boolean isRegistered(String id){
        return list.getGitMap().containsKey(id);
    }

    public boolean repoExists(String name, GHUser myself){
        try{
            System.out.println(myself.getRepository(name).getHttpTransportUrl());
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public EmbedBuilder createEmbed(){
        return new EmbedBuilder();
    }

    public EmbedBuilder formatEmbed(String title, String desc, Color color, String url){
        EmbedBuilder embed = createEmbed();
        embed.setColor(color);
        if(url.length() == 0) embed.setTitle(title);
        else embed.setTitle(title, url);
        embed.setDescription(desc);
        embed.setThumbnail("https://cdn.discordapp.com/attachments/975541046329114654/1088582525359751239/5847f98fcef1014c0b5e48c0.png");
        embed.setFooter("skull - jithub", "https://cdn.discordapp.com/attachments/975541046329114654/1087167446835789888/9057d24b4f38cff3e985520e23c668b4.png");
        return embed;
    }

    public EmbedBuilder statusEmbed(String repoName, String desc, boolean failed, String url){
        EmbedBuilder embed = createEmbed();
        if(failed){
            embed.setColor(Color.DARK_GRAY);
        } else {
            embed.setColor(Color.white);
        }
        if(url.length() == 0) embed.setTitle(repoName);
        else embed.setTitle(repoName, url);
        embed.setDescription(desc);
        embed.setThumbnail("https://cdn.discordapp.com/attachments/975541046329114654/1089328823641854012/logomark-black_2x-removebg-preview.png");
        embed.setFooter("sudo mode", "https://cdn.discordapp.com/attachments/975541046329114654/1089327134067150929/images-removebg-preview.png");
        return embed;
    }

    gitList list = GitHubListeners.list;
    repoEditUser sudoUser;

    String authorId;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        GHRepository repo;
        authorId = event.getUser().getId();

        if(!isRegistered(authorId)){
            System.out.println("not registered??");
            return;
        }

        GHUser gitUser;

        try {
            gitUser = list.getGitMap().get(authorId).getMyself();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sudoUser = list.getRepoEditUser().get(authorId);
        list = GitHubListeners.list;
        String repoName;

        if(event.getName().equals("set")){
            if(sudoUser.isRepoEditMode()){
                EmbedBuilder embed = formatEmbed("Failed to set repository.", "New repository cannot be set within sudo mode.\nUse /sudoexit and retry.", Color.red, "");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            GHRepository tempRepo;

            event.deferReply().setEphemeral(true).queue();
            String name = event.getOption("reponame").getAsString();
            if(repoExists(name, gitUser)){
                list.setSudoName(authorId, name);
                try {
                    tempRepo = gitUser.getRepository(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                EmbedBuilder embed = formatEmbed("Failed to set repository.", "Repository does not exist.", Color.RED ,"");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            EmbedBuilder embed = formatEmbed("Set repository to: \"" + name + "\".", "", Color.white, tempRepo.getHttpTransportUrl());
            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
            return;
        }

        try {
            repoName = sudoUser.getRepoEditName();
            repo = gitUser.getRepository(repoName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(event.getName().equals("sudo")){
            if(sudoUser.getRepoEditName() == null){
                EmbedBuilder embed = formatEmbed("No repository set.", "Set a repository using /set.", Color.RED ,"");
                event.replyEmbeds(embed.build()).queue();
                return;
            }
            event.deferReply().queue();
            if(sudoUser.isRepoEditMode()){
                EmbedBuilder embed = formatEmbed("Something went wrong...", "You are already in sudo mode.", Color.RED, "");
                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            String name = sudoUser.getRepoEditName();
            if(repoExists(name, gitUser)){
                list.setSudoName(authorId, name);
            } else {
                EmbedBuilder embed = formatEmbed("Failed to edit repository.", "Repository " + name + " may not exist.", Color.RED ,"");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            list.setSudoMode(authorId, true);
            EmbedBuilder embed = formatEmbed("You are now in *sudo mode*.", "Editing repository: **" + sudoUser.getRepoEditName() + "**.", Color.white, repo.getHttpTransportUrl());
            embed.addField("Usage: ", "```-name <String>\n" +
                    "-desc <String>\n" +
                    "-v <public/private>\n" +
                    "-col <-a/-r> <-name>```", false);
            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
        }

        if(event.getName().equals("sudoexit")){
            event.deferReply().queue();
            if(!sudoUser.isRepoEditMode()){
                EmbedBuilder embed = formatEmbed("Something went wrong...", "You are not in sudo mode.", Color.RED, "");
                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            list.setSudoMode(authorId, false);
            EmbedBuilder embed = formatEmbed("You have exited *sudo mode*.", "Finished editing repository: **" + sudoUser.getRepoEditName() + "**.", Color.white, repo.getHttpTransportUrl());
            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        GHRepository repo;
        authorId = event.getAuthor().getId(); //initialize parameters
        if(!isRegistered(authorId)){
            return;
        }
        sudoUser = list.getRepoEditUser().get(authorId);
        if(!sudoUser.isRepoEditMode()){
            return;
        }
        authorId = event.getAuthor().getId();

        GHUser gitUser;

        try {
            gitUser = list.getGitMap().get(authorId).getMyself();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(!repoExists(sudoUser.getRepoEditName(), gitUser)){
            EmbedBuilder embed = formatEmbed("Repository does not exist anymore.", "Force-exited sudo mode.", Color.RED, "");
            list.setSudoMode(authorId, false);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        sudoUser = list.getRepoEditUser().get(authorId);
        list = GitHubListeners.list;
        String repoName;
        try {
            repoName = sudoUser.getRepoEditName();
            repo = gitUser.getRepository(repoName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String msg = event.getMessage().getContentRaw();
        Channel channel = event.getChannel().asTextChannel();

        if(msg.equals("echo")){
            event.getChannel().sendMessage("echo").queue();
            return;
        } else if(msg.startsWith("-v")){ //SET VISIBLITY
            if(msg.substring(msg.indexOf(" ") + 1).equals("public")){
                try {
                    repo.setVisibility(GHRepository.Visibility.PUBLIC);
                    EmbedBuilder embed = statusEmbed(repoName, "Set visibility to **public**.", false, repo.getHtmlUrl().toString());
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else if(msg.substring(msg.indexOf(" ") + 1).equals("private")){
                try {
                    repo.setVisibility(GHRepository.Visibility.PRIVATE);
                } catch (IOException e) {
                    System.out.println(e);
                }
                EmbedBuilder embed = statusEmbed(repoName, "Set visibility to **private**.", false, repo.getHtmlUrl().toString());
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        } else if(msg.startsWith("-name")){ //SET NAME
            String name = msg.substring(msg.indexOf(" ") + 1);
            try {
                name = name.replaceAll(" ", "-");
                String tempName = repo.getName();
                repo.renameTo(name);
                list.setSudoName(authorId, name);
                EmbedBuilder embed = statusEmbed(name, "renamed **" + tempName + "** to **" + name + "**.", false, repo.getHtmlUrl().toString());
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } catch (IOException e) {
                EmbedBuilder embed = statusEmbed("something went wrong...", "", true, repo.getHtmlUrl().toString());
                embed.addField("possible reasons: ", "```- repository " + name + " may already exist.\n" +
                        "- name contains illegal characters.\n" +
                        "- name is not between 1 - 99 characters in length.```", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        } else if(msg.startsWith("-desc")){ //SET DESC
            String desc = msg.substring(msg.indexOf(" ") + 1);
            try {
                repo.setDescription(desc);
                EmbedBuilder embed = statusEmbed(repoName, "set description: \n```" + desc + "```", false, repo.getHtmlUrl().toString());
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } catch (IOException e) {
                EmbedBuilder embed = statusEmbed("something went wrong...", "", true, repo.getHtmlUrl().toString());
                embed.addField("possible reasons: ", "```- description is not between 1 - 255 characters in length.```", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        } else if(msg.startsWith("-col")){ //collaborator commands
            msg = msg.substring(msg.indexOf(" ") + 1);
            String name = msg.substring(msg.indexOf(" ") + 1);
            System.out.println(name);
            GHUser user = new GHUser();
            try { //check if user exists
                user = list.getGitConnection(authorId).getUser(name);
                System.out.println(user.getCreatedAt());
            } catch (IOException e) { //user doesnt exist
                EmbedBuilder embed = statusEmbed("something went wrong...", "", true, repo.getHtmlUrl().toString());
                embed.addField("possible reasons: ", "```- user " + name + " does not exist.```", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            String userUrl = user.getHtmlUrl().toString();
            if(msg.startsWith("-a")){ //add
                try {
                    repo.addCollaborators(user);
                    EmbedBuilder embed = statusEmbed(repoName, "invited user [" + name + "](" + userUrl + ")" + "\n[click to view invites](" + repo.getHtmlUrl() + "/invitations" + ")", false, repo.getHtmlUrl().toString());
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            if(msg.startsWith("-r")){ //add
                try {
                    repo.removeCollaborators(user);
                    EmbedBuilder embed = statusEmbed(repoName, "removed user [" + name + "](" + userUrl + ")", false, repo.getHtmlUrl().toString());
                    embed.addField("WARNING: does not work for *pending* invites", "[view pending invites here](" + repo.getHtmlUrl() + "/invitations" + ")", false);
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
}