package net.frankheijden.serverutils.reflection;

import net.frankheijden.serverutils.utils.MapUtils;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.*;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.min;

public class RCraftingManager {

    private static Class<?> craftingManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            craftingManagerClass = Class.forName(String.format("net.minecraft.server.%s.CraftingManager", ReflectionUtils.NMS));
            fields = getAllFields(craftingManagerClass,
                    fieldOf("recipes", min(12)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

            if (MINOR == 13) {
                MapUtils.removeKeys(recipes, RMinecraftKey.matchingPluginPredicate(new AtomicBoolean(false), plugin));
            } else {
                Collection<Map> list = (Collection<Map>) recipes.values();
                list.forEach(map -> MapUtils.removeKeys(map, RMinecraftKey.matchingPluginPredicate(new AtomicBoolean(false), plugin)));
            }
        }
    }
}
