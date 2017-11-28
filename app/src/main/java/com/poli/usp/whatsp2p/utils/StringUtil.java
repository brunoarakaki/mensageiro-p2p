package com.poli.usp.whatsp2p.utils;


import com.poli.usp.whatsp2p.Constants;
import com.poli.usp.whatsp2p.utils.base.BaseStringUtils;

import java.util.Locale;

/**
 * Created by mobile2you on 11/08/16.
 */
public class StringUtil extends BaseStringUtils {

    public static String formatCurrencyBRL(float floatValue){
        Locale locale = new Locale(Constants.LANGUAGE_PT, Constants.COUNTRY_BR);
        return formatCurrency(floatValue, locale);
    }

    public static boolean isBlank(String str){
        return str == null || str.trim().equals("");
    }
}
