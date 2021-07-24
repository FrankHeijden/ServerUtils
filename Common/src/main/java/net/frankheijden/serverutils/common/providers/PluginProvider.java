package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.ServerUtilsApp;

public interface PluginProvider<P> {

    default File getPluginsFolder() {
        return ServerUtilsApp.getPlugin().getDataFolder().getParentFile();
    }

    List<P> getPlugins();

    String getPluginName(P plugin);

    File getPluginFile(P plugin);

    File getPluginFile(String pluginName);

    P getPlugin(String pluginName);

    Set<String> getCommands();

    /**
     * Retrieves a list of plugins, sorted by name.
     * @return The list of plugins.
     */
    default List<P> getPluginsSorted() {
        List<P> plugins = getPlugins();
        plugins.sort(Comparator.comparing(this::getPluginName));
        return plugins;
    }

    /**
     * Retrieves a list of plugin names.
     * @return The plugin names.
     */
    default List<String> getPluginNames() {
        return getPlugins().stream()
                .map(this::getPluginName)
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
