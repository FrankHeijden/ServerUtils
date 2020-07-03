package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.frankheijden.serverutils.common.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RRegistrySimple {

    private static Class<?> registrySimpleClass;
    private static Map<String, Field> fields;

    static {
        try {
            registrySimpleClass = Class.forName(String.format("net.minecraft.server.%s.RegistrySimple",
                    BukkitReflection.NMS));
            fields = getAllFields(registrySimpleClass,
                    fieldOf("c", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes all registered MinecraftKey's from an instance associated to the specified plugin.
     * @param instance The RegistrySimple instance.
     * @param plugin The plugin to remove keys for.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    @SuppressWarnings("rawtypes")
    public static void removeKeyFor(Object instance, Plugin plugin) throws IllegalAccessException {
        Map map = (Map) get(fields, instance, "c");
        if (map == null) throw new RuntimeException("Map object was null!");

        AtomicBoolean errorThrown = new AtomicBoolean(false);
        MapUtils.removeKeys(map, RMinecraftKey.matchingPluginPredicate(errorThrown, plugin));
    }
}
