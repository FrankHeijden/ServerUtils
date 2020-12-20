package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.frankheijden.serverutils.common.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RRegistrySimple {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.RegistrySimple");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Removes all registered MinecraftKey's from an instance associated to the specified plugin.
     * @param instance The RegistrySimple instance.
     * @param plugin The plugin to remove keys for.
     */
    @SuppressWarnings("rawtypes")
    public static void removeKeyFor(Object instance, Plugin plugin) {
        Map map = reflection.get(instance, "c");
        if (map == null) throw new RuntimeException("Map object was null!");

        AtomicBoolean errorThrown = new AtomicBoolean(false);
        MapUtils.removeKeys(map, RMinecraftKey.matchingPluginPredicate(errorThrown, plugin));
    }
}
