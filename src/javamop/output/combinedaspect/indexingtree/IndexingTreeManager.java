package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
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

		if (Main.combinedindexing)
			combineIndexingTrees(name);

	}

	protected void combineIndexingTrees(String name) throws MOPException {
		HashMap<MOPParameterTypes, ArrayList<IndexingTree>> typeToTree = new HashMap<MOPParameterTypes, ArrayList<IndexingTree>>();
		HashMap<MOPParameterTypes, ArrayList<IndexingTree>> typeToTree_perthread = new HashMap<MOPParameterTypes, ArrayList<IndexingTree>>();

		for (IndexingDecl indexDecl : trees.values()) {
			for (IndexingTree tree : indexDecl.indexingTrees.values()) {
				if (tree.queryTypes.size() == 0)
					continue;

				if (tree.perthread) {
					ArrayList<IndexingTree> list = typeToTree_perthread.get(tree.queryTypes);

					if (list == null) {
						list = new ArrayList<IndexingTree>();
						typeToTree_perthread.put(tree.queryTypes, list);
					}

					list.add(tree);
				} else {
					ArrayList<IndexingTree> list = typeToTree.get(tree.queryTypes);

					if (list == null) {
						list = new ArrayList<IndexingTree>();
						typeToTree.put(tree.queryTypes, list);
					}

					list.add(tree);
				}
			}
		}

		for (Iterator<Map.Entry<MOPParameterTypes, ArrayList<IndexingTree>>> iter = typeToTree.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<MOPParameterTypes, ArrayList<IndexingTree>> entry = iter.next();

			ArrayList<IndexingTree> list = entry.getValue();

			if (list.size() < 2) {
				iter.remove();
				continue;
			}

			Collections.sort(list, new Comparator<IndexingTree>() {
				public int compare(IndexingTree t1, IndexingTree t2) {
					return t1.getName().toString().compareTo(t2.getName().toString());
				}
			});

			MOPVariable combinedTreeName = new MOPVariable(name + "_Index_" + entry.getKey() + "_Map");
			IndexingTree combinedIndexingTree = new CentralizedMultiIndexingTree(combinedTreeName, entry.getKey(), list, false);

			for (int i = 0; i < list.size(); i++) {
				IndexingTree tree = list.get(i);

				tree.combinedIndexingTree = combinedIndexingTree;
				tree.index_id = i;
			}
		}

		for (Iterator<Map.Entry<MOPParameterTypes, ArrayList<IndexingTree>>> iter = typeToTree_perthread.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<MOPParameterTypes, ArrayList<IndexingTree>> entry = iter.next();

			ArrayList<IndexingTree> list = entry.getValue();

			if (list.size() < 2) {
				iter.remove();
				continue;
			}

			Collections.sort(list, new Comparator<IndexingTree>() {
				public int compare(IndexingTree t1, IndexingTree t2) {
					return t1.getName().toString().compareTo(t2.getName().toString());
				}
			});

			MOPVariable combinedTreeName = new MOPVariable(name + "_PerthreadIndex_" + entry.getKey() + "_Map");
			IndexingTree combinedIndexingTree = new CentralizedMultiIndexingTree(combinedTreeName, entry.getKey(), list, true);

			for (int i = 0; i < list.size(); i++) {
				IndexingTree tree = list.get(i);

				tree.combinedIndexingTree = combinedIndexingTree;
				tree.index_id = i;
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

		return ret;
	}

}
