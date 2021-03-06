package net.frankheijden.serverutils.common.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class MapUtils {

    /**
     * Removes keys from a map using a predicate.
     * @param map The map.
     * @param predicate The predicate used to test removal.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeKeys(Map map, Predicate<Object> predicate) {
        if (map == null) return;
        Set<Object> keysToRemove = new HashSet<>();
        map.forEach((k, v) -> {
            if (predicate.test(k)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(map::remove);
    }

    /**
     * Removes values from a map using a predicate.
     * @param map The map.
     * @param predicate The predicate used to test removal.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeValues(Map map, Predicate<Object> predicate) {
        if (map == null) return;
        Set<Object> valuesToRemove = new HashSet<>();
        map.forEach((k, v) -> {
            if (predicate.test(v)) {
                valuesToRemove.add(k);
            }
        });
        valuesToRemove.forEach(map::remove);
    }

    /**
     * Removes a key from a map.
     * @param map The map instance.
     * @param obj The object to remove.
     */
    @SuppressWarnings("rawtypes")
    public static void remove(Map map, Object obj) {
        if (map == null) return;
        map.remove(obj);
    }
}
