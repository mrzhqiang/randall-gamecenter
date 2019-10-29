package com.github.mrzhqiang.randall.gamecenter.util;

/**
 * @author mrzhqiang
 */
public enum  Environments {
    ;

    public static boolean debugMode() {
        // todo check Eclipse property or not check
        return Boolean.parseBoolean(System.getProperty("intellij.debug.agent", "false"));
    }

    public static String homework() {
        return System.getProperty("user.dir");
    }

    public static String debugWork() {
        return System.getProperty("debug.dir", "./sample");
    }
}
