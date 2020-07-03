package net.frankheijden.serverutils.common.reflection;

public class FieldParam {
    public String field;
    public VersionParam versionParam;

    private FieldParam(String field, VersionParam versionParam) {
        this.field = field;
        this.versionParam = versionParam;
    }

    public static FieldParam fieldOf(String field, VersionParam versionParam) {
        return new FieldParam(field, versionParam);
    }
}
