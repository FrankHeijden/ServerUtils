package net.frankheijden.serverutils.common.utils;

import java.util.ArrayList;
import java.util.List;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.kyori.adventure.text.Component;
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

    public KeyValueComponentBuilder.KeyValuePair key(String key) {
        return new KeyValuePair(key);
    }

    public KeyValueComponentBuilder.KeyValuePair key(Component key) {
        return new KeyValuePair(key);
    }

    private KeyValueComponentBuilder add(Template key, Template value) {
        this.templatesList.add(new Template[]{ key, value });
        return this;
    }

    /**
     * Builds the current ListMessageBuilder instance into a Component.
     */
    public List<Component> build() {
        List<Component> components = new ArrayList<>(templatesList.size());

        for (Template[] templates : templatesList) {
            components.add(format.toComponent(templates));
        }

        return components;
    }

    public class KeyValuePair {

        private final Template key;

        private KeyValuePair(String key) {
            this.key = Template.of(keyPlaceholder, key);
        }

        private KeyValuePair(Component key) {
            this.key = Template.of(keyPlaceholder, key);
        }

        public KeyValueComponentBuilder value(String value) {
            if (value == null) return KeyValueComponentBuilder.this;
            return add(key, Template.of(valuePlaceholder, value));
        }

        public KeyValueComponentBuilder value(Component value) {
            if (value == null) return KeyValueComponentBuilder.this;
            return add(key, Template.of(valuePlaceholder, value));
        }
    }
}
