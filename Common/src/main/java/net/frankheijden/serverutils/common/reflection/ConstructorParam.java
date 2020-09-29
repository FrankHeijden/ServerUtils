package net.frankheijden.serverutils.common.reflection;

public class ConstructorParam {

    public final Class<?>[] params;

    private ConstructorParam(Class<?>[] params) {
        this.params = params;
    }

    public static ConstructorParam constructorOf(Class<?>... params) {
        return new ConstructorParam(params);
    }
}
