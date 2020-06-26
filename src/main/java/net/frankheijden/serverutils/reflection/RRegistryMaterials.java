package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.frankheijden.serverutils.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RRegistryMaterials {

    private static Class<?> registryMaterialsClass;

    private static Map<String, Field> fields;

    static {
        try {
            registryMaterialsClass = Class.forName(String.format("net.minecraft.server.%s.RegistryMaterials",
                    ReflectionUtils.NMS));
            fields = getAllFields(registryMaterialsClass,
                    fieldOf("b", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes all registered keys from an instance associated to the specified plugin.
     * @param instance The RegistryMaterials instance.
     * @param plugin The plugin to remove keys for.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    @SuppressWarnings("rawtypes")
    public static void removeKeysFor(Object instance, Plugin plugin) throws IllegalAccessException {
        Map map = (Map) get(fields, instance, "b");
        if (map == null) throw new RuntimeException("Map object was null!");

        AtomicBoolean errorThrown = new AtomicBoolean(false);
        MapUtils.removeValues(map, RMinecraftKey.matchingPluginPredicate(errorThrown, plugin));
        RRegistrySimple.removeKeyFor(instance, plugin);
    }
}
