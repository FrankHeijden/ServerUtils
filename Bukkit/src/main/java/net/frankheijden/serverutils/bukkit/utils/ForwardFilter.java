package net.frankheijden.serverutils.bukkit.utils;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.frankheijden.serverutils.common.utils.PredicateFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ForwardFilter extends PredicateFilter {

    private boolean warnings;

    /**
     * Creates a filter which forwards all output to the sender.
     * @param sender The sender to forward logs to.
     */
    public ForwardFilter(CommandSender sender) {
        this.warnings = false;

        setPredicate(rec -> {
            ChatColor color = getColor(rec.getLevel());
            if (color != ChatColor.GREEN) warnings = true;
            sender.sendMessage(color + format(rec));
            return true;
        });
    }

    public boolean hasWarnings() {
        return warnings;
    }

    private static ChatColor getColor(Level level) {
        if (Level.SEVERE.equals(level)) {
            return ChatColor.RED;
        } else if (Level.WARNING.equals(level)) {
            return ChatColor.GOLD;
        }
        return ChatColor.GREEN;
    }

    private static String format(LogRecord record) {
        String msg = record.getMessage();

        Object[] params = record.getParameters();
        if (params == null) return msg;
        for (int i = 0; i < params.length; i++) {
            msg = msg.replace("{" + i + "}", String.valueOf(params[i]));
        }
        return msg;
    }
}
