package com.objectvolatile.megacorev2.util.lang;

import java.util.Map;

public class LineCaptureIndices {

    private Map<Integer, LineCapture> indices;

    public LineCaptureIndices(Map<Integer, LineCapture> indices) {
        this.indices = indices;
    }

    public Map<Integer, LineCapture> indices() {
        return indices;
    }

    @Override
    public String toString() {
        return "[LineCaptureIndices " + indices + "]";
    }
}