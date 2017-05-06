package stream.flarebot.flarebot.commands.administrator;

import net.dv8tion.jda.core.entities.*;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;

public class RolesCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Server Roles**\n```\n");
        for (Role role : channel.getGuild().getRoles()) {
            sb.append(role.getName()).append(" (").append(role.getId()).append(")\n");
        }
        sb.append("```");

        channel.sendMessage(sb.toString()).queue();
    }

    @Override
    public String getCommand() {
        return "roles";
    }

    @Override
    public String getDescription() {
        return "Get roles on the server";
    }

    @Override
    public String getUsage() {
        return "`{%}roles` - Gets the roles for the current server";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public boolean isDefaultPermission() {
        return true;
    }
}
