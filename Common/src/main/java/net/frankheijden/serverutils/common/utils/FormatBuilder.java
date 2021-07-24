package net.frankheijden.serverutils.common.utils;

import java.util.ArrayList;
import java.util.List;

public class FormatBuilder {

    private final String format;
    private final List<String[]> valueList;
    private String[] orderedKeys;
    private boolean skipEmpty;

    private FormatBuilder(String format) {
        this.format = format;
        this.valueList = new ArrayList<>();
        this.orderedKeys = new String[0];
        this.skipEmpty = true;
    }

    public static FormatBuilder create(String format) {
        return new FormatBuilder(format);
    }

    public FormatBuilder orderedKeys(String... orderedKeys) {
        this.orderedKeys = orderedKeys;
        return this;
    }

    public FormatBuilder add(String... values) {
        this.valueList.add(values);
        return this;
    }

    public FormatBuilder skipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
        return this;
    }

    /**
     * Builds the current FormatBuilder instance into a list of strings.
     */
    public List<String> build() {
        List<String> strings = new ArrayList<>();

        loop:
        for (String[] values : valueList) {
            String str = format;
            for (int i = 0; i < Math.min(values.length, orderedKeys.length); i++) {
                String value = values[i];
                if ((value == null || value.isEmpty()) && skipEmpty) continue loop;
                str = str.replace(orderedKeys[i], String.valueOf(value));
            }
            strings.add(str);
        }

        return strings;
    }

    @Override
    public String toString() {
        return build().toString();
    }
}
