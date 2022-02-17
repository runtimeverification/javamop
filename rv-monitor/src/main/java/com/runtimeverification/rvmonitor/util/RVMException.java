/*
 * Created on Aug 17, 2003
 */
package com.runtimeverification.rvmonitor.util;

/**
 * @author fengchen
 */
public class RVMException extends Exception {
    private static final long serialVersionUID = 2145299315023315212L;

    public RVMException(Exception e) {
        super("RV Monitor Expection:" + e.getMessage(), e);
    }

    public RVMException(String str) {
        super(str);
    }

    public RVMException(String str, Exception e) {
        super(str, e);
    }
}
