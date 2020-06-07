package net.frankheijden.serverutils.reflection;

import net.frankheijden.serverutils.utils.MapUtils;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

public class RRegistrySimple {

    private static Class<?> registrySimpleClass;
    private static Map<String, Field> fields;

    static {
        try {
            registrySimpleClass = Class.forName(String.format("net.minecraft.server.%s.RegistrySimple", ReflectionUtils.NMS));
            fields = getAllFields(registrySimpleClass,
                    fieldOf("c", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void removeKeyFor(Object instance, Plugin plugin) throws IllegalAccessException {
        Map map = (Map) get(fields, instance, "c");
        if (map == null) throw new RuntimeException("Map object was null!");

        AtomicBoolean errorThrown = new AtomicBoolean(false);
        MapUtils.removeKeys(map, RMinecraftKey.matchingPluginPredicate(errorThrown, plugin));
    }
}
