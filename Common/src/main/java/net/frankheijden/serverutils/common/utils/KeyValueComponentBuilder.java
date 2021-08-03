package net.frankheijden.serverutils.common.utils;

import java.util.ArrayList;
import java.util.List;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;

public class KeyValueComponentBuilder {

    private final MessagesResource.Message format;
    private final List<Template[]> templatesList;
    private final String keyPlaceholder;
    private final String valuePlaceholder;

    private KeyValueComponentBuilder(
            MessagesResource.Message format,
            String keyPlaceholder,
            String valuePlaceholder
    ) {
        this.format = format;
        this.templatesList = new ArrayList<>();
        this.keyPlaceholder = keyPlaceholder;
        this.valuePlaceholder = valuePlaceholder;
    }

    /**
     * Constructs a new KeyValueComponentBuilder.
     */
    public static KeyValueComponentBuilder create(
            MessagesResource.Message format,
            String keyPlaceholder,
            String valuePlaceholder
    ) {
        return new KeyValueComponentBuilder(format, keyPlaceholder, valuePlaceholder);
    }

    /**
     * Adds an entry.
     */
    public KeyValueComponentBuilder add(String key, String value) {
        if (value != null) this.templatesList.add(new Template[]{
                Template.of(keyPlaceholder, key),
                Template.of(valuePlaceholder, value)
        });
        return this;
    }

    /**
     * Adds an entry.
     */
    public KeyValueComponentBuilder add(String key, Component value) {
        if (value != null) this.templatesList.add(new Template[]{
                Template.of(keyPlaceholder, key),
                Template.of(valuePlaceholder, value)
        });
        return this;
    }

    /**
     * Adds an entry.
     */
    public KeyValueComponentBuilder add(Component key, String value) {
        if (value != null) this.templatesList.add(new Template[]{
                Template.of(keyPlaceholder, key),
                Template.of(valuePlaceholder, value)
        });
        return this;
    }

    /**
     * Adds an entry.
     */
    public KeyValueComponentBuilder add(Component key, Component value) {
        if (value != null) this.templatesList.add(new Template[]{
                Template.of(keyPlaceholder, key),
                Template.of(valuePlaceholder, value)
        });
        return this;
    }

    /**
     * Builds the current ListMessageBuilder instance into a Component.
     */
    public Component build() {
        TextComponent.Builder builder = Component.text();

        for (Template[] templates : templatesList) {
            builder.append(format.toComponent(templates));
        }

        return builder.build();
    }
}
