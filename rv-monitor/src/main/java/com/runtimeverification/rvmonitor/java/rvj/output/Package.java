package com.runtimeverification.rvmonitor.java.rvj.output;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;

public class Package {
    private String packageString;

    public Package(RVMSpecFile rvmSpecFile) {
        if (rvmSpecFile.getPakage() != null) {
            packageString = rvmSpecFile.getPakage().toString();
        } else {
            packageString = "";
        }
        packageString = packageString.trim();
    }

    @Override
    public String toString() {
        return packageString + "\n";
    }
}
