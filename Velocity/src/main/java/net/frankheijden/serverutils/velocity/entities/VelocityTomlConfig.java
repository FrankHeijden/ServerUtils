package net.frankheijden.serverutils.velocity.entities;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;

public class VelocityTomlConfig implements ServerUtilsConfig {

    private final CommentedConfig config;
    private final File file;

    /**
     * Creates a new VelocityTomlConfig instance.
     */
    public VelocityTomlConfig(File file) {
        CommentedFileConfig conf = CommentedFileConfig.of(file, TomlFormat.instance());
        conf.load();

        this.config = conf;
        this.file = file;
    }

    public VelocityTomlConfig(CommentedConfig config, File file) {
        this.config = config;
        this.file = file;
    }

    @Override
    public Object get(String path) {
        Object obj = config.get(path);
        if (obj instanceof CommentedConfig) {
            return new VelocityTomlConfig((CommentedConfig) obj, file);
        }
        return obj;
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getOrElse(path, new ArrayList<>());
    }

    @Override
    public Map<String, Object> getMap(String path) {
        CommentedConfig section = config.get(path);
        if (section == null) return new HashMap<>();
        return section.valueMap();
    }

    @Override
    public void set(String path, Object value) {
        if (value == null) {
            config.remove(path);
        } else {
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex != -1) {
                String parentPath = path.substring(0, lastDotIndex);
                try {
                    CommentedConfig parent = config.get(parentPath);
                } catch (ClassCastException ex) {
                    config.remove(parentPath);
                }
            }
            config.set(path, value);
        }
    }

    @Override
    public String getString(String path) {
        return config.get(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return config.get(path);
    }

    @Override
    public int getInt(String path) {
        return config.getOrElse(path, -1);
    }

    @Override
    public Collection<? extends String> getKeys() {
        return new HashSet<>(config.valueMap().keySet());
    }

    @Override
    public void save() throws IOException {
        if (config instanceof CommentedFileConfig) {
            ((CommentedFileConfig) config).save();
        } else {
            throw new IOException("Config is not an instance of CommentedFileConfig!");
        }
    }
}
