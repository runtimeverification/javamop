package javamop.output;

import javamop.MOPException;
import javamop.commandline.JavaMOPOptions;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

/**
 * The top-level generated AspectJ code.
 */
public class AspectJCode {
    private final String name;
    
    private final Package packageDecl;
    private final Imports imports;
    private final CombinedAspect aspect;
    private boolean versionedStack = false;
    private final SystemAspect systemAspect;
    private boolean statistics;
    
    /**
     * Construct the AspectJ code.
     * @param name The name of the aspect.
     * @param mopSpecFile The specification file that will be used to build aspects.
     * @throw MOPException If something goes wrong in generating the aspects.
     */
    public AspectJCode(String name, MOPSpecFile mopSpecFile, JavaMOPOptions options) throws MOPException {
        this.name = name;
        packageDecl = new Package(mopSpecFile);
        imports = new Imports(mopSpecFile);
        this.statistics = options.statistics;
        
        for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {
            
            for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
                versionedStack |= prop.getVersionedStack();
            }
        }
        
        aspect = new CombinedAspect(name, mopSpecFile, versionedStack, options);
        
        if(versionedStack) {
            systemAspect = new SystemAspect(name, options.dacapo);
        } else {
            systemAspect = null;
        }
    }
    
    /**
     * Generate the AspectJ code that complements the generated RV-Monitor monitoring code.
     * @return The AspectJ/Java source code.
     */
    public String toRVString() {
        String ret = "";
        ret += packageDecl;
        ret += imports.toString().replaceAll("import javamoprt.*", "");
        
        ret += "\n";
        
        // The order of these two is really important.
        if(systemAspect != null){
            ret += "aspect " + name + "OrderAspect {\n";
            ret += "declare precedence : ";
            ret += systemAspect.getSystemAspectName() + ""; 
            ret += ", ";
            ret += systemAspect.getSystemAspectName() + "2";
            ret += ", ";
            ret += aspect.getAspectName();
            ret += ";\n";
            
            ret += "}\n";
            ret += "\n";
        }
        
        ret += aspect.toRVString();
        
        if(systemAspect != null)
            ret += "\n" + systemAspect;
        
        return ret;
    }
}
