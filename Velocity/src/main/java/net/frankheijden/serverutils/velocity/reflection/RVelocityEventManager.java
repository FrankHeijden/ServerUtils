package net.frankheijden.serverutils.velocity.reflection;

import com.google.common.collect.Multimap;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.PluginContainer;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RVelocityEventManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.event.VelocityEventManager");

    private RVelocityEventManager() {}

    @SuppressWarnings("rawtypes")
    public static Multimap getHandlersByType(EventManager manager) {
        return reflection.get(manager, "handlersByType");
    }

    /**
     * Retrieves the registrations from a plugin for a specific event.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getRegistrationsByPlugin(EventManager manager, Object plugin, Class<?> eventClass) {
        return (List<Object>) getHandlersByType(manager).get(eventClass).stream()
                .filter(r -> RHandlerRegistration.getPlugin(r).getInstance().orElse(null) == plugin)
                .collect(Collectors.toList());
    }

    /**
     * Registers the listener for a given plugin.
     */
    public static void registerInternally(EventManager manager, PluginContainer container, Object listener) {
        reflection.invoke(
                manager,
                "registerInternally",
                ClassObject.of(PluginContainer.class, container),
                ClassObject.of(Object.class, listener)
        );
    }

    /**
     * Fires an event specifically for one plugin.
     */
    public static <E> CompletableFuture<E> fireForPlugin(
            EventManager manager,
            E event,
            Object plugin
    ) {
        List<Object> registrations = getRegistrationsByPlugin(manager, plugin, event.getClass());
        CompletableFuture<E> future = new CompletableFuture<>();

        Object registrationsEmptyArray = Array.newInstance(RHandlerRegistration.reflection.getClazz(), 0);
        Class<?> registrationsArrayClass = registrationsEmptyArray.getClass();

        reflection.invoke(
                manager,
                "fire",
                ClassObject.of(CompletableFuture.class, future),
                ClassObject.of(Object.class, event),
                ClassObject.of(int.class, 0),
                ClassObject.of(boolean.class, false),
                ClassObject.of(registrationsArrayClass, registrations.toArray((Object[]) registrationsEmptyArray))
        );

        return future;
    }

    public static class RHandlerRegistration {

        private static final MinecraftReflection reflection = MinecraftReflection
                .of("com.velocitypowered.proxy.event.VelocityEventManager$HandlerRegistration");

        private RHandlerRegistration() {}

        public static PluginContainer getPlugin(Object registration) {
            return reflection.get(registration, "plugin");
        }

        public static EventHandler<Object> getEventHandler(Object registration) {
            return reflection.get(registration, "handler");
        }
    }
}
