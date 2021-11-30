package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.frankheijden.serverutils.common.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RCraftingManager {

    private static final MinecraftReflection reflection;

    static {
        if (MinecraftReflectionVersion.MINOR >= 17) {
            reflection = MinecraftReflection.of("net.minecraft.world.item.crafting.CraftingManager");
        } else {
            reflection = MinecraftReflection.of("net.minecraft.server.%s.CraftingManager");
        }
    }

    private RCraftingManager() {}

    /**
     * Removes all associated recipes of a plugin.
     * @param plugin The plugin to remove recipes for.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeRecipesFor(Plugin plugin) {
        // Cleaning up recipes before MC 1.12 is not possible,
        // as recipes are not associated to plugins.
        if (MinecraftReflectionVersion.MINOR == 12) {
            RRegistryMaterials.removeKeysFor(reflection.get(null, "recipes"), plugin);
        } else if (MinecraftReflectionVersion.MINOR > 12) {
            Object server = RMinecraftServer.getReflection().invoke(null, "getServer");
            String getCraftingManagerMethod = MinecraftReflectionVersion.MINOR >= 18 ? "aC" : "getCraftingManager";
            Object craftingManager = RMinecraftServer.getReflection().invoke(server, getCraftingManagerMethod);

            Map recipes;
            if (MinecraftReflectionVersion.MINOR >= 17) {
                recipes = reflection.get(craftingManager, "c");
            } else {
                recipes = reflection.get(craftingManager, "recipes");
            }

            Predicate<Object> predicate = RMinecraftKey.matchingPluginPredicate(new AtomicBoolean(false), plugin);
            if (MinecraftReflectionVersion.MINOR == 13) {
                MapUtils.removeKeys(recipes, predicate);
            } else {
                Collection<Map> list = (Collection<Map>) recipes.values();
                list.forEach(map -> MapUtils.removeKeys(map, predicate));
            }
        }
    }
}
