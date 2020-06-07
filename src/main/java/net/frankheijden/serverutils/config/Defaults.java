package net.frankheijden.serverutils.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class Defaults {

    private final Map<String, Object> rootMap;

    private Defaults(Object... objects) {
        this.rootMap = new LinkedHashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            this.rootMap.put(String.valueOf(objects[i]), objects[i + 1]);
        }
    }

    public static Defaults of(Object... objects) {
        return new Defaults(objects);
    }

    public static void addDefaults(Defaults defaults, YamlConfiguration yml) {
        addDefaults(defaults, yml, "");
    }

    private static void addDefaults(Defaults defaults, YamlConfiguration yml, String root) {
        for (Map.Entry<String, Object> entry : defaults.rootMap.entrySet()) {
            String key = (root.isEmpty() ? "" : root + ".") + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Defaults) {
                addDefaults((Defaults) value, yml, key);
            } else if (yml.get(key) == null) {
                yml.set(key, value);
            }
        }
    }
}
