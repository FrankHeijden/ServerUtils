package net.frankheijden.serverutils.common.reflection;

import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

public class FieldParam {

    public final String field;
    public final VersionParam versionParam;

    private FieldParam(String field, VersionParam versionParam) {
        this.field = field;
        this.versionParam = versionParam;
    }

    public static FieldParam fieldOf(String field, VersionParam versionParam) {
        return new FieldParam(field, versionParam);
    }

    public static FieldParam fieldOf(String field) {
        return fieldOf(field, ALL_VERSIONS);
    }
}
