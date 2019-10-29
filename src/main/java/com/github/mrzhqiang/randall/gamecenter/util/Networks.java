package com.github.mrzhqiang.randall.gamecenter.util;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * @author mrzhqiang
 */
public enum Networks {
    ;

    private static final String REGEX_V4_ADDRESS = "([0-9]{1,3}(\\.[0-9]{1,3}){3})";
    private static final String REGEX_V6_ADDRESS = "[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7}";

    public static boolean isAddressV4(String address) {
        Preconditions.checkNotNull(address, "address == null");
        if (Pattern.matches(REGEX_V4_ADDRESS, address)) {
            String[] strings = address.split("\\.");
            for (String value : strings) {
                try {
                    int i = Integer.parseInt(value);
                    if (i < 0 || i > 255) {
                        return false;
                    }
                } catch (Exception ignore) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isAddressV6(String address) {
        Preconditions.checkNotNull(address, "address == null");
        return Pattern.matches(REGEX_V6_ADDRESS, address);
    }
}
