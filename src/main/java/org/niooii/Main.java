package org.niooii;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandReference;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.niooii.githubUtils.gitList;
import org.niooii.jupiter.UserList;
import org.niooii.listeners.GitHubListeners;
import org.niooii.listeners.JupiterAndToDoList;
import org.niooii.listeners.repoEditListener;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        JDA bot = JDABuilder.createDefault("MTA4NzEwNTg3ODMyMDIzNDU1Nw.GSsyrj.4ZvgBAqoWQ2F4zrObtRBTZpSF4qg0j54ShPkZo", GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS
                )
                .setActivity(Activity.playing("/link"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new JupiterAndToDoList(), new GitHubListeners(), new repoEditListener())
                .build();

        ArrayList<Command.Choice> months = new ArrayList<>(){
            {
                add(new Command.Choice("January", "Jan"));
                add(new Command.Choice("February", "Feb"));
                add(new Command.Choice("March", "Mar"));
                add(new Command.Choice("April", "Apr"));
                add(new Command.Choice("May", "May"));
                add(new Command.Choice("June", "Jun"));
                add(new Command.Choice("July", "Jul"));
                add(new Command.Choice("August", "Aug"));
                add(new Command.Choice("September", "Sep"));
                add(new Command.Choice("October", "Oct"));
                add(new Command.Choice("November", "Nov"));
                add(new Command.Choice("December", "Dec"));
            }
        };

        ArrayList<Command.Choice> bool = new ArrayList<>(){
            {
                add(new Command.Choice("Public", "public"));
                add(new Command.Choice("Private", "private"));
            }
        };

        //OptionData colorOptions = new OptionData(OptionType.STRING, "colors", "set a personalized color.", true).addChoices(months);

        //git command options
        OptionData repoVisibility = new OptionData(OptionType.STRING, "visibility", "set your repository's visibility", true).addChoices(bool);

        OptionData removeID = new OptionData(OptionType.INTEGER, "id", "task id", true).setMinValue(0).setMaxValue(24);

        SubcommandData subCommandData = new SubcommandData("repo", "repo stuff");

        CommandData data = Commands.slash("update", "update data for user");


        bot.updateCommands().addCommands(
                Commands.slash("link", "link jupiter account"),
                Commands.slash("unlink", "unlink jupiter account"),
                Commands.slash("fetch", "fetch data"),
                Commands.slash("update", "update data for user"),
                Commands.slash("todo", "fetch to-do list"),
                Commands.slash("add", "appends task to to-do list"),
                Commands.slash("clear", "clears to-do list"),
                Commands.slash("remove", "removes task from to-do list").addOptions(removeID),
                Commands.slash("git", "links your github account w/ personal access token").addOptions(new OptionData(OptionType.STRING, "pat", "enter personal access token", true)),
                Commands.slash("repocreate", "creates repository").addOptions(repoVisibility),
                Commands.slash("reposearch", "searches repositories by keyword").addOptions(new OptionData(OptionType.STRING, "keyword", "keyword to search by", true).setMinLength(1).setMaxLength(99)),
                Commands.slash("repodelete", "deletes repository").addOptions(new OptionData(OptionType.STRING, "name", "name of repository", true).setMinLength(1).setMaxLength(99)),
                Commands.slash("repolist", "lists repositories (very very bad)"),
                Commands.slash("sudo", "edits set repository"),
                Commands.slash("sudoexit", "stops editing mode"),
                Commands.slash("set", "sets repository for editing").addOptions(new OptionData(OptionType.STRING, "reponame", "name of repository", true).setMinLength(1).setMaxLength(99))
                //Commands.slash("setcolor", "set custom color for embeds").addOptions(colorOptions)
        ).queue();

        new UserList();
        new gitList();

    }
}