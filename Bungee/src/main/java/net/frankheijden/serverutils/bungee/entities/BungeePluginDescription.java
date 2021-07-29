package net.frankheijden.serverutils.bungee.entities;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.md_5.bungee.api.plugin.PluginDescription;

public class BungeePluginDescription implements ServerUtilsPluginDescription {

    private final PluginDescription description;
    private final File file;
    private final Set<String> dependencies;

    /**
     * Constructs a new BungeePluginDescription.
     */
    public BungeePluginDescription(PluginDescription description) {
        this.description = description;
        this.file = description.getFile();
        this.dependencies = new HashSet<>(description.getDepends());
    }

    @Override
    public String getId() {
        return this.description.getName();
    }

    @Override
    public String getName() {
        return this.description.getName();
    }

    @Override
    public String getVersion() {
        return this.description.getVersion();
    }

    @Override
    public String getAuthor() {
        return this.description.getAuthor();
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public Set<String> getDependencies() {
        return this.dependencies;
    }

    public PluginDescription getDescription() {
        return description;
    }
}
