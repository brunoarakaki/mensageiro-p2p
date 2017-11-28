package com.poli.usp.whatsp2p.utils.base;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by mobile2you on 11/08/16.
 */
public class BaseStringUtils {

    /**
     * Returns string of float value to percentage
     * @param value
     * @param maximumFractionDigits length of fraction digits
     * @return percentage value of @value
     */
    public static String toPercentage(float value, int maximumFractionDigits) {
        NumberFormat number = NumberFormat.getPercentInstance();
        number.setMaximumFractionDigits(maximumFractionDigits);
        return number.format(value);
    }

    /**
     * @param value value to format
     * @param locale locale of the currency to format
     * @return the value with the locale currency
     */
    public static String formatCurrency(float value, Locale locale) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String returnable = currencyFormatter.format(value);
        return returnable;
    }

    /**
     * @param text
     * @return text with the first letter capitalized
     */
    public static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
