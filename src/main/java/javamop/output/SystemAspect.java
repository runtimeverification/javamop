// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output;

/**
 * An aspect that is included with the generated code to keep track of the depth of the call stack.
 * It has thread-local storage to keep independent storage for each thread's callstack.
 */
class SystemAspect {
    private final String name;

    /**
     * Construct a SystemAspect with the given name.
     * @param name The name of the SystemAspect.
     */
    public SystemAspect(String name) {
        this.name = name + "SystemAspect";
    }
    
    /**
     * The name of the SystemAspect.
     * @return The name.
     */
    public String getSystemAspectName() {
        return name;
    }
    
    /**
     * The source code of the SystemAspect to include with the generated code.
     * @return The SystemAspect source code.
     */
    @Override
    public String toString() {
        String ret = "";
        
        ret += "aspect " + name + " implements javamoprt.MOPObject {\n";
        ret += "public static final ThreadLocal t_version = new ThreadLocal(){\n";
        ret += "protected Object initialValue() {\n";
        ret += "return new int[1000000];\n";
        ret += "}\n";
        ret += "};\n\n";
        
        ret += "public static final ThreadLocal t_global_depth = new ThreadLocal(){\n";
        ret += "protected Object initialValue() {\n";
        ret += "return new int[1];\n";
        ret += "}\n";
        ret += "};\n\n";


        ret += "pointcut sysbegin() : execution(* *(..)) && ";
        ret += "!within(javamoprt.MOPObject+) && !adviceexecution();\n";
        ret += "before () : sysbegin() {\n";
        ret += "((int[])t_version.get())[++((int[])t_global_depth.get())[0]]++;\n";
        ret += "}\n";
        ret += "}\n\n";

        ret += "aspect " + name + "2 implements javamoprt.MOPObject {\n";
        ret += "pointcut sysend() : execution(* *(..)) && ";
        ret += "!within(javamoprt.MOPObject+) && !adviceexecution();\n";
        ret += "after () : sysend() {\n";
        ret += "((int[])" + name + ".t_global_depth.get())[0]--;\n";
        ret += "}\n";
        
        ret += "}";
        
        return ret;
    }
}
