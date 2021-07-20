package net.frankheijden.serverutils.velocity.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;

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
        var manager = new VelocityPluginCommandManager(path);
        if (Files.exists(path)) {
            Map<String, Collection<String>> rawMap = gson.fromJson(
                    Files.newBufferedReader(path),
                    new TypeToken<Map<String, Collection<String>>>(){}.getType()
            );
            rawMap.forEach(manager.pluginCommands::putAll);
        }

        return manager;
    }

    public Multimap<String, String> getPluginCommands() {
        return pluginCommands;
    }

    /**
     * Saves the map to the {@link Path} it was loaded from.
     */
    public void save() throws IOException {
        Files.writeString(
                path,
                gson.toJson(pluginCommands.asMap()),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
