package net.frankheijden.serverutils.common.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * A wrap for a Yaml Configuration file.
 */
public interface YamlConfig {

    /**
     * Retrieves the value at a given path.
     * @param path The path.
     * @return The object.
     */
    Object get(String path);

    /**
     * Retrieves a map with key/values for the path specified.
     * @param path The path.
     * @return The map object with key/values.
     */
    Map<String, Object> getMap(String path);

    /**
     * Sets a value to a path.
     * @param path The path.
     * @param value The object to set the path's value to.
     */
    void set(String path, Object value);

    /**
     * Retrieves a string from a path.
     * @param path The path.
     * @return The string at given path.
     */
    String getString(String path);

    /**
     * Retrieves a boolean from a path.
     * @param path The path.
     * @return The boolean at given path.
     */
    boolean getBoolean(String path);

    /**
     * Retrieves the key nodes at the current level.
     * @return The keys.
     */
    Collection<? extends String> getKeys();

    /**
     * Saves the YamlConfig to disk.
     * @throws IOException Iff an I/O error occurred.
     */
    void save() throws IOException;

    /**
     * Adds defaults if keys don't exist to the configuration specified.
     * @param def The defaults to copy values over from.
     * @param conf The configuration to copy the defaults to.
     */
    static void addDefaults(YamlConfig def, YamlConfig conf) {
        addDefaults(def, conf, "");
    }

    /**
     * Adds defaults if keys don't exist to the configuration specified.
     * @param def The defaults to copy values over from.
     * @param conf The configuration to copy the defaults to.
     * @param root The current root path of the iteration.
     */
    static void addDefaults(YamlConfig def, YamlConfig conf, String root) {
        if (def == null) return;
        for (String key : def.getKeys()) {
            String newKey = (root.isEmpty() ? "" : root + ".") + key;
            Object value = def.get(key);
            if (value instanceof YamlConfig) {
                addDefaults((YamlConfig) value, conf, newKey);
            } else if (conf.get(newKey) == null) {
                conf.set(newKey, value);
            }
        }
    }

    /**
     * Initiates a Configuration from a file with associated defaults.
     * @param def The default Configuration to be applied.
     * @param conf The Configuration where the defaults will be applied to.
     * @return The loaded Configuration of the file with defaults.
     */
    static YamlConfig init(YamlConfig def, YamlConfig conf) {
        YamlConfig.addDefaults(def, conf);

        try {
            conf.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return conf;
    }
}
