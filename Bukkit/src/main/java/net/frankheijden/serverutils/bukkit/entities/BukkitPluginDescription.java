package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitPluginDescription implements ServerUtilsPluginDescription {

    private final PluginDescriptionFile descriptionFile;
    private final File file;
    private final String author;
    private final Set<String> dependencies;

    /**
     * Constructs a new BukkitPluginDescription.
     */
    public BukkitPluginDescription(PluginDescriptionFile descriptionFile, File file) {
        this.descriptionFile = descriptionFile;
        this.file = file;
        this.author = String.join(", ", this.descriptionFile.getAuthors());
        this.dependencies = new HashSet<>(descriptionFile.getDepend());
    }

    @Override
    public String getId() {
        return this.descriptionFile.getName();
    }

    @Override
    public String getName() {
        return this.descriptionFile.getName();
    }

    @Override
    public String getVersion() {
        return this.descriptionFile.getVersion();
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public Set<String> getDependencies() {
        return this.dependencies;
    }

    public PluginDescriptionFile getDescriptionFile() {
        return descriptionFile;
    }
}
