package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.output.EnableSet;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class IndexingTreeManager {

	HashMap<JavaMOPSpec, IndexingDecl> trees = new HashMap<JavaMOPSpec, IndexingDecl>();

	public IndexingTreeManager(String name, List<JavaMOPSpec> specs, HashMap<JavaMOPSpec, MonitorSet> monitorSets, HashMap<JavaMOPSpec, WrapperMonitor> monitors,
			HashMap<JavaMOPSpec, EnableSet> enableSets) throws MOPException {
		for (JavaMOPSpec spec : specs) {
			MonitorSet monitorSet = monitorSets.get(spec);
			WrapperMonitor monitor = monitors.get(spec);
			EnableSet enableSet = enableSets.get(spec);

			trees.put(spec, new IndexingDecl(spec, monitorSet, monitor, enableSet));
		}
	}
	
	public IndexingDecl getIndexingDecl(JavaMOPSpec spec){
		return trees.get(spec);
	}

	public String decl() {
		String ret = "";

		if (trees.size() <= 0)
			return ret;

		ret += "// Declarations for Indexing Trees \n";
		for (IndexingDecl tree : trees.values()) {
			ret += tree;
		}
		ret += "\n";

		return ret;
	}

}
