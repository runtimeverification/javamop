// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

public class SpecModifierSet {
    
    /* Definitions of the bits in the modifiers field. */
    public static final int UNSYNC = 0x0001;
    
    public static final int DECENTRL = 0x0002;
    
    public static final int PERTHREAD = 0x0004;
    
    public static final int SUFFIX = 0x0008;
    
    public static final int FULLBINDING = 0x0010;
    
    public static final int CONNECTED = 0x0020;
    
    public static final int AVOID = 0x0040;
    
    public static final int ENFORCE = 0x0080;
    
    /**
     * A set of accessors that indicate whether the specified modifier is in the
     * set.
     */
    public static boolean isUnSync(int modifiers) {
        return (modifiers & UNSYNC) != 0;
    }
    
    public static boolean isDecentralized(int modifiers) {
        return (modifiers & DECENTRL) != 0;
    }
    
    public static boolean isPerThread(int modifiers) {
        return (modifiers & PERTHREAD) != 0;
    }
    
    public static boolean isSuffix(int modifiers) {
        return (modifiers & SUFFIX) != 0;
    }
    
    public static boolean isFullBinding(int modifiers) {
        return (modifiers & FULLBINDING) != 0;
    }
    
    public static boolean isConnected(int modifiers) {
        return (modifiers & CONNECTED) != 0;
    }
    
    public static boolean isAvoid(int modifiers) {
        return (modifiers & AVOID) != 0;
    }
    
    public static boolean isEnforce(int modifiers) {
        return (modifiers & ENFORCE) != 0;
    }
    
}
