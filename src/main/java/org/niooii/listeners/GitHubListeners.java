package org.niooii.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.*;
import org.niooii.githubUtils.gitList;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GitHubListeners extends ListenerAdapter {

    boolean pub = true;

    public GitHubListeners() throws IOException {
    }

    public EmbedBuilder createEmbed(){
        return new EmbedBuilder();
    }

    public EmbedBuilder formatEmbed(String title, String desc, Color color){
        EmbedBuilder embed = createEmbed();
        embed.setColor(color);
        embed.setTitle(title);
        embed.setDescription(desc);
        embed.setThumbnail("https://cdn.discordapp.com/attachments/975541046329114654/1088582525359751239/5847f98fcef1014c0b5e48c0.png");
        embed.setFooter("skull - jithub", "https://cdn.discordapp.com/attachments/975541046329114654/1087167446835789888/9057d24b4f38cff3e985520e23c668b4.png");
        return embed;
    }

    String authorId;

    String tempName;

    public static gitList list;

    static {
        try {
            list = new gitList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRegistered(String authorId){
        return list.getGitMap().containsKey(authorId);
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        authorId = event.getUser().getId();
        if(event.getName().equals("git")){
            event.deferReply().queue();
            EmbedBuilder embed;
            if(isRegistered(authorId)){
                GHUser myself = null;
                try {
                    myself = list.getGitConnection(authorId).getMyself();
                } catch (IOException e) {
                    System.out.println("bruh?");
                }
                embed = formatEmbed("You already have an account linked!", "Linked account: **" + myself.getHtmlUrl() + "**", Color.BLACK);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            String token = event.getOption("pat").getAsString();
            try {
                if(list.addUser(authorId, token)){
                    GHUser myself = list.getGitConnection(authorId).getMyself();
                    embed = formatEmbed("Successfully connected GitHub account! ", "Linked account: **" + myself.getHtmlUrl() + "**", Color.BLACK);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                } else {
                    embed = formatEmbed("Failed to link account.", "Double-check your personal access token.", Color.RED);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            } catch (IOException e) {
                System.out.println("bruh?");
            }
        }
        else if(event.getName().equals("repocreate")){
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("You don't have an account linked!", "To link an account, generate a PAT token and use /git.", Color.RED);
                event.replyEmbeds(embed.build()).queue();
                return;
            }
            GHUser myself;
            try {
                myself = list.getGitConnection(authorId).getMyself();
            } catch (IOException e) {
                System.out.println("...");
                return;
            }
            TextInput repoName = TextInput.create("repoName", "Repository name", TextInputStyle.SHORT).setMinLength(1).setMaxLength(99)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();
            TextInput repoDesc = TextInput.create("repoDesc", "Repository description", TextInputStyle.SHORT).setMaxLength(255)
                    .setMinLength(1)
                    .setRequired(false)
                    .build();
            Modal modal = Modal.create("repoCreate", "Creating a Repository for " + myself.getHtmlUrl().toString().substring(myself.getHtmlUrl().toString().lastIndexOf('/') + 1))
                    .addActionRows(ActionRow.of(repoName), ActionRow.of(repoDesc))
                    .build();

            if(event.getOption("visibility").getAsString().equals("private")) pub = false;

            event.replyModal(modal).queue();
            return;
        } else if(event.getName().equals("repodelete")){
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("You don't have an account linked!", "To link an account, generate a PAT token and use /git.", Color.RED);
                event.replyEmbeds(embed.build()).queue();
                return;
            }
            EmbedBuilder embed;
            String url = "";
            String name = event.getOption("name").getAsString();
            GHUser myself;
            try {
                myself = list.getGitConnection(authorId).getMyself();
            if (myself.getRepository(name) == null) {
                event.deferReply().queue();
                embed = formatEmbed("Failed to delete repository.", "Repository does not exist.", Color.RED);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
                url = myself.getRepository(name).getHttpTransportUrl();
            embed = formatEmbed("⚠ Are you sure you want to delete \"" + name + "\" ? ⚠" , "The contents of this repository can be found here: \n " + url, Color.black);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } catch(Exception e){
                System.out.println("bruh " + e);
            }
            TextInput confirm = TextInput.create("confirm", "TYPE \"CONFIRM\" TO CONTINUE.", TextInputStyle.SHORT).setMinLength(7)
                    .setMaxLength(7)
                    .setRequired(true)
                    .build();
            Modal modal = Modal.create("repoDelete", "Deleting repository \"" + name + "\"")
                    .addActionRows(ActionRow.of(confirm))
                    .addComponents()
                    .build();
            tempName = name;
            event.replyModal(modal).queueAfter(2, TimeUnit.SECONDS);
        } else if(event.getName().equals("repolist")){
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("You don't have an account linked!", "To link an account, generate a PAT token and use /git.", Color.RED);
                event.replyEmbeds(embed.build()).queue();
                return;
            }
            GHUser myself;
            Map<String, GHRepository> repoMap;
            try{
                myself = list.getGitConnection(authorId).getMyself();
                repoMap =  myself.getRepositories();
            } catch(Exception e){
                System.out.println(e);
                return;
            }
            event.deferReply().queue();

            ArrayList<HashMap<String, GHRepository>> repoMapLists = new ArrayList<>();
            int k = 0;
            for(int i = repoMap.size(); i >= k; i-=25){
                int j = 0;
                HashMap<String, GHRepository> temp = new HashMap<>();
                for (Map.Entry<String, GHRepository> elem : repoMap.entrySet()) {
                    if(j >= 25) break;
                    temp.put(elem.getKey(), elem.getValue());
                    j++;
                }
                repoMapLists.add(temp);
                k=25;
            }
            String myName = myself.getHtmlUrl().toString().substring(myself.getHtmlUrl().toString().lastIndexOf('/') + 1);
            EmbedBuilder embed = formatEmbed("Displaying " + myName + "'s repositories", "", Color.black);
            System.out.println("checkpoint1");
            System.out.println(repoMapLists.size());
            for(HashMap<String, GHRepository> map : repoMapLists){
                System.out.println("working?");
                map.forEach((key, v)
                        -> {
                    embed.addField(key + "\n*[" + v.getVisibility().toString() + "]*", "insert info here " + "" + "\n[remote](" + v.getHttpTransportUrl() + ")", false);
                }); //cannot be bothered to use string.format
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                System.out.println("messages sent supposedly.");
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("repoCreate")){
            event.deferReply().queue();
            String repoDesc;
            String repoName = event.getValue("repoName").getAsString();
            try{
                repoDesc = event.getValue("repoDesc").getAsString();
            } catch(NullPointerException e){
                System.out.println(e);
                repoDesc = "";
            }
            EmbedBuilder embed;
            GHUser user = new GHUser();
            GitHub connection = list.getGitConnection(authorId);
            try {
                user = list.getGitConnection(authorId).getMyself();
            } catch (Exception e) {
                System.out.println(e);
                //embed = formatEmbed("", "removed user from database. please use /git to link again.", Color.RED);
            }
                System.out.println("trying to create repo...");
                try {
                    if(user.getRepository(repoName) != null){
                        embed = formatEmbed("Failed to create repository.", "Repository already exists at " + user.getRepository(repoName).getHttpTransportUrl() + ".", Color.RED);
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                    connection.createRepository(repoName).create();
                    System.out.println("line before user");
                    GHRepository repo = user.getRepository(repoName);
                    System.out.println("got user");
                    embed = formatEmbed("Created repository.", "Initialized repository at " + repo.getHttpTransportUrl() + "\nSetting description...", Color.black);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    repo.setDescription(repoDesc);
                    embed.setDescription("Initialized repository at " + repo.getHttpTransportUrl() + "\nSetting visibility...");
                    event.getHook().editOriginalEmbeds(embed.build()).queue();
                    if(pub){
                        repo.setVisibility(GHRepository.Visibility.PUBLIC);
                    } else {
                        repo.setVisibility(GHRepository.Visibility.PRIVATE);
                    }
                    pub = true;
                    embed.setDescription("Initialized repository at " + repo.getHttpTransportUrl() + "\nSuccess!");
                    embed.addField("Getting started: ",
                            "```- git init\n" +
                            "- git commit -m \"first commit\"\n" +
                            "- git remote add origin "+ repo.getHttpTransportUrl() + "\n" +
                            "- git branch -M main\n" +
                            "- git push -u origin main```", false);
                    event.getHook().editOriginalEmbeds(embed.build()).queue();
                    return;
                } catch (Exception e) {
                    System.out.println(e);
                    embed = formatEmbed("Failed to create repository.", "**Possible reasons:** \n" +
                            "1. Repository already exists.\n" +
                            "2. Invalid characters in name.\n" +
                            "3. Invalid personal access token.", Color.RED);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            }

        if(event.getModalId().equals("repoDelete")) {
            String repoName = tempName;
            tempName = "";
            String confirm = event.getValue("confirm").getAsString();
            EmbedBuilder embed;
            GHUser user = new GHUser();
            GitHub connection = list.getGitConnection(authorId);
            if(!confirm.equalsIgnoreCase("confirm")){
                event.reply("Type confirm to delete!").setEphemeral(true).queue();
                return;
            }
            event.deferReply().queue();
            try {
                user = connection.getMyself();
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("trying to delete repo...");
            try {
                GHRepository repo = user.getRepository(repoName);
                System.out.println("got past getting repo lol");
                repo.delete();
                System.out.println("deleted repo");
                embed = formatEmbed("Deleted repository " + repoName + ".", "", Color.black);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            } catch (Exception e) {
                System.out.println(e);
                embed = formatEmbed("Failed to delete repository.", "**Possible reasons:** \n" +
                        "1. Repository is already deleted.\n" +
                        "2. Invalid personal access token.", Color.RED);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}

