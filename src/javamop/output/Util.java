package javamop.output;

import javamop.parser.ast.PackageDeclaration;

/**
 * Utility methods for the generated code output.
 */
public final class Util {
    
    /**
     * Private to prevent instantiation.
     */
    private Util() {
        
    }
    
    /**
     * Convert a property with a given package and name into a URL that should have something
     * describing the property.
     * @param packageDeclaration The package the property is in.
     * @param name The name of the property.
     * @return A URL which should point to documentation of the property.
     */
    public static String packageAndNameToUrl(final PackageDeclaration packageDeclaration, 
            final String name) {
        return "http://runtimeverification.com/monitor/annotated-java/__properties/html/" +
        packageToUrlFragment(packageDeclaration) + "/" + name + ".html"; 
    }
    
    /**
     * Convert a package declaration to be part of a valid URL.
     * @param packageDeclaration The package.
     * @return Part of a URL.
     */
    public static String packageToUrlFragment(final PackageDeclaration packageDeclaration) {
        if(packageDeclaration == null) return "";
        return packageDeclaration.toString().replaceAll("[.]","/").replaceAll(
            "(package\\s*)|;|\\s*","");
    }
}
