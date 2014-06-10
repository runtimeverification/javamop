package javamop.output.combinedaspect;

import java.util.List;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.EventManager;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class CombinedAspect {
    private String name;
    private MOPVariable mapManager;
    private boolean versionedStack;
    
    private List<JavaMOPSpec> specs;
    public MOPStatManager statManager;
    public LockManager lockManager;
    public EventManager eventManager;
    
    public CombinedAspect(String name, MOPSpecFile mopSpecFile, boolean versionedStack) throws MOPException {
        this.name = name + "MonitorAspect";
        this.versionedStack = versionedStack;
        
        this.specs = mopSpecFile.getSpecs();
        this.statManager = new MOPStatManager(name, this.specs);
        this.lockManager = new LockManager(name, this.specs);
        
        
        this.eventManager = new EventManager(name, this.specs, this);
        
        this.mapManager = new MOPVariable(name + "MapManager");
    }
    
    public String getAspectName() {
        return name;
    }
    
    public String getFileName() {
        return name.substring(0, name.length() - "MonitorAspect".length());
    }
    
    
    public String toString() {
        String ret = "";
        
        ret += this.statManager.statClass();
        
        ret += "public aspect " + this.name + " implements javamoprt.MOPObject {\n";
        
        ret += "javamoprt.map.MOPMapManager " + mapManager + ";\n";
        
        ret += this.statManager.fieldDecl2();
        
        // constructor
        ret += "public " + this.name + "(){\n";
        
        ret += this.eventManager.printConstructor();
        
        ret += mapManager + " = " + "new javamoprt.map.MOPMapManager();\n";
        ret += mapManager + ".start();\n";
        
        ret += this.statManager.constructor();
        
        //ret += constructor();
        //ret += initCache();
        
        ret += "}\n";
        ret += "\n";
        
        ret += this.statManager.fieldDecl();
        
        ret += this.lockManager.decl();
        
        ret += this.eventManager.advices();
        
        ret += this.statManager.advice();
        
        ret += "}\n";
        
        return ret;
    }
    
    public String toRVString() {
        String ret = "";
        ret += this.statManager.statClass();
        ret += "public aspect " + this.name +
        " implements com.runtimeverification.rvmonitor.java.rt.RVMObject {\n";
        
        // Constructor
        ret += "public " + this.name + "(){\n";
        
        ret += this.eventManager.printConstructor();
        
        //ret += mapManager + " = " + "new javamoprt.map.MOPMapManager();\n";
        //ret += mapManager + ".start();\n";
        
        //ret += this.statManager.constructor();
        
        //ret += constructor();
        //ret += initCache();
        
        ret += "}\n";
        ret += "\n";
        
        
        ret += this.lockManager.decl();
        
        ret += this.eventManager.advices();
        
        
        ret += "}\n";
        return ret;
    }
}
