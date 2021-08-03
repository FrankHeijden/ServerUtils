package net.frankheijden.serverutils.common.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

public class MessagesResource extends ServerUtilsResource {

    public static final String MESSAGES_RESOURCE = "messages";

    private final Map<ConfigKey, Message> messageMap;
    private final MiniMessage miniMessage;

    /**
     * Constructs a new MessagesResource.
     */
    public MessagesResource(ServerUtilsPlugin<?, ?, ?, ?, ?> plugin) {
        super(plugin, MESSAGES_RESOURCE);
        this.messageMap = new HashMap<>();
        this.miniMessage = MiniMessage.get();
    }

    public Message get(String path) {
        return get(MessageKey.fromPath(path));
    }

    public Message get(ConfigKey key) {
        return messageMap.get(key);
    }

    /**
     * Loads message keys and pre-compiles them if possible.
     */
    public void load(Collection<? extends PlaceholderConfigKey> keys) {
        for (PlaceholderConfigKey key : keys) {
            this.messageMap.put(key, new Message(key));
        }
    }

    public class Message {

        private final PlaceholderConfigKey key;
        private final String messageString;
        private final Component component;

        /**
         * Constructs a new Message.
         */
        public Message(PlaceholderConfigKey key) {
            this.key = key;
            this.messageString = getConfig().getString("messages." + key.getPath());
            this.component = key.hasPlaceholders() ? null : miniMessage.parse(messageString);
        }

        /**
         * Creates a {@link Component}.
         */
        public Component toComponent() {
            return this.component == null ? miniMessage.parse(messageString) : this.component;
        }

        /**
         * Creates a {@link Component}.
         */
        public Component toComponent(Template... templates) {
            return this.component == null ? miniMessage.parse(messageString, templates) : this.component;
        }

        /**
         * Creates a {@link Component}.
         */
        public Component toComponent(String... placeholders) {
            return this.component == null ? miniMessage.parse(messageString, placeholders) : this.component;
        }

        public void sendTo(ServerUtilsAudience<?> serverAudience, Template... placeholders) {
            serverAudience.sendMessage(toComponent(placeholders));
        }
    }

    @Override
    public void migrate(int currentConfigVersion) {

    }
}
