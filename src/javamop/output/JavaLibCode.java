// Handle the creation of java code to be used as a library.
// Mostly modified from AspectJCode.java

package javamop.output;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class JavaLibCode {
	Package packageDecl;
	Imports imports;
	HashMap<JavaMOPSpec, MonitorSet> monitorSets = new HashMap<JavaMOPSpec, MonitorSet>();
	HashMap<JavaMOPSpec, WrapperMonitor> monitors = new HashMap<JavaMOPSpec, WrapperMonitor>();
	HashMap<JavaMOPSpec, EnableSet> enableSets = new HashMap<JavaMOPSpec, EnableSet>();
	HashMap<JavaMOPSpec, CoEnableSet> coenableSets = new HashMap<JavaMOPSpec, CoEnableSet>();

	public JavaLibCode(String name, MOPSpecFile mopSpecFile) throws MOPException {
		packageDecl = new Package(mopSpecFile);
		imports = new Imports(mopSpecFile);

		for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {
			EnableSet enableSet = new EnableSet(mopSpec.getEvents(), mopSpec.getParameters());
			CoEnableSet coenableSet = new CoEnableSet(mopSpec.getEvents(), mopSpec.getParameters());

			for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
				enableSet.add(new EnableSet(prop, mopSpec.getEvents(), mopSpec.getParameters()));
				coenableSet.add(new CoEnableSet(prop, mopSpec.getEvents(), mopSpec.getParameters()));
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

		return ret;
	}
}
