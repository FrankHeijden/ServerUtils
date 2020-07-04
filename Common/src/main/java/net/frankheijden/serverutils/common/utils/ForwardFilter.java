package net.frankheijden.serverutils.common.utils;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;

public class ForwardFilter extends PredicateFilter {

    private static final char INFO_COLOR = 'a';
    private static final char WARNING_COLOR = '6';
    private static final char SEVERE_COLOR = 'c';

    private boolean warnings;

    /**
     * Creates a filter which forwards all output to the sender.
     * @param sender The sender to forward logs to.
     */
    public ForwardFilter(ServerCommandSender sender) {
        this.warnings = false;

        setPredicate(rec -> {
            char color = getColor(rec.getLevel());
            if (color != INFO_COLOR) warnings = true;
            sender.sendMessage('&' + color + format(rec));
            return true;
        });
    }

    public boolean hasWarnings() {
        return warnings;
    }

    private static char getColor(Level level) {
        if (Level.SEVERE.equals(level)) {
            return SEVERE_COLOR;
        } else if (Level.WARNING.equals(level)) {
            return WARNING_COLOR;
        }
        return INFO_COLOR;
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
