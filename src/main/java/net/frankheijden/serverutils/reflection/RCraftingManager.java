package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.MINOR;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.min;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.invoke;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import net.frankheijden.serverutils.utils.MapUtils;
import org.bukkit.plugin.Plugin;

public class RCraftingManager {

    private static Class<?> craftingManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            craftingManagerClass = Class.forName(String.format("net.minecraft.server.%s.CraftingManager",
                    ReflectionUtils.NMS));
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
