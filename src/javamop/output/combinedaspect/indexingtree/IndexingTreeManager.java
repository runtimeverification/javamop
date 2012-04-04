package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.output.EnableSet;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;

public class IndexingTreeManager {

	HashMap<JavaMOPSpec, IndexingDecl> trees = new HashMap<JavaMOPSpec, IndexingDecl>();
	
	public HashMap<String, RefTree> refTrees = new HashMap<String, RefTree>();

	public IndexingTreeManager(String name, List<JavaMOPSpec> specs, HashMap<JavaMOPSpec, MonitorSet> monitorSets, HashMap<JavaMOPSpec, SuffixMonitor> monitors,
			HashMap<JavaMOPSpec, EnableSet> enableSets) throws MOPException {
		getRefTrees(name, specs);
		
		for (JavaMOPSpec spec : specs) {
			MonitorSet monitorSet = monitorSets.get(spec);
			SuffixMonitor monitor = monitors.get(spec);
			EnableSet enableSet = enableSets.get(spec);

			trees.put(spec, new IndexingDecl(spec, monitorSet, monitor, enableSet, refTrees));
		}
		
		
	}
	
	protected void getRefTrees(String name, List<JavaMOPSpec> specs) throws MOPException {
		for (JavaMOPSpec spec : specs){
			for(MOPParameter param : spec.getParameters()){
				RefTree refTree = refTrees.get(param.getType().toString());
				
				if(refTree == null){
					refTree = new RefTree(name, param);

					refTrees.put(param.getType().toString(), refTree);
				}
				
				refTree.addProperty(spec);
			}
		}
	}
	
	public IndexingDecl getIndexingDecl(JavaMOPSpec spec) {
		return trees.get(spec);
	}

	public String decl() {
		String ret = "";

		if (trees.size() <= 0)
			return ret;

		ret += "// Declarations for Indexing Trees \n";
		for (IndexingDecl indexDecl : trees.values()) {
			ret += indexDecl;
		}
		ret += "\n";

		ret += "// Trees for References\n";
		for (RefTree refTree : refTrees.values()){
			ret += refTree;
		}
		ret += "\n";

		return ret;
	}

}
