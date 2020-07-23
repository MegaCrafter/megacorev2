package com.objectvolatile.megacorev2.util.lang;

public class ActionInfo {

    public final int capture;
    public final ActionInfoParam[] params;

    public ActionInfo(int capture, ActionInfoParam... params) {
        this.capture = capture;
        this.params = params;
    }
}