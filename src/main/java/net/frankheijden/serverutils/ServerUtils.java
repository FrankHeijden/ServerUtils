package net.frankheijden.serverutils;

import co.aikar.commands.PaperCommandManager;
import net.frankheijden.serverutils.commands.CommandPlugins;
import net.frankheijden.serverutils.commands.CommandServerUtils;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.reflection.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerUtils extends JavaPlugin implements CommandExecutor {

    private static ServerUtils instance;
    private PaperCommandManager commandManager;

    public static ServerUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        this.removeCommands("pl", "plugins");

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CommandServerUtils());
        commandManager.registerCommand(new CommandPlugins());
        commandManager.getCommandCompletions().registerAsyncCompletion("plugins", context -> Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .collect(Collectors.toList()));
        commandManager.getCommandCompletions().registerAsyncCompletion("pluginJars", context -> Arrays.stream(getJars())
                .map(File::getName)
                .collect(Collectors.toList()));
        commandManager.getCommandCompletions().registerAsyncCompletion("supportedConfigs", context -> CommandServerUtils.getSupportedConfigs());
        reload();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void removeCommands(String... commands) {
        Map<String, Command> map;
        try {
            map = RCommandMap.getKnownCommands(RCraftServer.getCommandMap());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        for (String command : commands) {
            map.remove(command);
        }
    }

    public void reload() {
        new Messenger(copyResourceIfNotExists("messages.yml"));
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    private File[] getJars() {
        File parent = getDataFolder().getParentFile();
        if (parent == null) return new File[0];
        return parent.listFiles(f -> f.getName().endsWith(".jar"));
    }

    private void createDataFolderIfNotExists() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
    }

    private File copyResourceIfNotExists(String resource) {
        createDataFolderIfNotExists();

        File file = new File(getDataFolder(), resource);
        if (!file.exists()) {
            getLogger().info(String.format("'%s' not found, creating!", resource));
            saveResource(resource, false);
        }
        return file;
    }
}
