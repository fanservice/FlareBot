package stream.flarebot.flarebot.commands.music;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.player.Track;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.FlareBotManager;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.music.extractors.YouTubeExtractor;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.util.ButtonRunnable;
import stream.flarebot.flarebot.util.ButtonUtil;
import stream.flarebot.flarebot.util.GeneralUtils;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.objects.ButtonGroup;

public class SongCommand implements Command {

    private PlayerManager manager;

    public SongCommand(FlareBot bot) {
        this.manager = bot.getMusicManager();
    }

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (manager.getPlayer(channel.getGuild().getId()).getPlayingTrack() != null) {
            Track track = manager.getPlayer(channel.getGuild().getId()).getPlayingTrack();
            EmbedBuilder eb = MessageUtils.getEmbed(sender)
                    .addField("Current Song", getLink(track), false)
                    .setThumbnail("https://img.youtube.com/vi/" + track.getTrack().getIdentifier() + "/0.jpg");
            if (track.getTrack().getInfo().isStream)
                eb.addField("Amount Played", "Issa livestream ;)", false);
            else
                eb.addField("Amount Played", GeneralUtils.getProgressBar(track), true)
                        .addField("Time", String.format("%s / %s", GeneralUtils.formatDuration(track.getTrack().getPosition()),
                                GeneralUtils.formatDuration(track.getTrack().getDuration())), false);
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.addButton("\u23EF", new ButtonRunnable() {
                @Override
                public void run(User user) {
                    if (manager.hasPlayer(guild.getGuildId())) {
                        if (manager.getPlayer(guild.getGuild().getId()).getPaused()) {
                            if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), "flarebot.resume")) {
                                manager.getPlayer(guild.getGuild().getId()).play();
                            }
                        } else {
                            if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), "flarebot.pause")) {
                                manager.getPlayer(guild.getGuild().getId()).setPaused(true);
                            }
                        }
                    }
                }
            });
            buttonGroup.addButton("\u23F9", new ButtonRunnable() {
                @Override
                public void run(User user) {
                    if (manager.hasPlayer(guild.getGuildId())) {
                        if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), "flarebot.stop")) {
                            manager.getPlayer(guild.getGuildId()).stop();
                        }
                    }
                }
            });
            buttonGroup.addButton("\u23ED", new ButtonRunnable() {
                @Override
                public void run(User user) {
                    if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), "flarebot.skip")) {
                        Command cmd = FlareBot.getInstance().getCommand("skip", user);
                        cmd.onCommand(user, guild, channel, null, new String[0], guild.getGuild().getMember(user));
                    }
                }
            });
            ButtonUtil.sendButtonedMessage(channel, eb.build(), buttonGroup);
        } else {
            channel.sendMessage(MessageUtils.getEmbed(sender)
                    .addField("Current song", "**No song playing right now!**", false)
                    .build()).queue();
        }
    }

    public static String getLink(Track track) {
        String name = String.valueOf(track.getTrack().getInfo().title);
        String link = YouTubeExtractor.WATCH_URL + track.getTrack().getIdentifier();
        return String.format("[`%s`](%s)", name, link);
    }

    @Override
    public String getCommand() {
        return "song";
    }

    @Override
    public String getDescription() {
        return "Get the current song playing.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"playing"};
    }

    @Override
    public String getUsage() {
        return "`{%}song` - Displays info about the currently playing song.";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

}
