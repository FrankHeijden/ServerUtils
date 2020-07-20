package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PluginProvider<T> {

    public abstract File getPluginsFolder();

    public abstract List<T> getPlugins();

    public abstract String getPluginName(T plugin);

    public abstract File getPluginFile(T plugin);

    public abstract File getPluginFile(String pluginName);

    public abstract T getPlugin(String pluginName);

    public abstract Set<String> getCommands();

    /**
     * Retrieves a list of plugins, sorted by name.
     * @return The list of plugins.
     */
    public List<T> getPluginsSorted() {
        List<T> plugins = getPlugins();
        plugins.sort(Comparator.comparing(this::getPluginName));
        return plugins;
    }

    /**
     * Retrieves a list of plugin names.
     * @return The plugin names.
     */
    public List<String> getPluginNames() {
        return getPlugins().stream()
                .map(this::getPluginName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all files with a jar extension in the plugins/ folder and returns solely their name.
     * @return An list of jar file names.
     */
    public List<String> getPluginFileNames() {
        return Arrays.stream(getPluginJars())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all files with a jar extension in the plugins/ folder.
     * @return An array of jar files.
     */
    public File[] getPluginJars() {
        File parent = getPluginsFolder();
        if (parent == null || !parent.exists()) return new File[0];
        return parent.listFiles(f -> f.getName().endsWith(".jar"));
    }
}
