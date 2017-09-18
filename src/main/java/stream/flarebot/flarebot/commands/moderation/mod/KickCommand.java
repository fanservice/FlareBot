package stream.flarebot.flarebot.commands.moderation.mod;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.mod.Punishment;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.util.GeneralUtils;
import stream.flarebot.flarebot.util.MessageUtils;

import java.awt.Color;
import java.util.EnumSet;

public class KickCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length >= 1) {
            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.KICK_MEMBERS)) {
                User user = GeneralUtils.getUser(args[0]);
                if (user == null) {
                    channel.sendMessage(new EmbedBuilder()
                            .setDescription("We cannot find that user! Try their ID if you didn't already.")
                            .setColor(Color.red).build()).queue();
                    return;
                }
                String reason = null;
                if (args.length >= 2)
                    reason = MessageUtils.getMessage(args, 1);
                guild.getAutoModConfig().postToModLog(user, sender, new Punishment(Punishment.EPunishment.KICK), reason);
                try {
                    channel.getGuild().getController().kick(channel.getGuild().getMember(user), reason).queue();
                    channel.sendMessage(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setDescription(user.getName() + " has been kicked from the server!")
                            .build()).queue();
                } catch (PermissionException e) {
                    MessageUtils.sendErrorMessage(String.format("Cannot kick player **%s#%s**! I do not have permission!", user.getName(), user.getDiscriminator()), channel);
                }
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setDescription("We can't kick users! Make sure we have the `Kick Members` permission!")
                        .setColor(Color.red).build()).queue();
            }
        } else {
            MessageUtils.getUsage(this, channel, sender).queue();
        }
    }

    @Override
    public String getCommand() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kicks a user";
    }

    @Override
    public String getUsage() {
        return "`{%}kick <user> [reason]` - Kicks a user with an optional reason";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public EnumSet<Permission> getDiscordPermission() {
        return EnumSet.of(Permission.KICK_MEMBERS);
    }
}