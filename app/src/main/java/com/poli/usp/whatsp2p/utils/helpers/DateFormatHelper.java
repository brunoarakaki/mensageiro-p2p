package com.poli.usp.whatsp2p.utils.helpers;

import java.util.Locale;

import com.poli.usp.whatsp2p.utils.base.BaseDateFormatHelper;

/**
 * Created by mobile2you on 11/08/16.
 */
public class DateFormatHelper extends BaseDateFormatHelper {
    public DateFormatHelper(String dateFormat) {
        super(dateFormat);
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
