package com.objectvolatile.megacorev2.util.oop;

public class Clamped<T extends Number> {

    private T number;
    private T min;
    private T max;

    public Clamped(T number, T min, T max) {
        this.number = number;
        this.min = min;
        this.max = max;
    }

    public T value() {
        if (number.floatValue() < min.floatValue()) return min;
        else if (number.floatValue() > max.floatValue()) return max;
        else return number;
    }
}