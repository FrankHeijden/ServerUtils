package net.frankheijden.serverutils.bukkit.reflection;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Field;
import java.util.Map;

public class RDedicatedServerProperties {

    private static Class<?> serverPropertiesClass;
    private static Map<String, Field> fields;

    static {
        try {
            serverPropertiesClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerProperties",
                    BukkitReflection.NMS));
            fields = getAllFields(serverPropertiesClass,
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
