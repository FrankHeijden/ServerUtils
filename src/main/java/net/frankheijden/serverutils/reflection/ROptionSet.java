package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

public class ROptionSet {

    private static Class<?> optionSetClass;
    private static Map<String, Method> methods;

    static {
        try {
            optionSetClass = Class.forName("joptsimple.OptionSet");
            methods = getAllMethods(optionSetClass,
                    methodOf("valueOf", ALL_VERSIONS, String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
