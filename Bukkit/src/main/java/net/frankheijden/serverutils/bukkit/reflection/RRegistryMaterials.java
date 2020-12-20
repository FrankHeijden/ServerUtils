package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.frankheijden.serverutils.common.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RRegistryMaterials {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.RegistryMaterials");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Removes all registered keys from an instance associated to the specified plugin.
     * @param instance The RegistryMaterials instance.
     * @param plugin The plugin to remove keys for.
     */
    @SuppressWarnings("rawtypes")
    public static void removeKeysFor(Object instance, Plugin plugin) {
        Map map = reflection.get(instance, "b");
        if (map == null) throw new RuntimeException("Map object was null!");

        AtomicBoolean errorThrown = new AtomicBoolean(false);
        MapUtils.removeValues(map, RMinecraftKey.matchingPluginPredicate(errorThrown, plugin));
        RRegistrySimple.removeKeyFor(instance, plugin);
    }
}
