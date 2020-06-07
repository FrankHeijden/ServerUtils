package net.frankheijden.serverutils.utils;

import java.util.Collection;

public class ListBuilder<T> {

    private final Collection<T> collection;
    private ListFormat<T> formatter;
    private String seperator;
    private String lastSeperator;

    private ListBuilder(Collection<T> collection) {
        this.collection = collection;
    }

    public static <T> ListBuilder<T> create(Collection<T> collection) {
        return new ListBuilder<>(collection);
    }

    @SuppressWarnings("unchecked")
    public static ListBuilder<String> createStrings(Collection<? extends String> collection) {
        ListBuilder<String> builder = create((Collection<String>) collection);
        builder.format(ListFormat.stringFormat);
        return builder;
    }

    public ListBuilder<T> format(ListFormat<T> formatter) {
        this.formatter = formatter;
        return this;
    }

    public ListBuilder<T> seperator(String seperator) {
        this.seperator = seperator;
        return this;
    }

    public ListBuilder<T> lastSeperator(String lastSeperator) {
        this.lastSeperator = lastSeperator;
        return this;
    }

    @Override
    public String toString() {
        if (collection.size() == 1) {
            return formatter.format(collection.iterator().next());
        } else {
            StringBuilder sb = new StringBuilder();

            int i = 1;
            for (T t : collection) {
                sb.append(formatter.format(t));
                if (i == collection.size() - 1) {
                    sb.append(lastSeperator);
                } else if (i != collection.size()) {
                    sb.append(seperator);
                }
                i++;
            }
            return sb.toString();
        }
    }
}
