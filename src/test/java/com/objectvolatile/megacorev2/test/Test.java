package com.objectvolatile.megacorev2.test;

import com.objectvolatile.megacorev2.util.oop.PlaceholderString;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        String text = "%prefix% you did it man %prefix%";
        Map<String, Object> map = new HashMap<>();

        map.put("prefix", "&a[Yee]");
        map.put("player", "anen yani");

        System.out.println(new PlaceholderString(text, map).applied());
    }

}