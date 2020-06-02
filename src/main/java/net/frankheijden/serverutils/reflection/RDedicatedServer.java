package net.frankheijden.serverutils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.*;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.*;

public class RDedicatedServer {

    private static Class<?> dedicatedServerClass;
    private static Map<String, Field> fields;
    private static Map<String, Method> methods;

    static {
        try {
            dedicatedServerClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServer", ReflectionUtils.NMS));

            fields = getAllFields(dedicatedServerClass,
                    fieldOf("propertyManager", ALL_VERSIONS),
                    fieldOf("options", ALL_VERSIONS),
                    fieldOf("autosavePeriod", ALL_VERSIONS),
                    fieldOf("o", min(13)));
            methods = getAllMethods(dedicatedServerClass,
                    methodOf("setSpawnAnimals", ALL_VERSIONS, boolean.class),
                    methodOf("getSpawnAnimals", ALL_VERSIONS),
                    methodOf("setPVP", ALL_VERSIONS, boolean.class),
                    methodOf("getPVP", ALL_VERSIONS),
                    methodOf("setAllowFlight", ALL_VERSIONS, boolean.class),
                    methodOf("getAllowFlight", ALL_VERSIONS),
                    methodOf("setMotd", ALL_VERSIONS, String.class),
                    methodOf("getMotd", ALL_VERSIONS),
                    methodOf("setSpawnNPCs", ALL_VERSIONS, boolean.class),
                    methodOf("setAllowFlight", ALL_VERSIONS, boolean.class),
                    methodOf("setResourcePack", ALL_VERSIONS, String.class, String.class),
                    methodOf("setForceGamemode", ALL_VERSIONS, boolean.class),
                    methodOf("n", min(13), boolean.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Field> getFields() {
        return fields;
    }

    public static void reload(Object console) throws Exception {
        Object options = get(fields, console, "options");

        if (MINOR >= 13) {
            Object propertyManager = RDedicatedServerSettings.newInstance(options);
            set(fields, console, "propertyManager", propertyManager);
            Object config = invoke(RDedicatedServerSettings.getMethods(), propertyManager, "getProperties");
            invoke(methods, console, "setSpawnAnimals", get(RDedicatedServerProperties.getFields(), config, "spawnAnimals"));
            invoke(methods, console, "setSpawnNPCs", get(RDedicatedServerProperties.getFields(), config, "spawnNpcs"));
            invoke(methods, console, "setPVP", get(RDedicatedServerProperties.getFields(), config, "pvp"));
            invoke(methods, console, "setAllowFlight", get(RDedicatedServerProperties.getFields(), config, "allowFlight"));
            invoke(methods, console, "setResourcePack", get(RDedicatedServerProperties.getFields(), config, "resourcePack"), invoke(methods, console, "aZ"));
            invoke(methods, console, "setMotd", get(RDedicatedServerProperties.getFields(), config, "motd"));
            invoke(methods, console, "setForceGamemode", get(RDedicatedServerProperties.getFields(), config, "forceGamemode"));
            invoke(methods, console, "n", get(RDedicatedServerProperties.getFields(), config, "enforceWhitelist"));
            set(fields, console, "o", get(RDedicatedServerProperties.getFields(), config, "gamemode"));
        } else {
            Object config = RPropertyManager.newInstance(options);
            set(fields, console, "propertyManager", config);
            invoke(methods, console, "setSpawnAnimals", invoke(RPropertyManager.getMethods(), config, "getBoolean", "spawn-animals", invoke(methods, console, "getSpawnAnimals")));
            invoke(methods, console, "setPVP", invoke(RPropertyManager.getMethods(), config, "getBoolean", "pvp", invoke(methods, console, "getPVP")));
            invoke(methods, console, "setAllowFlight", invoke(RPropertyManager.getMethods(), config, "getBoolean", "allow-flight", invoke(methods, console, "getAllowFlight")));
            invoke(methods, console, "setMotd", invoke(RPropertyManager.getMethods(), config, "getString", "motd", invoke(methods, console, "getMotd")));
        }
    }
}
