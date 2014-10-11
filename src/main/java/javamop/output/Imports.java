// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output;

import java.util.ArrayList;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;

/**
 * A list of import declarations.
 */
class Imports {
    private final ArrayList<String> imports;
    private final String[] required = {"java.util.concurrent.*", "java.util.concurrent.locks.*", 
        "java.util.*", "javamoprt.*", "java.lang.ref.*", "org.aspectj.lang.*" };
    
    /**
     * Extract the imports from a specification file.
     * @param mopSpecFile The specification to pull the imports from.
     */
    public Imports(MOPSpecFile mopSpecFile) {
        imports = new ArrayList<String>();
        
        for (ImportDeclaration imp : mopSpecFile.getImports()) {
            String n = "";
            if (imp.isStatic())
                n += "static ";
            n += imp.getName().toString().trim(); 
            if (imp.isAsterisk())
                n += ".*";
            
            if(!imports.contains(n))
                imports.add(n);
        }
        
        for (String req : required) {
            if(!imports.contains(req))
                imports.add(req);
        }
        
    }
    
    /**
     * The imports as Java source code.
     * @return A string with Java import statements.
     */
    @Override
    public String toString() {
        String ret = "";
        
        for (String imp : imports)
            ret += "import " + imp + ";\n";
        
        return ret;
    }
    
}
