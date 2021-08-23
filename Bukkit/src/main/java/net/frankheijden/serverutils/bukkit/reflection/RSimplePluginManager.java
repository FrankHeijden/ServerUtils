package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

public class RSimplePluginManager {

    private static final MinecraftReflection reflection = MinecraftReflection.of(SimplePluginManager.class);

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static Map<Pattern, PluginLoader> getFileAssociations(Object manager) throws IllegalAccessException {
        return reflection.get(manager, "fileAssociations");
    }

    public static List<Plugin> getPlugins(Object manager) {
        return reflection.get(manager, "plugins");
    }

    public static Map<Boolean, Set<Permission>> getDefaultPermissions(Object manager) {
        return reflection.get(manager, "defaultPerms");
    }

    /**
     * Clears permissions from the given plugin manager.
     */
    public static void clearPermissions(PluginManager manager, Collection<? extends Permission> permissions) {
        Map<Boolean, Set<Permission>> defaultPerms = reflection.get(manager, "defaultPerms");
        Map<String, Map<Permissible, Boolean>> permSubs = reflection.get(manager, "permSubs");

        for (Permission permission : permissions) {
            manager.removePermission(permission);
            for (Set<Permission> value : defaultPerms.values()) {
                value.remove(permission);
            }

            String name = permission.getName().toLowerCase(Locale.ENGLISH);
            permSubs.remove(name);
        }
    }

    /**
     * Removes the lookup name of the plugin.
     * This ensures the plugin cannot be found anymore in Bukkit#getPlugin(String name).
     * @param manager The SimplePluginManager instance to remove the lookup name from.
     * @param name The name of the plugin to remove.
     */
    public static void removeLookupName(Object manager, String name) {
        Map<String, Plugin> lookupNames = reflection.get(manager, "lookupNames");
        if (lookupNames == null) return;
        lookupNames.remove(name.replace(' ', '_'));
        lookupNames.remove(name.replace(' ', '_').toLowerCase(Locale.ENGLISH)); // Paper
    }
}
