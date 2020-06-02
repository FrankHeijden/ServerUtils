package net.frankheijden.serverutils.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.invoke;

public class RJsonList {

    private static Class<?> jsonListClass;
    private static Map<String, Method> methods;
    static {
        try {
            jsonListClass = Class.forName(String.format("net.minecraft.server.%s.JsonList", ReflectionUtils.NMS));
            methods = getAllMethods(jsonListClass,
                    methodOf("load", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void load(Object jsonList) throws InvocationTargetException, IllegalAccessException {
        invoke(methods, jsonList, "load");
    }
}
