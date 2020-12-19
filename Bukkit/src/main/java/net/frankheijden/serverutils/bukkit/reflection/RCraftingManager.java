package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.bukkit.entities.BukkitReflection.MINOR;
import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.VersionParam.min;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;
import net.frankheijden.serverutils.common.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RCraftingManager {

    private static Class<?> craftingManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            craftingManagerClass = Class.forName(String.format("net.minecraft.server.%s.CraftingManager",
                    BukkitReflection.NMS));
            fields = getAllFields(craftingManagerClass,
                    fieldOf("recipes", min(12)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes all associated recipes of a plugin.
     * @param plugin The plugin to remove recipes for.
     * @throws IllegalAccessException When prohibited access to the method.
     * @throws InvocationTargetException If the method call produced an exception.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeRecipesFor(Plugin plugin) throws IllegalAccessException, InvocationTargetException {
        // Cleaning up recipes before MC 1.12 is not possible,
        // as recipes are not associated to plugins.
        if (MINOR < 12) return;

        Field recipesField = fields.get("recipes");

        if (MINOR == 12) {
            Object recipes = get(fields, null, "recipes");
            RRegistryMaterials.removeKeysFor(recipes, plugin);
        } else {
            Object server = invoke(RMinecraftServer.getMethods(), null, "getServer");
            Object craftingManager = invoke(RMinecraftServer.getMethods(), server, "getCraftingManager");
            Map recipes = (Map) recipesField.get(craftingManager);

            Predicate<Object> predicate = RMinecraftKey.matchingPluginPredicate(new AtomicBoolean(false), plugin);
            if (MINOR == 13) {
                MapUtils.removeKeys(recipes, predicate);
            } else {
                Collection<Map> list = (Collection<Map>) recipes.values();
                list.forEach(map -> MapUtils.removeKeys(map, predicate));
            }
        }
    }
}
