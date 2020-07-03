package net.frankheijden.serverutils.common.reflection;

public class VersionParam {

    public static VersionParam ALL_VERSIONS = new VersionParam(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public int min;
    public int max;

    private VersionParam(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static VersionParam versionOf(int ver) {
        return new VersionParam(ver, ver);
    }

    public static VersionParam between(int min, int max) {
        return new VersionParam(min, max);
    }

    public static VersionParam min(int min) {
        return between(min, Integer.MAX_VALUE);
    }

    public static VersionParam max(int max) {
        return between(Integer.MIN_VALUE, max);
    }
}
