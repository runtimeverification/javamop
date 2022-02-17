package com.runtimeverification.rvmonitor.java.rvj.output;

public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = -1416906081090623451L;

    public NotImplementedException() {
        super("the required feature has not been implemented");
    }

    public NotImplementedException(String reason) {
        super(reason);
    }
}
