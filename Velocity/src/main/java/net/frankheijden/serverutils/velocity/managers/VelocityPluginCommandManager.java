package net.frankheijden.serverutils.velocity.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class VelocityPluginCommandManager {

    private static final Gson gson = new Gson();

    private final Multimap<String, String> pluginCommands;
    private final Path path;

    public VelocityPluginCommandManager(Path path) {
        this.pluginCommands = Multimaps.synchronizedSetMultimap(HashMultimap.create());
        this.path = path;
    }

    /**
     * Loads and constructs a new {@link VelocityPluginCommandManager} from the given {@link Path}.
     */
    public static VelocityPluginCommandManager load(Path path) throws IOException {
        VelocityPluginCommandManager manager = new VelocityPluginCommandManager(path);
        if (Files.exists(path)) {
            Map<String, Collection<String>> rawMap = gson.fromJson(
                    Files.newBufferedReader(path),
                    new TypeToken<Map<String, Collection<String>>>(){}.getType()
            );
            rawMap.forEach(manager.pluginCommands::putAll);
        }

        return manager;
    }

    /**
     * Attempts to find the plugin id for a given command alias.
     */
    public Optional<String> findPluginId(String alias) {
        for (Map.Entry<String, String> entry : pluginCommands.entries()) {
            if (alias.equals(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Multimap<String, String> getPluginCommands() {
        return pluginCommands;
    }

    /**
     * Saves the map to the {@link Path} it was loaded from.
     */
    public void save() throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        Files.write(
                path,
                gson.toJson(pluginCommands.asMap()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
