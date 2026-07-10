package com.plux.party.util;

import org.bukkit.Bukkit;

public class VersionUtil {

    private static String nmsVersion;
    private static String mcVersion;
    private static int versionNumber;

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
        
        String ver = Bukkit.getVersion();
        int start = ver.indexOf("(MC: ") + 5;
        mcVersion = ver.substring(start, ver.indexOf(')', start));
        
        versionNumber = parseVersionNumber(mcVersion);
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

    public static String getMcVersion() {
        return mcVersion;
    }

    public static int getVersionNumber() {
        return versionNumber;
    }

    public static boolean is1_13OrHigher() {
        return versionNumber >= 113;
    }

    public static boolean is1_12OrLower() {
        return versionNumber <= 112;
    }

    private static int parseVersionNumber(String version) {
        try {
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return major * 100 + minor;
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        return 0;
    }
}
