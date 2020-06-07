package net.frankheijden.serverutils.utils;

public class VersionUtils {

    public static boolean isNewVersion(String oldVersion, String newVersion) {
        String[] oldVersionSplit = oldVersion.split("\\.");
        String[] newVersionSplit = newVersion.split("\\.");

        int i = 0;
        while (i < oldVersionSplit.length && i < newVersionSplit.length) {
            int o = Integer.parseInt(oldVersionSplit[i]);
            int n = Integer.parseInt(newVersionSplit[i]);
            if (i != oldVersionSplit.length - 1 && i != newVersionSplit.length - 1) {
                if (n < o) return false;
            }
            if (n > o) return true;
            i++;
        }
        return false;
    }
}
