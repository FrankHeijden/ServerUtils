package net.frankheijden.serverutils.common.entities.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.frankheijden.serverutils.common.config.ConfigKey;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;

public class PluginResults<T> implements Iterable<PluginResult<T>> {

    protected final List<PluginResult<T>> results;

    public PluginResults() {
        this.results = new ArrayList<>();
    }

    public PluginResults<T> addResult(String pluginId, Result result, String... placeholders) {
        addResult(pluginId, null, result, placeholders);
        return this;
    }

    public PluginResults<T> addResult(String pluginId, T plugin, String... placeholders) {
        addResult(pluginId, plugin, Result.SUCCESS, placeholders);
        return this;
    }

    protected PluginResults<T> addResult(String pluginId, T plugin, Result result, String... placeholders) {
        addResult(new PluginResult<>(pluginId, plugin, result, placeholders));
        return this;
    }

    public PluginResults<T> addResult(PluginResult<T> pluginResult) {
        this.results.add(pluginResult);
        return this;
    }

    public boolean isSuccess() {
        return results.stream().allMatch(PluginResult::isSuccess);
    }

    public List<PluginResult<T>> getResults() {
        return results;
    }

    /**
     * Creates an array of all plugins.
     * @throws IllegalArgumentException Iff a result was not successful (check {@link PluginResults#isSuccess()} first!)
     */
    public List<T> getPlugins() {
        List<T> plugins = new ArrayList<>(results.size());
        for (PluginResult<T> result : results) {
            if (!result.isSuccess()) throw new IllegalArgumentException(
                    "Result after handling plugin '" + result.getPluginId() + "' was not successful!"
            );
            plugins.add(result.getPlugin());
        }
        return plugins;
    }

    public PluginResult<T> first() {
        return results.get(0);
    }

    public PluginResult<T> last() {
        return results.get(results.size() - 1);
    }

    /**
     * Sends the result(s) to the given sender.
     */
    public void sendTo(ServerUtilsAudience<?> sender, ConfigKey successKey) {
        for (PluginResult<T> result : results) {
            result.sendTo(sender, successKey);
        }
    }

    @Override
    public Iterator<PluginResult<T>> iterator() {
        return results.iterator();
    }
}
