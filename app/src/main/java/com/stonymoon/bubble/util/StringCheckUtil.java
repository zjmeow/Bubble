package com.stonymoon.bubble.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringCheckUtil {
    private static String passwordRegex = "[a-zA-Z0-9]{1,16}";

    public static boolean isPassword(String password) {
        return Pattern.matches(passwordRegex, password);
    }


}
