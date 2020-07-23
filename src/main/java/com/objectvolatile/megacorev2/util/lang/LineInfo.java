package com.objectvolatile.megacorev2.util.lang;

public class LineInfo {

    public static final int NORMAL = 0;
    public static final int TITLE = 1;
    public static final int ACTIONBAR = 2;

    private int showpos = NORMAL;

    private int fadeIn = 0;
    private int dur = 0;
    private int fadeOut = 0;

    private String subtitleJson = "";

    public void setShowpos(int showpos) {
        this.showpos = showpos;
    }
    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }
    public void setDur(int dur) {
        this.dur = dur;
    }
    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }
    public void setSubtitleJson(String subtitleJson) {
        this.subtitleJson = subtitleJson;
    }

    public int showpos() {
        return showpos;
    }
    public int fadeIn() {
        return fadeIn;
    }
    public int dur() {
        return dur;
    }
    public int fadeOut() {
        return fadeOut;
    }
    public String subtitleJson() {
        return subtitleJson;
    }
}