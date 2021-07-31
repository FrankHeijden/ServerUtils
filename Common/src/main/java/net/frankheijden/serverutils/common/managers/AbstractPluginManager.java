package net.frankheijden.serverutils.common.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResult;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.frankheijden.serverutils.common.utils.DependencyUtils;

public abstract class AbstractPluginManager<P, D extends ServerUtilsPluginDescription> implements PluginProvider<P, D> {

    /**
     * Loads the given plugin by their jar file.
     */
    public PluginResult<P> loadPlugin(String pluginFile) {
        File file = new File(getPluginsFolder(), pluginFile);
        if (!file.exists()) return new PluginResult<>(pluginFile, Result.NOT_EXISTS);
        return loadPlugin(file);
    }

    public PluginResult<P> loadPlugin(File file) {
        return loadPlugins(Collections.singletonList(file)).first();
    }

    /**
     * Loads a list of files as plugins.
     */
    public PluginResults<P> loadPlugins(List<File> files) {
        List<D> descriptions = new ArrayList<>(files.size());

        for (File file : files) {
            D description;
            try {
                Optional<D> descriptionOptional = getPluginDescription(file);
                if (!descriptionOptional.isPresent()) {
                    return new PluginResults<P>().addResult(file.getName(), Result.NOT_EXISTS);
                }

                description = descriptionOptional.get();
            } catch (InvalidPluginDescriptionException ex) {
                return new PluginResults<P>().addResult(file.getName(), Result.INVALID_DESCRIPTION);
            }

            if (getPlugin(description.getId()).isPresent()) {
                return new PluginResults<P>().addResult(description.getId(), Result.ALREADY_LOADED);
            }

            descriptions.add(description);
        }

        List<D> orderedDescriptions;
        try {
            orderedDescriptions = determineLoadOrder(descriptions);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();

            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(", ").append(file.getName());
            }

            return new PluginResults<P>().addResult(sb.substring(2), Result.ERROR);
        }

        return loadPluginDescriptions(orderedDescriptions);
    }

    protected abstract PluginResults<P> loadPluginDescriptions(List<D> descriptions);

    /**
     * Enables the given plugin by name.
     */
    public PluginResult<P> enablePlugin(String pluginId) {
        return getPlugin(pluginId)
                .map(this::enablePlugin)
                .orElse(new PluginResult<>(pluginId, Result.NOT_EXISTS));
    }

    public PluginResult<P> enablePlugin(P plugin) {
        return enablePlugins(Collections.singletonList(plugin)).first();
    }

    /**
     * Enables a list of plugins.
     */
    public PluginResults<P> enablePlugins(List<P> plugins) {
        for (P plugin : plugins) {
            String pluginId = getPluginId(plugin);
            if (isPluginEnabled(pluginId)) {
                return new PluginResults<P>().addResult(pluginId, Result.ALREADY_ENABLED);
            }
        }

        return enableOrderedPlugins(determineLoadOrder(plugins));
    }

    protected abstract PluginResults<P> enableOrderedPlugins(List<P> plugins);

    public boolean isPluginEnabled(P plugin) {
        return isPluginEnabled(getPluginId(plugin));
    }

    public abstract boolean isPluginEnabled(String pluginId);

    /**
     * Disables the given plugin by name.
     */
    public PluginResult<P> disablePlugin(String pluginId) {
        return getPlugin(pluginId)
                .map(this::disablePlugin)
                .orElse(new PluginResult<>(pluginId, Result.NOT_EXISTS));
    }

    public PluginResult<P> disablePlugin(P plugin) {
        return disablePlugins(Collections.singletonList(plugin)).first();
    }

    /**
     * Disables a list of plugins.
     */
    public PluginResults<P> disablePlugins(List<P> plugins) {
        for (P plugin : plugins) {
            if (!isPluginEnabled(plugin)) {
                return new PluginResults<P>().addResult(getPluginId(plugin), Result.ALREADY_DISABLED);
            }
        }

        List<P> orderedPlugins;
        try {
            orderedPlugins = determineLoadOrder(plugins);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();

            StringBuilder sb = new StringBuilder();
            for (P plugin : plugins) {
                sb.append(", ").append(getPluginId(plugin));
            }

            return new PluginResults<P>().addResult(sb.substring(2), Result.ERROR);
        }

        Collections.reverse(orderedPlugins);
        return disableOrderedPlugins(orderedPlugins);
    }

    public abstract PluginResults<P> disableOrderedPlugins(List<P> plugins);

    /**
     * Reloads the given plugin by name.
     */
    public PluginResult<P> reloadPlugin(String pluginId) {
        return getPlugin(pluginId)
                .map(this::reloadPlugin)
                .orElse(new PluginResult<>(pluginId, Result.NOT_EXISTS));
    }

    public PluginResult<P> reloadPlugin(P plugin) {
        return reloadPlugins(Collections.singletonList(plugin)).first();
    }

    /**
     * Reloads the given plugins.
     */
    public PluginResults<P> reloadPlugins(List<P> plugins) {
        PluginResults<P> disableResults = disablePlugins(plugins);
        for (PluginResult<P> disableResult : disableResults.getResults()) {
            if (!disableResult.isSuccess() && disableResult.getResult() != Result.ALREADY_DISABLED) {
                return disableResults;
            }
        }

        List<String> pluginIds = new ArrayList<>(plugins.size());
        for (P plugin : plugins) {
            pluginIds.add(getPluginId(plugin));
        }

        CloseablePluginResults<P> unloadResults = unloadPlugins(plugins);
        if (!unloadResults.isSuccess()) return unloadResults;
        unloadResults.tryClose();

        List<File> pluginFiles = new ArrayList<>(plugins.size());
        for (String pluginId : pluginIds) {
            Optional<File> pluginFile = getPluginFile(pluginId);
            if (!pluginFile.isPresent()) return new PluginResults<P>().addResult(pluginId, Result.FILE_DELETED);
            pluginFiles.add(pluginFile.get());
        }

        PluginResults<P> loadResults = loadPlugins(pluginFiles);
        if (!loadResults.isSuccess()) return loadResults;

        List<P> loadedPlugins = new ArrayList<>(pluginIds.size());
        for (PluginResult<P> loadResult : loadResults) {
            loadedPlugins.add(loadResult.getPlugin());
        }

        return enablePlugins(loadedPlugins);
    }

    /**
     * Unloads the given plugin by name.
     */
    public CloseablePluginResult<P> unloadPlugin(String pluginId) {
        return getPlugin(pluginId)
                .map(this::unloadPlugin)
                .orElse(new CloseablePluginResult<>(pluginId, Result.NOT_EXISTS));
    }

    public CloseablePluginResult<P> unloadPlugin(P plugin) {
        return unloadPlugins(Collections.singletonList(plugin)).first();
    }

    /**
     * Unloads a list of plugins.
     */
    public CloseablePluginResults<P> unloadPlugins(List<P> plugins) {
        List<P> orderedPlugins;
        try {
            orderedPlugins = determineLoadOrder(plugins);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();

            StringBuilder sb = new StringBuilder();
            for (P plugin : plugins) {
                sb.append(", ").append(getPluginId(plugin));
            }

            return new CloseablePluginResults<P>().addResult(sb.substring(2), Result.ERROR);
        }

        Collections.reverse(orderedPlugins);
        return unloadOrderedPlugins(orderedPlugins);
    }

    public abstract CloseablePluginResults<P> unloadOrderedPlugins(List<P> plugins);

    /**
     * Determines the load order of a list of plugins.
     */
    public List<P> determineLoadOrder(List<P> plugins) throws IllegalStateException {
        Map<D, P> descriptionMap = new HashMap<>(plugins.size());
        for (P plugin : plugins) {
            descriptionMap.put(getLoadedPluginDescription(plugin), plugin);
        }

        List<P> orderedPlugins = new ArrayList<>(plugins.size());
        for (D description : determineLoadOrder(descriptionMap.keySet())) {
            orderedPlugins.add(descriptionMap.get(description));
        }
        return orderedPlugins;
    }

    /**
     * Determines the load order for a given collection of descriptions.
     * @throws IllegalStateException Iff circular dependency
     */
    public List<D> determineLoadOrder(Collection<? extends D> descriptions) throws IllegalStateException {
        Map<String, D> pluginIdToDescriptionMap = new HashMap<>();
        for (D description : descriptions) {
            pluginIdToDescriptionMap.put(description.getId(), description);
        }

        Map<D, Set<D>> dependencyMap = new HashMap<>(descriptions.size());
        for (D description : descriptions) {
            Set<String> dependencyStrings = description.getDependencies();
            Set<D> dependencies = new HashSet<>();

            for (String dependencyString : dependencyStrings) {
                D dependency = pluginIdToDescriptionMap.get(dependencyString);
                if (dependency != null) {
                    dependencies.add(dependency);
                }
            }

            dependencyMap.put(description, dependencies);
        }

        return DependencyUtils.determineOrder(dependencyMap);
    }
}
