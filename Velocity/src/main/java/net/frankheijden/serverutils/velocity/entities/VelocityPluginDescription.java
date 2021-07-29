package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;

public class VelocityPluginDescription implements ServerUtilsPluginDescription {

    private final PluginDescription description;
    private final File file;
    private final String author;
    private final Set<String> dependencies;

    /**
     * Constructs a new BungeePluginDescription.
     */
    public VelocityPluginDescription(PluginDescription description) {
        this.description = description;

        Optional<Path> sourceOptional = description.getSource();
        if (!sourceOptional.isPresent()) throw new InvalidPluginDescriptionException("Source path is null");

        this.file = sourceOptional.get().toFile();
        this.author = String.join(", ", description.getAuthors());
        this.dependencies = description.getDependencies().stream()
                .map(PluginDependency::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public String getId() {
        return this.description.getId();
    }

    @Override
    public String getName() {
        return this.description.getName().orElse("<UNKNOWN>");
    }

    @Override
    public String getVersion() {
        return this.description.getVersion().orElse("<UNKNOWN>");
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

    public PluginDescription getDescription() {
        return description;
    }
}
