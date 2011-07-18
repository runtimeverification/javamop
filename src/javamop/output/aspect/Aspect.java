package javamop.output.aspect;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
import javamop.output.aspect.specialevent.EndProgram;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class Aspect {
	String name;
	HashMap<JavaMOPSpec, MonitorSet> monitorSets;
	HashMap<JavaMOPSpec, WrapperMonitor> monitors;
	HashMap<JavaMOPSpec, EnableSet> enableSets;

	ArrayList<AspectBody> aspectBodies = new ArrayList<AspectBody>();
	MOPVariable mapManager;
	boolean versionedStack;

	public Aspect(String name, MOPSpecFile mopSpecFile, HashMap<JavaMOPSpec, MonitorSet> monitorSets, HashMap<JavaMOPSpec, WrapperMonitor> monitors,
			HashMap<JavaMOPSpec, EnableSet> enableSets, boolean versionedStack) throws MOPException {
		this.name = name + "MonitorAspect";
		this.monitorSets = monitorSets;
		this.monitors = monitors;
		this.enableSets = enableSets;
		this.versionedStack = versionedStack;

		for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {
			aspectBodies.add(new AspectBody(name, mopSpec, monitorSets.get(mopSpec), monitors.get(mopSpec), enableSets.get(mopSpec)));
		}

		this.mapManager = new MOPVariable(name + "MapManager");
	}
	
	public String getAspectName(){
		return name;
	}

	public String toString() {
		String ret = "";

		ret += "public aspect " + this.name + " implements javamoprt.MOPObject {\n";

		ret += "javamoprt.MOPMapManager " + mapManager + ";\n";

		// constructor
		ret += "public " + this.name + "(){\n";
		for (AspectBody aspectBody : aspectBodies) {
			for (EndProgram endProgram : aspectBody.endProgramEvents) {
				ret += endProgram.printAddStatement();
			}
		}
		ret += mapManager + " = " + "new javamoprt.MOPMapManager();\n";
		ret += mapManager + ".start();\n";
		ret += "}\n";

		for (AspectBody aspectBody : aspectBodies) {
			ret += aspectBody;
		}

		ret += "}\n";

		return ret;
	}
}
