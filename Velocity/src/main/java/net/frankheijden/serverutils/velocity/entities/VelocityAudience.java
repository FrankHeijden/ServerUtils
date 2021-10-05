package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class VelocityAudience extends ServerUtilsAudience<CommandSource> {

    private static final GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    private static Object deserializer;
    private static MethodHandle deserializeMethodHandle;
    private static MethodHandle sendMessageMethodHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            @SuppressWarnings("LineLength")
            Class<?> deserializerClass = Class.forName("net.frankheijden.serverutils.dependencies.impl.adventure.text.serializer.gson.GsonComponentSerializer");
            deserializer = deserializerClass.getDeclaredMethod("gson").invoke(null);
            deserializeMethodHandle = lookup.unreflect(deserializerClass.getMethod("deserialize", Object.class));

            Class<?> componentClass = Class.forName(
                    new String(new char[]{'n', 'e', 't'}) + ".kyori.adventure.text.Component" // relocate is smart
            );
            sendMessageMethodHandle = lookup.unreflect(CommandSource.class.getMethod("sendMessage", componentClass));
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    protected VelocityAudience(Audience audience, CommandSource source) {
        super(audience, source);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public void sendMessage(Component component) {
        // Shading in adventure is fun when making a single distributable jar...
        String serializedString = serializer.serialize(component);
        try {
            sendMessageMethodHandle.invoke(source, deserializeMethodHandle.invoke(deserializer, serializedString));
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
