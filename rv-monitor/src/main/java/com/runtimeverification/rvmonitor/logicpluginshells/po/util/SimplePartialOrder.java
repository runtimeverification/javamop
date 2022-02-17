package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

public class SimplePartialOrder {
    private String before;
    private String after;

    public SimplePartialOrder(String before, String after) {
        this.setBefore(before);
        this.setAfter(after);
    }

    void setBefore(String before) {
        this.before = before;
    }

    String getBefore() {
        return before;
    }

    void setAfter(String after) {
        this.after = after;
    }

    String getAfter() {
        return after;
    }
}
