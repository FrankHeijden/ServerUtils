package net.frankheijden.serverutils.common.config;

import java.io.IOException;
import java.util.Collection;

public interface YamlConfig {

    Object get(String path);

    void set(String path, Object value);

    String getString(String path);

    boolean getBoolean(String path);

    Collection<? extends String> getKeys();

    void save() throws IOException;

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
