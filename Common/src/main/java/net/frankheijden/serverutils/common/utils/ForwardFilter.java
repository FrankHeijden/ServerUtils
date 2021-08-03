package net.frankheijden.serverutils.common.utils;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ForwardFilter extends PredicateFilter {

    private static final NamedTextColor INFO_COLOR = NamedTextColor.GREEN;
    private static final NamedTextColor WARNING_COLOR = NamedTextColor.GOLD;
    private static final NamedTextColor SEVERE_COLOR = NamedTextColor.RED;

    private boolean warnings;

    /**
     * Creates a filter which forwards all output to the sender.
     * @param sender The sender to forward logs to.
     */
    public ForwardFilter(ServerUtilsAudience<?> sender) {
        this.warnings = false;

        setPredicate(rec -> {
            NamedTextColor color = getColor(rec.getLevel());
            if (color != INFO_COLOR) warnings = true;
            sender.sendMessage(Component.text(format(rec), color));
            return true;
        });
    }

    public boolean hasWarnings() {
        return warnings;
    }

    private static NamedTextColor getColor(Level level) {
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
