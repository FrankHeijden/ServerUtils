package net.frankheijden.serverutils.common.tasks;

import com.sun.nio.file.SensitivityWatchEventModifier;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.AbstractTask;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.entities.WatchResult;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

public class PluginWatcherTask extends AbstractTask {

    private static final WatchEvent.Kind<?>[] EVENTS = new WatchEvent.Kind[]{
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE
    };

    private final ServerUtilsPlugin plugin = ServerUtilsApp.getPlugin();

    @SuppressWarnings("rawtypes")
    private final AbstractPluginManager pluginManager = plugin.getPluginManager();
    private final ChatProvider chatProvider = plugin.getChatProvider();
    @SuppressWarnings("rawtypes")
    private final AbstractTaskManager taskManager = plugin.getTaskManager();

    private final ServerCommandSender sender;
    private final String pluginName;
    private final AtomicBoolean run;
    private File file;
    private String hash;
    private long hashTimestamp = 0;

    private WatchService watchService;
    private Object task = null;

    /**
     * Constructs a new PluginWatcherTask for the specified plugin.
     *
     * @param pluginName The name of the plugin.
     */
    public PluginWatcherTask(ServerCommandSender sender, String pluginName) {
        this.sender = sender;
        this.pluginName = pluginName;
        this.file = pluginManager.getPluginFile(pluginName);
        this.run = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.watchService = watchService;

            File folder = pluginManager.getPluginsFolder();
            folder.toPath().register(watchService, EVENTS, SensitivityWatchEventModifier.HIGH);

            while (run.get()) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (file.getName().equals(event.context().toString())) {
                        if (task != null) {
                            //noinspection unchecked
                            taskManager.cancelTask(task);
                        }

                        String previousHash = hash;
                        long previousHashTimestamp = hashTimestamp;

                        hash = FileUtils.getHash(file.toPath());
                        hashTimestamp = System.currentTimeMillis();
                        task = ServerUtilsApp.getPlugin().getTaskManager().runTaskLater(() -> {
                            if (hash.equals(previousHash) || previousHashTimestamp < hashTimestamp - 1000L) {
                                send(WatchResult.CHANGE);

                                pluginManager.reloadPlugin(pluginName);
                                file = pluginManager.getPluginFile(pluginName);
                            }
                        }, 10L);
                    }
                }

                if (file == null || !key.reset()) {
                    send(WatchResult.STOPPED);
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        } catch (ClosedWatchServiceException ignored) {
            //
        }
    }

    private void send(WatchResult result) {
        result.sendTo(sender, null, pluginName);
        if (sender.isPlayer()) {
            result.sendTo(chatProvider.getConsoleSender(), null, pluginName);
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
}
