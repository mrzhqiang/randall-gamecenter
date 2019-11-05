package main.java.randall.gamecenter.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author mrzhqiang
 */
public enum Throwables {
    ;

    public static String print(Throwable e) {
        if (!Environments.debugMode()) {
            return e != null ? e.getMessage() : "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String stackTrace() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (builder.length() > 0) {
                builder.append("\r\n");
            }
            builder.append(element.toString());
        }
        return builder.toString();
    }
}
