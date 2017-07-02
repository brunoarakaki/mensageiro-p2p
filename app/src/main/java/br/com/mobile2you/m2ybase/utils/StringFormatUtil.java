package br.com.mobile2you.m2ybase.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mobile2you on 30/08/16.
 */
public class StringFormatUtil {

    public static StringFormatUtil from(String format) {
        return new StringFormatUtil(format);
    }

    private final String format;
    private final Map<String, Object> tags = new LinkedHashMap<String, Object>();

    private StringFormatUtil(String format) {
        this.format = format;
    }

    public StringFormatUtil with(String key, Object value) {
        tags.put("\\{" + key + "\\}", value);
        return this;
    }

    public String format() {
        String formatted = format;
        for (Map.Entry<String, Object> tag : tags.entrySet()) {
            // bottleneck, creating temporary String objects!
            formatted = formatted.replaceAll(tag.getKey(), tag.getValue().toString());
        }
        return formatted;
    }
}
