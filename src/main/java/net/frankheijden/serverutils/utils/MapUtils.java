package net.frankheijden.serverutils.utils;

import java.util.*;
import java.util.function.Predicate;

public class MapUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeKeys(Map map, Predicate<Object> predicate) {
        Set<Object> keysToRemove = new HashSet<>();
        map.forEach((k, v) -> {
            if (predicate.test(k)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(map::remove);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void removeValues(Map map, Predicate<Object> predicate) {
        Set<Object> keysToRemove = new HashSet<>();
        map.forEach((k, v) -> {
            if (predicate.test(v)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(map::remove);
    }
}
