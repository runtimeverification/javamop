package javamop.output.combinedaspect;

import java.util.List;
import java.util.TreeMap;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class ActivatorManager {
    
    private List<JavaMOPSpec> specs;
    private TreeMap<JavaMOPSpec, MOPVariable> activators = new TreeMap<JavaMOPSpec, MOPVariable>();
    
    public ActivatorManager(String name, List<JavaMOPSpec> specs) {
        this.specs = specs;
        for (JavaMOPSpec spec : specs) {
            activators.put(spec, new MOPVariable(spec.getName() + "_activated"));
        }
    }
    
    public MOPVariable getActivator(JavaMOPSpec spec) {
        return activators.get(spec);
    }
    
    public String decl() {
        String ret = "";
        
        for (MOPVariable activator : activators.values()) {
            ret += "static boolean " + activator + " = false;\n";
        }
        
        if (activators.size() > 0)
            ret += "\n";
        
        return ret;
    }
    
    public String reset() {
        String ret = "";
        
        for (MOPVariable activator : activators.values()) {
            ret += activator + " = false;\n";
        }
        
        if (activators.size() > 0)
            ret += "\n";
        
        return ret;
    }
    
}
