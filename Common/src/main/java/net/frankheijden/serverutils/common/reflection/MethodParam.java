package net.frankheijden.serverutils.common.reflection;

import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

public class MethodParam {

    public String method;
    public VersionParam versionParam;
    public Class<?>[] params;

    private MethodParam(String method, VersionParam versionParam, Class<?>... params) {
        this.method = method;
        this.versionParam = versionParam;
        this.params = params;
    }

    public static MethodParam methodOf(String method, VersionParam versionParam, Class<?>... params) {
        return new MethodParam(method, versionParam, params);
    }

    public static MethodParam methodOf(String method, Class<?>... params) {
        return methodOf(method, ALL_VERSIONS, params);
    }
}
