package com.objectvolatile.megacorev2.util.oop;

import java.util.ArrayList;
import java.util.List;

public class ColoredList {

    private List<String> list;

    public ColoredList(List<String> list) {
        this.list = list;
    }

    public List<String> applied() {
        List<String> safe = new ArrayList<>(list);

        for (int size = safe.size(), i = 0; i < size; i++) {
            safe.set(i, new ColoredString(safe.get(i)).applied());
        }

        return safe;
    }
}