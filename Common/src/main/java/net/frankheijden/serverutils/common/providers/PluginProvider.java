package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;

public interface PluginProvider<P, D extends ServerUtilsPluginDescription> {

    default File getPluginsFolder() {
        return ServerUtilsApp.getPlugin().getDataFolder().getParentFile();
    }

    List<P> getPlugins();

    default String getPluginId(P plugin) {
        return getLoadedPluginDescription(plugin).getId();
    }

    default File getPluginFile(P plugin) {
        return getLoadedPluginDescription(plugin).getFile();
    }

    /**
     * Attempts to find the file for a given plugin id.
     */
    default Optional<File> getPluginFile(String pluginId) {
        for (File file : getPluginJars()) {
            Optional<D> pluginDescriptionOptional;
            try {
                pluginDescriptionOptional = getPluginDescription(file);
            } catch (InvalidPluginDescriptionException ex) {
                continue;
            }

            if (pluginDescriptionOptional.isPresent() && pluginDescriptionOptional.get().getId().equals(pluginId)) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves plugins which depend on the given plugin.
     */
    default List<P> getPluginsDependingOn(String pluginId) {
        List<P> plugins = new ArrayList<>();

        for (P loadedPlugin : getPlugins()) {
            ServerUtilsPluginDescription description = getLoadedPluginDescription(loadedPlugin);
            if (description.getDependencies().contains(pluginId)) {
                plugins.add(loadedPlugin);
            }
        }

        return plugins;
    }

    Optional<P> getPlugin(String pluginId);

    D getLoadedPluginDescription(P plugin);

    default Optional<D> getPluginDescription(String pluginId) throws InvalidPluginDescriptionException {
        Optional<File> fileOptional = getPluginFile(pluginId);
        return fileOptional.flatMap(this::getPluginDescription);
    }

    Optional<D> getPluginDescription(File file) throws InvalidPluginDescriptionException;

    Object getInstance(P plugin);

    Set<String> getCommands();

    /**
     * Retrieves a list of plugins, sorted by name.
     * @return The list of plugins.
     */
    default List<P> getPluginsSorted() {
        List<P> plugins = getPlugins();
        plugins.sort(Comparator.comparing(this::getPluginId));
        return plugins;
    }

    /**
     * Retrieves a list of plugin names.
     * @return The plugin names.
     */
    default List<String> getPluginNames() {
        return getPlugins().stream()
                .map(this::getPluginId)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all files with a jar extension in the plugins/ folder and returns solely their name.
     * @return An list of jar file names.
     */
    default List<String> getPluginFileNames() {
        return Arrays.stream(getPluginJars())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all files with a jar extension in the plugins/ folder.
     * @return An array of jar files.
     */
    default File[] getPluginJars() {
        File parent = getPluginsFolder();
        if (parent == null || !parent.exists()) return new File[0];
        return parent.listFiles(f -> f.getName().endsWith(".jar"));
    }
}
