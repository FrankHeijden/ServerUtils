package net.frankheijden.serverutils.common.reflection;

public class VersionParam {

    public static final Version MIN_VERSION = new Version(Integer.MIN_VALUE, Integer.MIN_VALUE);
    public static final Version MAX_VERSION = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final VersionParam ALL_VERSIONS = new VersionParam(MIN_VERSION, MAX_VERSION);

    public final Version min;
    public final Version max;

    private VersionParam(int min, int max) {
        this(min, Integer.MIN_VALUE, max, Integer.MAX_VALUE);
    }

    private VersionParam(int min, int minPatch, int max, int maxPatch) {
        this(new Version(min, minPatch), new Version(max, maxPatch));
    }

    private VersionParam(Version min, Version max) {
        this.min = min;
        this.max = max;
    }

    public static VersionParam exact(int minor) {
        return new VersionParam(minor, minor);
    }

    public static VersionParam exact(Version ver) {
        return new VersionParam(ver, ver);
    }

    public static VersionParam between(int minMinor, int maxMinor) {
        return new VersionParam(minMinor, maxMinor);
    }

    public static VersionParam between(Version min, Version max) {
        return new VersionParam(min, max);
    }

    public static VersionParam min(int minMinor) {
        return between(minMinor, Integer.MAX_VALUE);
    }

    public static VersionParam min(Version min) {
        return between(min, MAX_VERSION);
    }

    public static VersionParam max(int maxMinor) {
        return between(Integer.MIN_VALUE, maxMinor);
    }

    public static VersionParam max(Version max) {
        return between(MIN_VERSION, max);
    }

    public static class Version {
        public final int minor;
        public final int patch;

        public Version(int minor, int patch) {
            this.minor = minor;
            this.patch = patch;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }
    }
}
