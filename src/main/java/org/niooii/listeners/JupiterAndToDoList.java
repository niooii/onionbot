package org.niooii.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.niooii.jupiter.*;
import org.openqa.selenium.NoSuchElementException;
import com.example.niooii.jupitered.Course;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.time.Instant;

public class JupiterAndToDoList extends ListenerAdapter {
    String missingText = "||All assignments complete!||";

    public EmbedBuilder createEmbed(){
        return new EmbedBuilder();
    }

    public EmbedBuilder formatEmbed(String title, String desc, Color color){
        EmbedBuilder embed = createEmbed();
        embed.setColor(color);
        embed.setTitle(title);
        embed.setDescription(desc);
        embed.setThumbnail("https://cdn.discordapp.com/attachments/975541046329114654/1087166979657453638/unnamed-removebg-preview.png");
        embed.setFooter("Made by niooi#2923 - i hope this isnt illegal", "https://cdn.discordapp.com/attachments/975541046329114654/1087167446835789888/9057d24b4f38cff3e985520e23c668b4.png");
        return embed;
    }


    static UserList userList = new UserList();

    JupiterSession session;

    String authorId;
    int min = 0;
    int add = 10;
    int it;
    CourseData selectedCourse;
    Button prev = Button.danger("previous", "⬅");
    Button next = Button.success("forward", "➡");

    public boolean isRegistered(String id){
        return userList.getUserMap().containsKey(authorId);
    }

    public JupiterSession getSession(String id) throws InterruptedException {
        String osis = userList.getUserMap().get(id).getOsis();
        String password = userList.getUserMap().get(id).getPassword();
        return new JupiterSession(osis, password);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        authorId = event.getUser().getId();

        if(event.getName().equals("todo")){
            event.deferReply().queue();
            User user = userList.getUserMap().get(authorId);
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed.", "You don't have a linked account.\nTo link an account, use /link", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            EmbedBuilder embed = formatEmbed("Displaying tasks for " + user.getJupiterData().getName(), "", Color.BLACK);
            if(user.getToDoList().size() == 0){
                embed.setTitle("No tasks available! ");
                embed.setColor(Color.green);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            } else {
                for(int i = 0; i < user.getToDoList().size(); i++){
                    embed.addField("Id:  " + i, user.getToDoList().get(i), false);
                }
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        }

        if(event.getName().equals("add")){
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed.", "You don't have a linked account.\nTo link an account, use /link", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            if(userList.getUserMap().get(authorId).getToDoList().size() == 25){
                EmbedBuilder embed = formatEmbed("Failed.", "You cannot have more than 25 tasks concurrently.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            TextInput taskInput = TextInput.create("task", "enter task description", TextInputStyle.PARAGRAPH)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("taskAdd", "Add Task")
                    .addActionRows(ActionRow.of(taskInput))
                    .build();

            event.replyModal(modal).queue();
        }

        if(event.getName().equals("remove")){
            event.deferReply().setEphemeral(true).queue();
            User user = userList.getUserMap().get(authorId);
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed.", "You don't have a linked account.\nTo link an account, use /link", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            int id = event.getOption("id").getAsInt();
            String tempTask = "";
            if(id < user.getToDoList().size() && id >= 0)
            tempTask = user.getToDoList().get(id);
            if(userList.deleteTask(authorId, id)){
                EmbedBuilder embed = formatEmbed("Task removed!", "", Color.ORANGE);
                embed.addField("Removed task with id \" " + id + "\":", tempTask, true);
                if(id < user.getToDoList().size()){
                    embed.addField("New task with id \"" + id + "\":", user.getToDoList().get(id), false);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    return;
                } else{
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
            } else {
                EmbedBuilder embed = formatEmbed("Failed.", "Cannot remove index " + id + " for list of size " + user.getToDoList().size() + ".", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

        }

        if(event.getName().equals("clear")){
            event.deferReply().setEphemeral(true).queue();
            User user = userList.getUserMap().get(authorId);
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed.", "You don't have a linked account.\nTo link an account, use /link", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            if(userList.clearTaskList(authorId)){
                EmbedBuilder embed = formatEmbed("To-do list cleared!", "Cleared successfully.", Color.LIGHT_GRAY);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            } else {
                EmbedBuilder embed = formatEmbed("Failed.", "Your list is already clear.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

        }

        if(event.getName().equals("unlink")){
            event.deferReply().queue();
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed.", "You don't have a linked account.\nTo link an account, use /link", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            JupiterData data = userList.getJupiterData(authorId);
            String tempName = data.getName();
            userList.removeUser(authorId);
            EmbedBuilder embed = formatEmbed("Successfully unlinked account!", "No longer linked to user: **" + tempName + "**.", Color.cyan);
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

        if(event.getName().equals("link")){

            if(isRegistered(authorId)){
                event.deferReply().queue();
                JupiterData currentUserData = userList.getJupiterData(authorId);
                EmbedBuilder embed = formatEmbed("Failed.", "You are already linked to **" + currentUserData.getName() + "**.\nWant to unlink your account? /unlink", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            TextInput osisInput = TextInput.create("osis", "osis/username", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();
            TextInput pwInput = TextInput.create("pw", "password", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("register", "register")
                    .addActionRows(ActionRow.of(osisInput), ActionRow.of(pwInput))
                    .build();

            event.replyModal(modal).queue();
        }

        if(event.getName().equals("fetch")){
            event.deferReply().queue();
            AtomicLong counter2 = new AtomicLong(0);
            if(!isRegistered(authorId)){
                EmbedBuilder embed = formatEmbed("Failed to fetch data.", "Please link your account.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            } else {
                StringSelectMenu.Builder builder = StringSelectMenu.create("courses").setPlaceholder("Select a course to view more.");
                JupiterData currentUserData = userList.getJupiterData(authorId);
                builder.setMaxValues(1);
                EmbedBuilder embed = formatEmbed("Successfully fetched data for " + currentUserData.getName(), "", Color.cyan);
                embed.addField("⠀\n\uD83D\uDCDC  Enrolled Courses  \uD83D\uDCDC", "", false);
                for(CourseData course : currentUserData.getCourses()){
                    builder.addOption(course.getName(), ""+ counter2.getAndIncrement());
                    if(course.getMissing() != 0) missingText = course.getMissing() + " assignments missing.";
                    embed.addField("__" + course.getName() + "__:  " + course.getGradeAverage() + "%", missingText, false);
                    missingText = "||All assignments complete!||";
                }
                counter2.set(0);
                embed.addField(" ", " ", false);
                embed.addField("\uD83D\uDCCA  Assignment Statistics  \uD83D\uDCCA", " ", false);
                currentUserData.getAssignmentStats().forEach((key,value) -> {
                    embed.addField(key, String.valueOf(value), true);
                });
                embed.addField("⠀\n⠀\n\uD83D\uDD52  Last Updated  \uD83D\uDD52", "⠀\n<t:" + userList.getUserMap().get(authorId).getLastUpdated() + ":R>\n⠀", false);
                event.getHook().sendMessageEmbeds(embed.build()).setActionRow(builder.build()).queue();
            }
        }

        if(event.getName().equals("update")){
            AtomicLong counter = new AtomicLong();
            if(!isRegistered(authorId)){
                event.deferReply().queue();
                EmbedBuilder embed = formatEmbed("Failed to update data.", "Please link your account.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            User user = userList.getUserMap().get(authorId);
            String osis = user.getOsis();
            String pw = user.getPassword();
            event.deferReply().queue();
            StringSelectMenu.Builder builder = StringSelectMenu.create("courses").setPlaceholder("Select a course to view more.");
            try {
                session = new JupiterSession(osis, pw);
                builder.setMaxValues(1);
                EmbedBuilder embed = formatEmbed("Successfully updated data for " + session.getName(), "", Color.cyan);
                embed.addField("⠀\n\uD83D\uDCDC  Enrolled Courses  \uD83D\uDCDC", "", false);
                for(Course course : session.getCourses()){
                    if(course.getMissing() != 0) missingText = course.getMissing() + " assignments missing.";
                    embed.addField("__" + course.getName() + "__:  " + course.getGradeAverage() + "%", missingText, false);
                    builder.addOption(course.getName(), ""+counter.getAndIncrement());
                    missingText = "||All assignments complete!||";
                }
                counter.set(0);
                embed.addField(" ", " ", false);
                embed.addField("\uD83D\uDCCA  Assignment Statistics  \uD83D\uDCCA", " ", false);
                session.getAssignmentStats().forEach((key,value) -> {
                    embed.addField(key, String.valueOf(value), true);
                });
                long epoch = Instant.now().toEpochMilli()/1000;
                embed.addField("⠀\n⠀\n\uD83D\uDD52  Last Updated  \uD83D\uDD52", "⠀\n<t:" + epoch + ":R>\n⠀", false);
                event.getHook().sendMessageEmbeds(embed.build()).setActionRow(builder.build()).queue();
                authorId = event.getUser().getId();
                userList.updateUserData(authorId, session);
                userList.setLastUpdated(authorId, epoch);
            } catch (InterruptedException | NoSuchElementException e) {
                EmbedBuilder embed = formatEmbed("Failed to log in.", "Please check your credentials and try again.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        AtomicLong counter = new AtomicLong(0);

        if(event.getModalId().equals("taskAdd")) {
            String task = Objects.requireNonNull(event.getValue("task")).getAsString();
            event.deferReply().setEphemeral(true).queue();
            if(userList.addTask(authorId, task)){
                String tempTask = task;
                EmbedBuilder embed = formatEmbed("Task appended!", "", Color.green);
                embed.addField("Appended task with id \"" + (userList.getUserMap().get(authorId).getToDoList().size()-1) + "\":", tempTask, true);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        }

        if(event.getModalId().equals("register")){
            String osis = Objects.requireNonNull(event.getValue("osis")).getAsString();
            String pw = Objects.requireNonNull(event.getValue("pw")).getAsString();
            event.deferReply().queue();
            try {
                StringSelectMenu.Builder builder = StringSelectMenu.create("courses").setPlaceholder("Select a course to view more.");
                session = new JupiterSession(osis, pw);
                builder.setMaxValues(1);
                EmbedBuilder embed = formatEmbed("Successfully fetched data for " + session.getName(), "", Color.cyan);
                embed.addField("⠀\n\uD83D\uDCDC  Enrolled Courses  \uD83D\uDCDC", "", false);
                for(Course course : session.getCourses()){
                    if(course.getMissing() != 0) missingText = course.getMissing() + " assignments missing.";
                    embed.addField("__" + course.getName() + "__:  " + course.getGradeAverage() + "%", missingText, false);
                    builder.addOption(course.getName(), ""+counter.getAndIncrement());
                    missingText = "||All assignments complete!||";
                }
                counter.set(0);
                embed.addField(" ", " ", false);
                embed.addField("\uD83D\uDCCA  Assignment Statistics  \uD83D\uDCCA", " ", false);
                session.getAssignmentStats().forEach((key,value) -> {
                    embed.addField(key, String.valueOf(value), true);
                });
                long epoch = Instant.now().toEpochMilli()/1000;
                embed.addField("⠀\n⠀\n\uD83D\uDD52  Last Updated  \uD83D\uDD52", "⠀\n<t:" + epoch + ":R>\n⠀", false);
                event.getHook().sendMessageEmbeds(embed.build()).setActionRow(builder.build()).queue();
                authorId = event.getUser().getId();
                userList.createUser(authorId, osis, pw, session, epoch); //store var is gone btw
            } catch (InterruptedException | NoSuchElementException e) {
                EmbedBuilder embed = formatEmbed("Failed to log in.", "Please check your credentials and try again.", Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        JupiterData currentUserData = userList.getJupiterData(authorId);
        if (event.getComponentId().equals("courses")) {
            if(!event.getUser().getId().equals(authorId)){
                event.getHook().sendMessage("you not permitted :(").setEphemeral(true).queue();
            } else {
                int selected = Integer.parseInt(event.getValues().get(0)); // the values the user selected
                selectedCourse = currentUserData.getCourses().get(selected);
                String courseName = selectedCourse.getName();
                EmbedBuilder embed = formatEmbed("Displaying: " + courseName + " (1-10)", "**Graded:** " + selectedCourse.getGraded() +
                        "\n**Missing:** " + selectedCourse.getMissing() +
                        "\n**Ungraded:** " + selectedCourse.getUngraded(), Color.CYAN);
                int iterations = 0;
                for(int i = 0; i < selectedCourse.getAssignments().size();  i++){
                    AssignmentData assignment = selectedCourse.getAssignments().get(i);
                    if(iterations >= 10) break;
                    int length = assignment.getName().length() + String.valueOf(assignment.getScore()).length() + 3;
                    if(length > 35) {
                        length = 35;
                    }
                    iterations++;
                    String dashes = String.join("", Collections.nCopies(length, "="));
                    embed.addField(dashes + "\n" + assignment.getName() + ":  " + assignment.getScore() + "%\n" + dashes, "Status: " + assignment.getStatus() + "\nImpact: " + assignment.getImpact() + "\nCategory: " + assignment.getCategory(), false);
                }
                event.replyEmbeds(embed.build()).setActionRow(prev, next).setEphemeral(true).queue();
                min = 0;
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if(event.getButton().getId().equals("forward")){
            boolean reset = false;
            min += 10;
            if(min >= selectedCourse.getAssignments().size()){
                min = 0;
            }
            EmbedBuilder embed = formatEmbed("placeholder", "**Graded:** " + selectedCourse.getGraded() +
                    "\n**Missing:** " + selectedCourse.getMissing() +
                    "\n**Ungraded:** " + selectedCourse.getUngraded(), Color.CYAN);
            for(int i = min; i < min + add; i++){
                if(i >= selectedCourse.getAssignments().size()){
                    reset = true;
                    add = selectedCourse.getAssignments().size()%10;
                    break;
                }
                AssignmentData assignment = selectedCourse.getAssignments().get(i);
                int length = assignment.getName().length() + String.valueOf(assignment.getScore()).length() + 3;
                if(length > 35) {
                    length = 35;
                }
                String dashes = String.join("", Collections.nCopies(length, "="));
                embed.addField(dashes + "\n" + assignment.getName() + ":  " + assignment.getScore() + "%\n" + dashes, "Status: " + assignment.getStatus() + "\nImpact: " + assignment.getImpact() + "\nCategory: " + assignment.getCategory(), false);
            }
            embed.setTitle("Displaying: " + selectedCourse.getName() + " (" + (min + 1) + " - " + (min+add) + ")");
            if(reset){
                min = -10;
                add = 10;
            }
            event.editMessageEmbeds(embed.build()).setActionRow(prev, next).queue();
        }

        if(event.getButton().getId().equals("previous")){
            boolean reset = false;
            if(min == -10)
                min += selectedCourse.getAssignments().size() - selectedCourse.getAssignments().size()%10;
            else{
                min-=10;
            }
            if(min < 0){
                min = selectedCourse.getAssignments().size() - selectedCourse.getAssignments().size()%10;
            }
            EmbedBuilder embed = formatEmbed("placeholder", "**Graded:** " + selectedCourse.getGraded() +
                    "\n**Missing:** " + selectedCourse.getMissing() +
                    "\n**Ungraded:** " + selectedCourse.getUngraded(), Color.CYAN);
            int iterations = 0;
            for(int i = min; i < min + add; i++){
                if(i < 0){
                    reset = true;
                    add = selectedCourse.getAssignments().size()%10;
                    break;
                } else if(i >= selectedCourse.getAssignments().size()){
                    break;
                }
                iterations++;
                AssignmentData assignment = selectedCourse.getAssignments().get(i);
                int length = assignment.getName().length() + String.valueOf(assignment.getScore()).length() + 3;
                if(length > 35) {
                    length = 35;
                }
                String dashes = String.join("", Collections.nCopies(length, "="));
                embed.addField(dashes + "\n" + assignment.getName() + ":  " + assignment.getScore() + "%\n" + dashes, "Status: " + assignment.getStatus() + "\nImpact: " + assignment.getImpact() + "\nCategory: " + assignment.getCategory(), false);
            }
            embed.setTitle("Displaying: " + selectedCourse.getName() + " (" + (min + 1) + " - " + (min+iterations) + ")");
            if(reset){
                min = selectedCourse.getAssignments().size() - 1;
                add = 10;
            }
            iterations = 0;
            event.editMessageEmbeds(embed.build()).setActionRow(prev, next).queue();
        }

    }
}
