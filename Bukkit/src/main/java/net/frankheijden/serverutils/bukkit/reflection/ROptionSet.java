package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

public class ROptionSet {

    private static Class<?> optionSetClass;
    private static Map<String, Method> methods;

    static {
        try {
            optionSetClass = Class.forName("joptsimple.OptionSet");
            methods = getAllMethods(optionSetClass,
                    methodOf("valueOf", String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
