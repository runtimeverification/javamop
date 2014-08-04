package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.JavaMOPMain;
import javamop.commandline.JavaMOPOptions;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * Manages the relationship between specifications and their statistics, and also generates
 * a global statistics aspect/class.
 */
public class MOPStatManager {
    
    private final HashMap<JavaMOPSpec, MOPStatistics> stats = 
        new HashMap<JavaMOPSpec, MOPStatistics>();
    
    private final MOPVariable statClass;
    private final MOPVariable statObject;
    private boolean statistics;
    private boolean statistics2;

    /**
     * Construct a statistics manager with a name over some specifications.
     * @param name The name to put on the statistics variables.
     * @param specs All the specifications to generate statistics for.
     */
    public MOPStatManager(final String name, final List<JavaMOPSpec> specs, JavaMOPOptions options) throws MOPException {
        for (JavaMOPSpec spec : specs) {
            stats.put(spec, new MOPStatistics(name, spec, statistics));
        }
        
        statClass = new MOPVariable(name + "_Statistics");
        statObject = new MOPVariable(name + "_StatisticsInstance");
        this.statistics = options.statistics;
        this.statistics2 = options.statistics2;
    }
    
    /**
     * Retrieve the statistics object for a particular specification.
     * @param spec The specification to find statistics for.
     * @return The statistics relevant to that specification.
     */
    public MOPStatistics getStat(final JavaMOPSpec spec){
        return stats.get(spec);
    }
    
    /**
     * Java code for a statistics class with some global statistics.
     * @return Global statistics class java source code.
     */
    public String statClass() {
        String ret = "";
        
        if (!statistics2)
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
    
    /**
     * Java code to increment the counter for total events accross all specifications.
     * @param spec The specification of the event that was triggered.
     * @param event The event that was triggered.
     * @return Java source to increment the global event counter.
     */
    public String incEvent(final JavaMOPSpec spec, final EventDefinition event){
        String ret = "";
        
        if (!statistics2)
            return ret;
        
        ret += statClass + ".numTotalEvents++;\n";
        
        return ret;
    }
    
    /**
     * Java code to increment the counter for total monitors created accross all specifications.
     * @param spec The specification for which a monitor was created.
     * @return Java code incrementing the global monitor counter.
     */
    public String incMonitor(final JavaMOPSpec spec){
        String ret = "";
        
        if (!statistics2)
            return ret;
        
        ret += statClass + ".numTotalMonitors++;\n";
        
        return ret;
    }
    
    /**
     * Supplemental field declarations for the global statistics.
     * @return Java code with field declarations.
     */
    public String fieldDecl2() {
        String ret = "";
        
        if (!statistics2)
            return ret;
        
        ret += "static " + statClass + " " + statObject + ";\n";
        
        return ret;
    }
    
    /**
     * Constructor code for the global statistics.
     * @return Java constructor code for the global statistics.
     */
    public String constructor() {
        String ret = "";
        
        if (!statistics2)
            return ret;
        
        ret += statObject + " = new " + statClass + "();\n";
        ret += "Runtime.getRuntime().addShutdownHook(" + statObject + ");\n";
        
        return ret;
    }
    
    /**
     * Field declarations for the global statistics.
     * @return Java code with field declarations.
     */
    public String fieldDecl() {
        String ret = "";
        
        if (!statistics)
            return ret;
        
        ret += "// Declarations for Statistics \n";
        for (MOPStatistics stat : stats.values()) {
            ret += stat.fieldDecl();
        }
        ret += "\n";
        
        return ret;
    }
    
    /**
     * AspectJ advice code for all of the managed statistics.
     * @return AspectJ code.
     */
    public String advice() {
        String ret = "";
        
        if (!statistics)
            return ret;
        
        ret += "\n";
        ret += "// advices for Statistics \n";
        for (MOPStatistics stat : stats.values()) {
            ret += stat.advice();
        }
        
        return ret;
    }
    
}
