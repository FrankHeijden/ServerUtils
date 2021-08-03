package net.frankheijden.serverutils.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class ListComponentBuilder<T> {

    private final List<T> elements;
    private Format<T> format;
    private Component separator;
    private Component lastSeparator;

    private ListComponentBuilder() {
        this.elements = new ArrayList<>();
    }

    public static <T> ListComponentBuilder<T> create(Collection<? extends T> list) {
        return new ListComponentBuilder<T>().addAll(list);
    }

    @SafeVarargs
    public static <T> ListComponentBuilder<T> create(T... elements) {
        return new ListComponentBuilder<T>().addAll(Arrays.asList(elements));
    }

    public ListComponentBuilder<T> format(Format<T> format) {
        this.format = format;
        return this;
    }

    public ListComponentBuilder<T> separator(Component separator) {
        this.separator = separator;
        return this;
    }

    public ListComponentBuilder<T> lastSeparator(Component lastSeparator) {
        this.lastSeparator = lastSeparator;
        return this;
    }

    public ListComponentBuilder<T> addAll(Collection<? extends T> elements) {
        this.elements.addAll(elements);
        return this;
    }

    /**
     * Builds the ListComponent.
     */
    public Component build() {
        if (elements.size() == 1) {
            return format.format(elements.iterator().next());
        } else {
            TextComponent.Builder builder = Component.empty().toBuilder();

            int sizeMinusTwo = elements.size() - 2;
            for (int i = 0; i < elements.size(); i++) {
                builder.append(format.format(elements.get(i)));
                if (i == sizeMinusTwo && lastSeparator != null) {
                    builder.append(lastSeparator);
                } else if (i < sizeMinusTwo && separator != null) {
                    builder.append(separator);
                }
            }

            return builder.build();
        }
    }

    public interface Format<T> {

        Component format(T element);

    }
}
