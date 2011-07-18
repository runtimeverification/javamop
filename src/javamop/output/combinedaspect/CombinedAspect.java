package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.EventManager;
import javamop.output.combinedaspect.indexingtree.IndexingTreeManager;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class CombinedAspect {
	String name;
	public HashMap<JavaMOPSpec, MonitorSet> monitorSets;
	public HashMap<JavaMOPSpec, WrapperMonitor> monitors;
	public HashMap<JavaMOPSpec, EnableSet> enableSets;

	MOPVariable mapManager;
	boolean versionedStack;

	List<JavaMOPSpec> specs;
	public MOPStatManager statManager;
	public LockManager lockManager;
	public TimestampManager timestampManager;
	public IndexingTreeManager indexingTreeManager;
	public EventManager eventManager;

	public CombinedAspect(String name, MOPSpecFile mopSpecFile, HashMap<JavaMOPSpec, MonitorSet> monitorSets, HashMap<JavaMOPSpec, WrapperMonitor> monitors,
			HashMap<JavaMOPSpec, EnableSet> enableSets, boolean versionedStack) throws MOPException {
		this.name = name + "MonitorAspect";
		this.monitorSets = monitorSets;
		this.monitors = monitors;
		this.enableSets = enableSets;
		this.versionedStack = versionedStack;

		this.specs = mopSpecFile.getSpecs();
		this.statManager = new MOPStatManager(name, this.specs);
		this.lockManager = new LockManager(name, this.specs);
		this.timestampManager = new TimestampManager(name, this.specs);
		this.indexingTreeManager = new IndexingTreeManager(name, this.specs, this.monitorSets, this.monitors, this.enableSets);
		this.eventManager = new EventManager(name, this.specs, this);

		this.mapManager = new MOPVariable(name + "MapManager");
	}

	public String getAspectName() {
		return name;
	}

	public String toString() {
		String ret = "";

		ret += "public aspect " + this.name + " implements javamoprt.MOPObject {\n";

		ret += "javamoprt.MOPMapManager " + mapManager + ";\n";

		// constructor
		ret += "public " + this.name + "(){\n";

		ret += this.eventManager.printConstructor();
		
		ret += mapManager + " = " + "new javamoprt.MOPMapManager();\n";
		ret += mapManager + ".start();\n";
		ret += "}\n";
		ret += "\n";

		ret += this.statManager.fieldDecl();

		ret += this.lockManager.decl();

		ret += this.timestampManager.decl();

		ret += this.indexingTreeManager.decl();

		ret += this.eventManager.advices();

		ret += this.statManager.advice();

		ret += "}\n";

		return ret;
	}
}
