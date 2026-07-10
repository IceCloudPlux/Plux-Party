package com.plux.party.util;

import org.bukkit.Material;

public class MaterialAdapter {

    private static final String[] OLD_WOOL_NAMES = {
        "WOOL",
        "ORANGE_WOOL",
        "MAGENTA_WOOL",
        "LIGHT_BLUE_WOOL",
        "YELLOW_WOOL",
        "LIME_WOOL",
        "PINK_WOOL",
        "GRAY_WOOL",
        "LIGHT_GRAY_WOOL",
        "CYAN_WOOL",
        "PURPLE_WOOL",
        "BLUE_WOOL",
        "BROWN_WOOL",
        "GREEN_WOOL",
        "RED_WOOL",
        "BLACK_WOOL"
    };

    private static final String[] NEW_WOOL_NAMES = {
        "WHITE_WOOL",
        "ORANGE_WOOL",
        "MAGENTA_WOOL",
        "LIGHT_BLUE_WOOL",
        "YELLOW_WOOL",
        "LIME_WOOL",
        "PINK_WOOL",
        "GRAY_WOOL",
        "LIGHT_GRAY_WOOL",
        "CYAN_WOOL",
        "PURPLE_WOOL",
        "BLUE_WOOL",
        "BROWN_WOOL",
        "GREEN_WOOL",
        "RED_WOOL",
        "BLACK_WOOL"
    };

    public static Material getMaterial(String name) {
        if (name == null || name.isEmpty()) {
            return Material.STONE;
        }

        if (VersionUtil.is1_13OrHigher()) {
            return getMaterial1_13(name);
        } else {
            return getMaterial1_12(name);
        }
    }

    private static Material getMaterial1_13(String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    private static Material getMaterial1_12(String name) {
        String upperName = name.toUpperCase();

        if (upperName.contains("WOOL")) {
            for (int i = 0; i < NEW_WOOL_NAMES.length; i++) {
                if (upperName.equals(NEW_WOOL_NAMES[i])) {
                    try {
                        return Material.valueOf(OLD_WOOL_NAMES[i]);
                    } catch (IllegalArgumentException e) {
                        return Material.WOOL;
                    }
                }
            }
        }

        if (upperName.equals("PLAYER_HEAD")) {
            try {
                return Material.valueOf("SKULL_ITEM");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("NETHER_STAR")) {
            try {
                return Material.valueOf("NETHER_STAR");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("BARRIER")) {
            try {
                return Material.valueOf("BARRIER");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("ARROW")) {
            try {
                return Material.valueOf("ARROW");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("BOOK")) {
            try {
                return Material.valueOf("BOOK");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("OAK_DOOR")) {
            try {
                return Material.valueOf("WOOD_DOOR");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("LIME_DYE")) {
            try {
                return Material.valueOf("INK_SACK");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        if (upperName.equals("RED_DYE")) {
            try {
                return Material.valueOf("INK_SACK");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }

        try {
            return Material.valueOf(upperName);
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    public static Material getPlayerHead() {
        if (VersionUtil.is1_13OrHigher()) {
            Material mat = Material.getMaterial("PLAYER_HEAD");
            return mat != null ? mat : Material.STONE;
        } else {
            try {
                return Material.valueOf("SKULL_ITEM");
            } catch (IllegalArgumentException e) {
                return Material.STONE;
            }
        }
    }

    public static Material getGreenWool() {
        if (VersionUtil.is1_13OrHigher()) {
            Material mat = Material.getMaterial("GREEN_WOOL");
            return mat != null ? mat : Material.STONE;
        } else {
            return Material.WOOL;
        }
    }

    public static Material getRedWool() {
        if (VersionUtil.is1_13OrHigher()) {
            Material mat = Material.getMaterial("RED_WOOL");
            return mat != null ? mat : Material.STONE;
        } else {
            return Material.WOOL;
        }
    }
}
