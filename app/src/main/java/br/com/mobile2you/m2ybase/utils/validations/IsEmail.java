package br.com.mobile2you.m2ybase.utils.validations;

/**
 * Created by mobile2you on 12/08/16.
 */
public class IsEmail {

    private static final String EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    public static boolean isValid(String text) {
        return text.matches(EMAIL_PATTERN);
    }

}
