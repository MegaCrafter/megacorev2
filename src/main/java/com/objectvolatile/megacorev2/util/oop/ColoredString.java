package com.objectvolatile.megacorev2.util.oop;

import net.md_5.bungee.api.ChatColor;

public class ColoredString {

    private static final char TRANSLATECHAR = '&';

    private String string;

    public ColoredString(String string) {
        this.string = string;
    }

    public String applied() {
        if (string == null) return null;

        return ChatColor.translateAlternateColorCodes(TRANSLATECHAR, string);
    }
}