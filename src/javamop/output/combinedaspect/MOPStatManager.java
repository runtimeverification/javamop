package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.JavaMOPMain;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class MOPStatManager {
    
    private final HashMap<JavaMOPSpec, MOPStatistics> stats = 
        new HashMap<JavaMOPSpec, MOPStatistics>();
    
    private final MOPVariable statClass;
    private final MOPVariable statObject;
    
    public MOPStatManager(String name, List<JavaMOPSpec> specs) throws MOPException {
        for (JavaMOPSpec spec : specs) {
            stats.put(spec, new MOPStatistics(name, spec));
        }
        
        
        statClass = new MOPVariable(name + "_Statistics");
        statObject = new MOPVariable(name + "_StatisticsInstance");
    }
    
    public MOPStatistics getStat(JavaMOPSpec spec){
        return stats.get(spec);
    }
    
    public String statClass() {
        String ret = "";
        
        if (!JavaMOPMain.statistics2)
            return ret;
        
        ret = "class " + statClass + " extends Thread implements javamoprt.MOPObject {\n";
        
        ret += "static public long numTotalEvents = 0;\n"; 
        ret += "static public long numTotalMonitors = 0;\n"; 
        
        ret += "public void run() {\n";
        {
            ret += "System.err.println(\"# of total events: \" + " + statClass + 
                ".numTotalEvents);\n";
            ret += "System.err.println(\"# of total monitors: \" + " + statClass + 
                ".numTotalMonitors);\n";
        }
        ret += "}\n";
        
        ret += "}\n";
        
        return ret;
    }
    
    public String incEvent(JavaMOPSpec spec, EventDefinition event){
        String ret = "";
        
        if (!JavaMOPMain.statistics2)
            return ret;
        
        ret += statClass + ".numTotalEvents++;\n";
        
        return ret;
    }
    
    public String incMonitor(JavaMOPSpec spec){
        String ret = "";
        
        if (!JavaMOPMain.statistics2)
            return ret;
        
        ret += statClass + ".numTotalMonitors++;\n";
        
        return ret;
    }
    
    public String fieldDecl2() {
        String ret = "";
        
        if (!JavaMOPMain.statistics2)
            return ret;
        
        ret += "static " + statClass + " " + statObject + ";\n";
        
        return ret;
    }
    
    public String constructor() {
        String ret = "";
        
        if (!JavaMOPMain.statistics2)
            return ret;
        
        ret += statObject + " = new " + statClass + "();\n";
        ret += "Runtime.getRuntime().addShutdownHook(" + statObject + ");\n";
        
        return ret;
    }
    
    
    
    public String fieldDecl() {
        String ret = "";
        
        if (!JavaMOPMain.statistics)
            return ret;
        
        ret += "// Declarations for Statistics \n";
        for (MOPStatistics stat : stats.values()) {
            ret += stat.fieldDecl();
        }
        ret += "\n";
        
        return ret;
    }
    
    public String advice() {
        String ret = "";
        
        if (!JavaMOPMain.statistics)
            return ret;
        
        ret += "\n";
        ret += "// advices for Statistics \n";
        for (MOPStatistics stat : stats.values()) {
            ret += stat.advice();
        }
        
        return ret;
    }
    
}
