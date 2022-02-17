package com.runtimeverification.rvmonitor.java.rvj.output;

public class UserJavaCode {
    private final String code;

    public UserJavaCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        String ret = "";

        if (code != null)
            ret += code;

        return ret;
    }
}
