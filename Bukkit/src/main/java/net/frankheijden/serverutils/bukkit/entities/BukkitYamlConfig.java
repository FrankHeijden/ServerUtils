package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class BukkitYamlConfig implements ServerUtilsConfig {

    private final MemorySection config;
    private File file = null;

    public BukkitYamlConfig(File file) {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
    }

    public BukkitYamlConfig(InputStream in) {
        this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
    }

    public BukkitYamlConfig(MemorySection section) {
        this.config = section;
    }

    @Override
    public Object get(String path) {
        Object obj = config.get(path);
        if (obj instanceof MemorySection) {
            return new BukkitYamlConfig((MemorySection) obj);
        }
        return obj;
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public Map<String, Object> getMap(String path) {
        Object obj = config.get(path);
        if (obj instanceof MemorySection) {
            return ((MemorySection) obj).getValues(false);
        }
        return new HashMap<>();
    }

    @Override
    public void setUnsafe(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public void remove(String path) {
        config.set(path, null);
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
    public int getInt(String path) {
        return config.getInt(path, -1);
    }

    @Override
    public Collection<? extends String> getKeys() {
        return config.getKeys(false);
    }

    @Override
    public void save() throws IOException {
        if (!(config instanceof YamlConfiguration)) throw new IllegalArgumentException("Not a YamlConfiguration!");
        YamlConfiguration yml = (YamlConfiguration) config;
        yml.save(file);
    }
}
