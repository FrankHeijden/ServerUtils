package net.frankheijden.serverutils.common.utils;

import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PredicateFilter implements Filter {

    private Predicate<LogRecord> predicate;
    private Filter filter;

    public void setPredicate(Predicate<LogRecord> predicate) {
        this.predicate = predicate;
    }

    public void start(Logger logger) {
        this.filter = logger.getFilter();
        logger.setFilter(this);
    }

    public void stop(Logger logger) {
        logger.setFilter(filter);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return predicate.test(record);
    }
}
