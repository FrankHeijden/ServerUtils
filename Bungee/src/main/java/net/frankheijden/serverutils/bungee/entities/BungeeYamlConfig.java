package net.frankheijden.serverutils.bungee.entities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.frankheijden.serverutils.common.config.YamlConfig;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeYamlConfig implements YamlConfig {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final Configuration config;
    private File file = null;

    public BungeeYamlConfig(File file) throws IOException {
        this.config = provider.load(file);
        this.file = file;
    }

    public BungeeYamlConfig(InputStream in) {
        this.config = provider.load(in);
    }

    public BungeeYamlConfig(Configuration config) {
        this.config = config;
    }

    @Override
    public Object get(String path) {
        Object obj = config.get(path);
        if (obj instanceof Configuration) {
            return new BungeeYamlConfig((Configuration) obj);
        }
        return obj;
    }

    @Override
    public Map<String, Object> getMap(String path) {
        Object obj = config.get(path);
        if (obj instanceof Configuration) {
            Configuration section = (Configuration) obj;
            Collection<String> keys = section.getKeys();
            Map<String, Object> map = new HashMap<>(keys.size());
            for (String key : keys) {
                map.put(key, section.get(key));
            }
            return map;
        }
        return new HashMap<>();
    }

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public Collection<? extends String> getKeys() {
        return config.getKeys();
    }

    @Override
    public void save() throws IOException {
        provider.save(config, file);
    }
}
