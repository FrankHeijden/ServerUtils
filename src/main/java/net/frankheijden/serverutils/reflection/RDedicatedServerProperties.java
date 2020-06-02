package net.frankheijden.serverutils.reflection;

import java.lang.reflect.Field;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

public class RDedicatedServerProperties {

    private static Class<?> dedicatedServerPropertiesClass;
    private static Map<String, Field> fields;

    static {
        try {
            dedicatedServerPropertiesClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerProperties", ReflectionUtils.NMS));
            fields = getAllFields(dedicatedServerPropertiesClass,
                    fieldOf("spawnAnimals", ALL_VERSIONS),
                    fieldOf("spawnNpcs", ALL_VERSIONS),
                    fieldOf("pvp", ALL_VERSIONS),
                    fieldOf("allowFlight", ALL_VERSIONS),
                    fieldOf("resourcePack", ALL_VERSIONS),
                    fieldOf("motd", ALL_VERSIONS),
                    fieldOf("forceGamemode", ALL_VERSIONS),
                    fieldOf("enforceWhitelist", ALL_VERSIONS),
                    fieldOf("gamemode", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Field> getFields() {
        return fields;
    }
}
