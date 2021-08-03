package net.frankheijden.serverutils.common.tasks;

import com.sun.nio.file.SensitivityWatchEventModifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.entities.AbstractTask;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.WatchResult;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.utils.FileUtils;
import net.kyori.adventure.text.minimessage.Template;

public class PluginWatcherTask<P, T> extends AbstractTask {

    private static final WatchEvent.Kind<?>[] EVENTS = new WatchEvent.Kind[]{
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE
    };

    private final ServerUtilsPlugin<P, T, ?, ?, ?> plugin;
    private final ServerUtilsAudience<?> sender;
    private final Map<String, WatchEntry> fileNameToWatchEntryMap;
    private final Map<String, WatchEntry> pluginIdToWatchEntryMap;

    private final AtomicBoolean run = new AtomicBoolean(true);
    private WatchService watchService;
    private T task = null;

    /**
     * Constructs a new PluginWatcherTask for the specified plugin.
     */
    public PluginWatcherTask(ServerUtilsPlugin<P, T, ?, ?, ?> plugin, ServerUtilsAudience<?> sender, List<P> plugins) {
        this.plugin = plugin;
        this.sender = sender;
        this.fileNameToWatchEntryMap = new HashMap<>();
        this.pluginIdToWatchEntryMap = new HashMap<>();

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        for (P watchPlugin : plugins) {
            File file = pluginManager.getPluginFile(watchPlugin);

            WatchEntry entry = new WatchEntry(pluginManager.getPluginId(watchPlugin));
            entry.update(file);

            this.fileNameToWatchEntryMap.put(file.getName(), entry);
        }
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.watchService = watchService;

            AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
            Path basePath = pluginManager.getPluginsFolder().toPath();
            basePath.register(watchService, EVENTS, SensitivityWatchEventModifier.HIGH);

            while (run.get()) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path path = basePath.resolve((Path) event.context());

                    if (!Files.isDirectory(path)) {
                        handleWatchEvent(path);
                    }
                }

                if ((fileNameToWatchEntryMap.isEmpty() && pluginIdToWatchEntryMap.isEmpty()) || !key.reset()) {
                    send(WatchResult.STOPPED);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ClosedWatchServiceException ignored) {
            //
        }
    }

    private void handleWatchEvent(Path path) {
        String fileName = path.getFileName().toString();
        WatchEntry entry = fileNameToWatchEntryMap.get(fileName);

        if (entry == null && Files.exists(path)) {
            Optional<? extends ServerUtilsPluginDescription> descriptionOptional;
            try {
                descriptionOptional = plugin.getPluginManager().getPluginDescription(path.toFile());
            } catch (InvalidPluginDescriptionException ignored) {
                return;
            }

            if (descriptionOptional.isPresent()) {
                ServerUtilsPluginDescription description = descriptionOptional.get();
                WatchEntry foundEntry = pluginIdToWatchEntryMap.remove(description.getId());
                if (foundEntry != null) {
                    send(WatchResult.DELETED_FILE_IS_CREATED, Template.of("plugin", foundEntry.pluginId));
                    fileNameToWatchEntryMap.put(fileName, foundEntry);

                    if (pluginIdToWatchEntryMap.isEmpty()) {
                        entry = foundEntry;
                    }
                }
            }
        }

        if (entry != null) {
            checkWatchEntry(entry, fileName);
        }
    }

    private void checkWatchEntry(WatchEntry entry, String fileName) {
        if (task != null) {
            plugin.getTaskManager().cancelTask(task);
        }

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        Optional<File> fileOptional = pluginManager.getPluginFile(entry.pluginId);
        if (!fileOptional.isPresent()) {
            send(WatchResult.FILE_DELETED, Template.of("plugin", entry.pluginId));

            fileNameToWatchEntryMap.remove(fileName);
            pluginIdToWatchEntryMap.put(entry.pluginId, entry);
            return;
        }

        String previousHash = entry.hash;
        long previousTimestamp = entry.timestamp;
        entry.update(fileOptional.get());

        task = plugin.getTaskManager().runTaskLater(() -> {
            if (entry.hash.equals(previousHash) || previousTimestamp < entry.timestamp - 1000L) {
                send(WatchResult.CHANGE);

                List<P> plugins = new ArrayList<>(fileNameToWatchEntryMap.size());
                Map<String, WatchEntry> retainedWatchEntries = new HashMap<>();
                for (WatchEntry oldEntry : fileNameToWatchEntryMap.values()) {
                    Optional<P> pluginOptional = pluginManager.getPlugin(oldEntry.pluginId);
                    if (!pluginOptional.isPresent()) continue;

                    plugins.add(pluginOptional.get());
                    retainedWatchEntries.put(oldEntry.pluginId, oldEntry);
                }

                fileNameToWatchEntryMap.clear();

                PluginResults<P> reloadResults = pluginManager.reloadPlugins(plugins);
                sender.sendMessage(reloadResults.toComponent(MessageKey.RELOADPLUGIN));

                for (PluginResult<P> reloadResult : reloadResults) {
                    if (!reloadResult.isSuccess()) continue;

                    P reloadedPlugin = reloadResult.getPlugin();
                    String pluginId = pluginManager.getPluginId(reloadedPlugin);

                    WatchEntry retainedEntry = retainedWatchEntries.get(pluginId);
                    String pluginFileName = pluginManager.getPluginFile(reloadedPlugin).getName();
                    fileNameToWatchEntryMap.put(pluginFileName, retainedEntry);
                }
            }
        }, 10L);
    }

    private void send(WatchResult result, Template... templates) {
        result.sendTo(sender, templates);
        if (sender.isPlayer()) {
            result.sendTo(plugin.getChatProvider().getConsoleServerAudience(), templates);
        }
    }

    @Override
    public void cancel() {
        run.set(false);
        try {
            watchService.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static final class WatchEntry {

        private final String pluginId;
        private String hash = null;
        private long timestamp = 0L;

        public WatchEntry(String pluginId) {
            this.pluginId = pluginId;
        }

        public void update(File file) {
            this.hash = FileUtils.getHash(file.toPath());
            this.timestamp = System.currentTimeMillis();
        }
    }
}
