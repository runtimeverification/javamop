package javamop.output;

import javamop.parser.ast.MOPSpecFile;

public class Package {
    String packageString;
    
    public Package(MOPSpecFile mopSpecFile) {
        if (mopSpecFile.getPakage() != null) {
            packageString = mopSpecFile.getPakage().toString();
        } else {
            packageString = "";
        }
        packageString = packageString.trim();
    }
    
    public String toString(){
        String ret = "";
        
        ret += packageString + "\n";
        
        return ret;
    }
    
}
