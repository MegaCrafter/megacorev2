package com.objectvolatile.megacorev2.util.oop;

import com.objectvolatile.megacorev2.util.MUtils;

import java.util.Map;

public class PlaceholderString {

    private char symbol;
    private String base;
    private Map<String, Object> placeholderMap;

    public PlaceholderString(String base, Map<String, Object> placeholderMap) {
        this(base, '%', placeholderMap);
    }

    public PlaceholderString(String base, char symbol, Map<String, Object> placeholderMap) {
        this.base = base;
        this.symbol = symbol;
        this.placeholderMap = placeholderMap;
    }

    public String applied() {
        String[] split = (base + " ").split(symbol+"");
        // If split ends with the symbol, it doesnt count one more but it counts for the first no matter what
        // interesting but should be taken into action

        for (int i = 1; i < split.length - 1; i++) { // Dont count the first and the last parts (as they cant be embraced)
            String part = split[i];
            Object match = placeholderMap.get(part);
            if (match instanceof String) {
                base = MUtils.fastReplace(base, symbol + part + symbol, (String) match);
            }
        }

        return base;
    }
}