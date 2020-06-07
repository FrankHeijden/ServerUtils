package net.frankheijden.serverutils.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    public Object get(String path) {
        return get(this, path);
    }

    private Object get(Defaults defaults, String path) {
        String[] split = path.split("\\.");
        if (split.length > 1) {
            return get((Defaults) defaults.rootMap.get(split[0]), path.substring(split[0].length() + 1));
        }
        return defaults.rootMap.get(split[0]);
    }

    public static YamlConfiguration init(File file, Defaults defaults) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Defaults.addDefaults(defaults, yml);

        try {
            // Idk somehow the order messes up
            // of the messages if we don't do this
            file.delete();
            file.createNewFile();

            yml.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return yml;
    }
}
