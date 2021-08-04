package net.frankheijden.serverutils.common.config;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.frankheijden.serverutils.common.providers.ResourceProvider;

/**
 * A wrap for a Configuration file.
 */
public interface ServerUtilsConfig {

    /**
     * Retrieves the value at a given path.
     * @param path The path.
     * @return The object.
     */
    Object get(String path);

    /**
     * Retrieves a list of strings at a given path.
     * @param path The path.
     * @return The string list.
     */
    List<String> getStringList(String path);

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
    default void set(String path, Object value) {
        if (value == null) {
            remove(path);
        } else {
            String pathSegment = path;

            int lastDotIndex;
            while ((lastDotIndex = pathSegment.lastIndexOf('.')) != -1) {
                String parentPath = path.substring(0, lastDotIndex);
                if (!(get(parentPath) instanceof ServerUtilsConfig)) {
                    remove(parentPath);
                }
                pathSegment = parentPath;
            }

            setUnsafe(path, value);
        }
    }

    void setUnsafe(String path, Object value);

    /**
     * Removes a path.
     */
    void remove(String path);

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
     * Retrieves an integer from a path.
     * @param path The path.
     * @return The integer at given path.
     */
    int getInt(String path);

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
    static void addDefaults(ServerUtilsConfig def, ServerUtilsConfig conf) {
        addDefaults(def, conf, "");
    }

    /**
     * Adds defaults if keys don't exist to the configuration specified.
     * @param def The defaults to copy values over from.
     * @param conf The configuration to copy the defaults to.
     * @param root The current root path of the iteration.
     */
    static void addDefaults(ServerUtilsConfig def, ServerUtilsConfig conf, String root) {
        if (def == null) return;
        for (String key : def.getKeys()) {
            String newKey = (root.isEmpty() ? "" : root + ".") + key;
            Object value = def.get(key);
            if (value instanceof ServerUtilsConfig) {
                addDefaults((ServerUtilsConfig) value, conf, newKey);
            } else if (conf.get(newKey) == null) {
                if (value instanceof JsonElement) {
                    value = JsonConfig.toObjectValue((JsonElement) value);
                }
                conf.set(newKey, value);
            }
        }
    }

    /**
     * Removes unused keys from the configuration.
     */
    static void removeOldKeys(ServerUtilsConfig def, ServerUtilsConfig conf) {
        removeOldKeys(def, conf, "");
    }

    /**
     * Removes unused keys from the configuration, starting from the root node.
     */
    static void removeOldKeys(ServerUtilsConfig def, ServerUtilsConfig conf, String root) {
        if (def == null) return;
        for (String key : conf.getKeys()) {
            String defKey = (root.isEmpty() ? "" : root + ".") + key;
            Object value = conf.get(key);
            if (def.get(defKey) == null) {
                conf.remove(key);
            } else if (value instanceof ServerUtilsConfig) {
                removeOldKeys(def, (ServerUtilsConfig) value, defKey);
            }
        }
    }

    /**
     * Initiates a Configuration from a file with associated defaults.
     * @param def The default Configuration to be applied.
     * @param conf The Configuration where the defaults will be applied to.
     * @return The loaded Configuration of the file with defaults.
     */
    static ServerUtilsConfig init(ServerUtilsConfig def, ServerUtilsConfig conf) {
        ServerUtilsConfig.addDefaults(def, conf);
        ServerUtilsConfig.removeOldKeys(def, conf);

        try {
            conf.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return conf;
    }

    /**
     * Loads a resource from the jar file and writes it to the given path, applying defaults if needed.
     */
    static ServerUtilsConfig init(
            ServerUtilsConfig def,
            ResourceProvider provider,
            Path path
    ) {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return init(def, provider.load(path.toFile()));
    }
}
