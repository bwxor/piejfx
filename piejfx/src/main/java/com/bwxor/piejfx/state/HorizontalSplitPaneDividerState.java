package com.bwxor.piejfx.state;

public class HorizontalSplitPaneDividerState {
    private double maximizedPos = 0.2;
    private double normalPos = 0.25;

    public static final HorizontalSplitPaneDividerState instance = new HorizontalSplitPaneDividerState();

    private HorizontalSplitPaneDividerState() {
    }

    public double getMaximizedPos() {
        return maximizedPos;
    }

    public void setMaximizedPos(double maximizedPos) {
        this.maximizedPos = maximizedPos;
    }

    public double getNormalPos() {
        return normalPos;
    }

    public void setNormalPos(double normalPos) {
        this.normalPos = normalPos;
    }
}
