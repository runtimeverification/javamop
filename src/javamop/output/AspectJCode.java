package javamop.output;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class AspectJCode {
	String name;
	
	Package packageDecl;
	Imports imports;
	HashMap<JavaMOPSpec, MonitorSet> monitorSets = new HashMap<JavaMOPSpec, MonitorSet>();
	HashMap<JavaMOPSpec, WrapperMonitor> monitors = new HashMap<JavaMOPSpec, WrapperMonitor>();
	//Aspect aspect;
	CombinedAspect aspect;
	HashMap<JavaMOPSpec, EnableSet> enableSets = new HashMap<JavaMOPSpec, EnableSet>();
	HashMap<JavaMOPSpec, CoEnableSet> coenableSets = new HashMap<JavaMOPSpec, CoEnableSet>();
	boolean versionedStack = false;
	SystemAspect systemAspect;
	
	public AspectJCode(String name, MOPSpecFile mopSpecFile) throws MOPException {
		this.name = name;
		packageDecl = new Package(mopSpecFile);
		imports = new Imports(mopSpecFile);

		for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {
			EnableSet enableSet = new EnableSet(mopSpec.getEvents(), mopSpec.getParameters());
			CoEnableSet coenableSet = new CoEnableSet(mopSpec.getEvents(), mopSpec.getParameters());

			for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
				enableSet.add(new EnableSet(prop, mopSpec.getEvents(), mopSpec.getParameters()));
				coenableSet.add(new CoEnableSet(prop, mopSpec.getEvents(), mopSpec.getParameters()));
				versionedStack |= prop.getVersionedStack();
			}

			OptimizedCoenableSet optimizedCoenableSet = new OptimizedCoenableSet(coenableSet);

			enableSets.put(mopSpec, enableSet);
			coenableSets.put(mopSpec, optimizedCoenableSet);

			WrapperMonitor monitor = new WrapperMonitor(name, mopSpec, optimizedCoenableSet, true);

			monitors.put(mopSpec, monitor);

			if(mopSpec.isGeneral())
				monitorSets.put(mopSpec, new MonitorSet(name, mopSpec, monitor, true));
			else
				monitorSets.put(mopSpec, new MonitorSet(name, mopSpec, monitor, false));

		}

		//aspect = new Aspect(name, mopSpecFile, monitorSets, monitors, enableSets, versionedStack);
		aspect = new CombinedAspect(name, mopSpecFile, monitorSets, monitors, enableSets, versionedStack);
		
		if(versionedStack)
			systemAspect = new SystemAspect(name); 
	}

	public String toString() {
		String ret = "";

		ret += packageDecl;
		ret += imports;
		ret += "\n";

		for (MonitorSet monitorSet : this.monitorSets.values())
			ret += monitorSet;
		ret += "\n";

		for (WrapperMonitor monitor : this.monitors.values())
			ret += monitor;
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
		
		ret += aspect;

		if(systemAspect != null)
			ret += "\n" + systemAspect;
		
		return ret;
	}
}
