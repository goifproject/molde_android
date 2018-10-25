package com.limefriends.molde.common.utils.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static boolean validateEmail(String emalStr){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emalStr);
        return matcher.find();
    }
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{10,20}$"); // 10~20

    public static boolean validatePassword(String pwStr){
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }
}
