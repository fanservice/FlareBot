package stream.flarebot.flarebot.util;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.objects.Report;

import java.awt.Color;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GeneralUtils {

    private static final DecimalFormat percentageFormat = new DecimalFormat("#.##");

    public static String getShardId(JDA jda) {
        return jda.getShardInfo() == null ? "0" : String.valueOf(jda.getShardInfo().getShardId() + 1);
    }

    public static EmbedBuilder getReportEmbed(User sender, Report report) {
        EmbedBuilder eb = MessageUtils.getEmbed(sender);
        User reporter = FlareBot.getInstance().getUserByID(String.valueOf(report.getReporterId()));
        User reported = FlareBot.getInstance().getUserByID(String.valueOf(report.getReportedId()));

        eb.addField("Report ID", String.valueOf(report.getId()), true);
        eb.addField("Reporter", MessageUtils.getTag(reporter), true);
        eb.addField("Reported", MessageUtils.getTag(reported), true);

        eb.addField("Time", report.getTime().toLocalDateTime().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " GMT/BST", true);
        eb.addField("Status", report.getStatus().getMessage(), true);

        eb.addField("Message", "```" + report.getMessage() + "```", false);
        return eb;
    }

    public static String getPageOutOfTotal(int page, List<?> items, int pageLength) {
        return String.valueOf(page) + "/" + String.valueOf(items.size() < pageLength ? 1 : (items.size() / pageLength) + (items.size() % pageLength != 0 ? 1 : 0));
    }

    public static String formatDuration(long duration) {
        long totalSeconds = duration / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600);
        return (hours > 0 ? (hours < 10 ? "0" + hours : hours) + ":" : "")
                + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static String getProgressBar(Track track) {
        float percentage = (100f / track.getTrack().getDuration() * track.getTrack().getPosition());
        StringBuilder progress = new StringBuilder("[");
        progress.append(StringUtils.repeat("▬", (int) Math.round((double) percentage / 10)));
        progress.append("]()");
        progress.append(StringUtils.repeat("▬", 10 - (int) Math.round((double) percentage / 10)));
        progress.append(" ").append(GeneralUtils.percentageFormat.format(percentage)).append("%");
        return progress.toString();
    }

    private static char get(TextChannel channel) {
        if (channel.getGuild() != null) {
            return FlareBot.getPrefixes().get(channel.getGuild().getId());
        }
        return FlareBot.getPrefixes().get(null);
    }

    public static String formatCommandPrefix(TextChannel channel, String usage) {
        String prefix = String.valueOf(get(channel));
        return usage.replaceAll("\\{%\\}", prefix);
    }

    public static AudioItem resolveItem(Player player, String input) throws IllegalArgumentException, IllegalStateException {
        Optional<AudioItem> item = Optional.empty();
        boolean failed = false;
        int backoff = 2;
        for (int i = 0; i <= 2; i++) {
            try {
                item = Optional.ofNullable(player.resolve(input));
                failed = false;
                break;
            } catch (FriendlyException | InterruptedException | ExecutionException e) {
                failed = true;
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ignored) {}
                backoff ^= 2;
            }
        }
        if (failed) {
            throw new IllegalStateException();
        } else if (!item.isPresent()) {
            throw new IllegalArgumentException();
        }
        return item.get();
    }

    public static String colourFormat(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String truncate(int length, String string) {
        return truncate(length, string, true);
    }

    public static String truncate(int length, String string, boolean ellipse) {
        return string.substring(0, Math.min(string.length(), length)) + (string.length() > length ? "..." : "");
    }

}