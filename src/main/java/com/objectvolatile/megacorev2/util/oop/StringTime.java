package com.objectvolatile.megacorev2.util.oop;

import java.util.regex.Pattern;

public class StringTime {

    private String timestr;
    private char split;

    public StringTime(String timestr, char split) {
        this.timestr = timestr;
        this.split = split;
    }

    public long seconds() {
        String[] times = timestr.split(Pattern.quote(split+""));

        if (times.length > 4) {
            throw new IllegalArgumentException("Bad format: " + timestr);
        }

        long time = 0;
        int mul = 1;
        for (int i = times.length - 1; i > times.length - 4; i--) {
            if (i < 0) break;

            time += Integer.parseInt(times[i]) * mul;

            mul *= 60;
        }

        if (times.length == 4) {
            mul *= 7;

            time += Integer.parseInt(times[0]) * mul;
        }

        return time;
    }
}