package br.com.mobile2you.m2ybase.utils;


import java.util.Locale;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.utils.base.BaseStringUtils;

/**
 * Created by mobile2you on 11/08/16.
 */
public class StringUtil extends BaseStringUtils {

    public static String formatCurrencyBRL(float floatValue){
        Locale locale = new Locale(Constants.LANGUAGE_PT, Constants.COUNTRY_BR);
        return formatCurrency(floatValue, locale);
    }
}
