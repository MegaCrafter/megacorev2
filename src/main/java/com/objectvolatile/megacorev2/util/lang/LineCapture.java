package com.objectvolatile.megacorev2.util.lang;

public class LineCapture {

    private int startIndex;
    private int endIndex;

    public LineCapture(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int startIndex() {
        return startIndex;
    }

    public int endIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        return String.format("[LineCapture, start: %d, end: %d]", startIndex, endIndex);
    }
}