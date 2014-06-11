package javamop.output;

import javamop.MOPException;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class AspectJCode {
    private final String name;
    
    private final Package packageDecl;
    private final Imports imports;
    //Aspect aspect;
    private final CombinedAspect aspect;
    private boolean versionedStack = false;
    private SystemAspect systemAspect;
    
    public AspectJCode(String name, MOPSpecFile mopSpecFile) throws MOPException {
        this.name = name;
        packageDecl = new Package(mopSpecFile);
        imports = new Imports(mopSpecFile);
        
        for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {
            
            for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
                versionedStack |= prop.getVersionedStack();
            }
        }
        
        //aspect = new Aspect(name, mopSpecFile, monitorSets, monitors, enableSets, versionedStack);
        aspect = new CombinedAspect(name, mopSpecFile, versionedStack);
        
        if(versionedStack)
            systemAspect = new SystemAspect(name); 
    }
    
    /*
     * 
     * Generate RV aspect
     * 
     * */
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
