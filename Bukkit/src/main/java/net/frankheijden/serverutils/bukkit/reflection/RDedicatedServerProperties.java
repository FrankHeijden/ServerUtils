package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;

import java.lang.reflect.Field;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RDedicatedServerProperties {

    private static Class<?> serverPropertiesClass;
    private static Map<String, Field> fields;

    static {
        try {
            serverPropertiesClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerProperties",
                    BukkitReflection.NMS));
            fields = getAllFields(serverPropertiesClass,
                    fieldOf("spawnAnimals"),
                    fieldOf("spawnNpcs"),
                    fieldOf("pvp"),
                    fieldOf("allowFlight"),
                    fieldOf("resourcePack"),
                    fieldOf("motd"),
                    fieldOf("forceGamemode"),
                    fieldOf("enforceWhitelist"),
                    fieldOf("gamemode"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Field> getFields() {
        return fields;
    }
}
