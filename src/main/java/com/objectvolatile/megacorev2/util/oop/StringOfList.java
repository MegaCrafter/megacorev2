package com.objectvolatile.megacorev2.util.oop;

import java.util.Arrays;
import java.util.List;

public class StringOfList {

    private List<String> list;
    public StringOfList(List<String> list) {
        this.list = list;
    }

    public StringOfList(String[] array) {
        this.list = Arrays.asList(array);
    }

    public String value() {
        StringBuilder builder = new StringBuilder();

        boolean comma = false;
        for (String element : list) {
            if (comma) builder.append(", ");
            builder.append(element);
            comma = true;
        }

        return builder.toString();
    }

}