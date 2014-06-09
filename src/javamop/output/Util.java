package javamop.output;

import javamop.parser.ast.PackageDeclaration;

public class Util {
    
    public static String packageAndNameToUrl(PackageDeclaration packageDeclaration, String name){
        return "http://fsl.cs.uiuc.edu/annotated-java/__properties/html/" +
        packageToUrlFragment(packageDeclaration) + "/" + name + ".html"; 
    }
    
    public static String packageToUrlFragment(PackageDeclaration packageDeclaration){
        if(packageDeclaration == null) return "";
        return packageDeclaration.toString().replaceAll("[.]","/").replaceAll("(package\\s*)|;|\\s*","");
    }
}
