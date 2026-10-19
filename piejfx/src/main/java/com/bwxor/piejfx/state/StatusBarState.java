package com.bwxor.piejfx.state;

public final class StatusBarState {
    public static final StatusBarState instance = new StatusBarState();

    private boolean statusBarVisible = true;

    private StatusBarState() {}

    public boolean isStatusBarVisible() {
        return statusBarVisible;
    }

    public void setStatusBarVisible(boolean statusBarVisible) {
        this.statusBarVisible = statusBarVisible;
    }
}
