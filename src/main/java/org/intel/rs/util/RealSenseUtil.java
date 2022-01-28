package org.intel.rs.util;

public final class RealSenseUtil {
    private RealSenseUtil() {
    }

    public static boolean toBoolean(int value) {
        return value >= 1;
    }

    public static boolean toBoolean(String value) {
        if (value == null)
            return false;

        return value.equals("YES");
    }
}
