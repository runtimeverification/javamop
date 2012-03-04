package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class CentralizedMultiIndexingTree extends IndexingTree {
	boolean declPrinted = false;

	ArrayList<IndexingTree> trees;

	MultiIndexingCache cache = null;

	MOPVariable objs = new MOPVariable("objs");

	public CentralizedMultiIndexingTree(MOPVariable name, MOPParameterTypes queryTypes, ArrayList<IndexingTree> trees, boolean perthread) throws MOPException {
		super(name.toString(), null, null, null, null, null, perthread);

		this.name = name;

		this.trees = trees;

		this.queryTypes = queryTypes;

		if (queryTypes.size() != 0)
			this.cache = new MultiIndexingCache(this.name, queryTypes, perthread);
	}

	protected void bringParameters() {
		this.queryParam = trees.get(current_index_id).queryParam;
		this.fullParam = trees.get(current_index_id).fullParam;
		this.monitorSet = trees.get(current_index_id).monitorSet;
		this.monitor = trees.get(current_index_id).monitor;

		if (queryParam.equals(fullParam))
			isFullParam = true;
		else
			isFullParam = false;
	}

	public String addMonitor(MOPVariable map, MOPVariable obj, MOPVariable monitors, HashMap<String, MOPVariable> mopRefs, MOPVariable monitor) {
		String ret = "";

		bringParameters();

		if (queryParam.equals(fullParam))
			return ret;

		ret += map + " = " + retrieveTree() + ";\n";

		for (int i = 0; i < queryParam.size(); i++) {
			MOPParameter p = queryParam.get_lexicographic(i);

			if (i != 0) {
				ret += "if (" + obj + " == null) {\n";

				if (i == queryParam.size() - 1) {
					ret += obj + " = new javamoprt.MOPMultiMapNode(" + printMultiSignature(i) + ");\n";
					ret += map + ".put(";
					ret += monitor + "." + mopRefs.get(queryParam.get_lexicographic(i - 1).getName()) + ", " + obj + ");\n";
					ret += "}\n";
				} else {
					ret += obj + " = new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(i) + ");\n";
					ret += map + ".put(";
					ret += monitor + "." + mopRefs.get(queryParam.get_lexicographic(i - 1).getName()) + ", " + obj + ");\n";
					ret += "}\n";
				}

				ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
			}

			if (i < queryParam.size() - 1) {
				ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
			} else {
				ret += obj + " = " + map + ".get(" + p.getName() + ", " + current_index_id + ");\n";
			}
		}

		ret += monitors + " = ";
		ret += "(" + monitorSet.getName() + ")" + obj + ";\n";
		ret += "if (" + monitors + " == null) {\n";
		ret += monitors + " = new " + monitorSet.getName() + "();\n";
		ret += map + ".put(";
		ret += monitor + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName());
		ret += ", " + monitors + ", " + current_index_id + ");\n";
		ret += "}\n";
		ret += monitorSet.addMonitor(monitors.toString(), monitor);

		return ret;
	}

	public String getWeakReferenceAfterLookup(MOPVariable map, MOPVariable monitorVar, HashMap<String, MOPVariable> mopRefs) {
		String ret = "";

		bringParameters();

		if (queryParam.size() == 1) {
			ret += monitorVar + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + " = " + retrieveTree() + ".cachedKey;\n";
		} else {
			ret += monitorVar + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + " = " + map + ".cachedKey;\n";
		}
		
		return ret;
	}
	
	public String addMonitorAfterLookup(MOPVariable map, MOPVariable monitorVar, HashMap<String, MOPVariable> mopRefs) {
		String ret = "";

		bringParameters();

		if (queryParam.size() == 1) {
			ret += retrieveTree() + ".put(" + monitorVar + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + ", " + monitorVar + ", "
					+ current_index_id + ");\n";
		} else {
			ret += map + ".put(" + monitorVar + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + ", " + monitorVar + ", " + current_index_id
					+ ");\n";
		}

		return ret;
	}

	public String addExactWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs) {
		String ret = "";

		bringParameters();

		if (isFullParam) {
			if (queryParam.size() == 1) {
				ret += retrieveTree() + ".put(" + wrapper + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + ", " + wrapper + ", "
						+ current_index_id + ");\n";
			} else {
				ret += lastMap + ".put(" + wrapper + "." + mopRefs.get(queryParam.get_lexicographic(queryParam.size() - 1).getName()) + ", " + wrapper + ", " + current_index_id
						+ ");\n";
			}
		} else {
			if (queryParam.size() == 1) {
				ret += monitorSet.setNode(set.toString(), wrapper);
			} else {
				ret += monitorSet.setNode(set.toString(), wrapper);
			}
		}

		return ret;
	}

	public String addWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs) {
		String ret = "";

		bringParameters();

		if (queryParam.equals(fullParam))
			return ret;

		ret += monitorSet.addMonitor(set.toString(), wrapper);

		return ret;
	}

	public String lookup(MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs, boolean creative) {
		String ret = "";

		bringParameters();

		ret += "Object[] " + objs + ";\n";
		if (creative) {
			for (int i = 0; i < queryParam.size(); i++) {
				MOPParameter p = queryParam.get_lexicographic(i);

				if (i == queryParam.size() - 1) {
					if (i == 0) {
						ret += objs + " = " + retrieveTree() + ".getAll(" + p.getName() + ");\n";
						ret += "if(" + objs + " != null){\n";
						ret += obj + " = " + objs + "[" + current_index_id + "];\n";
						ret += "} else {\n";
						ret += obj + " = null;\n";
						ret += "}\n";
					} else {
						ret += objs + " = " + map + ".getAll(" + p.getName() + ");\n";
						ret += "if(" + objs + " != null){\n";
						ret += obj + " = " + objs + "[" + current_index_id + "];\n";
						ret += "} else {\n";
						ret += obj + " = null;\n";
						ret += "}\n";
					}
				} else if (i < queryParam.size() - 1) {
					if (i == 0) {
						ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
					} else {
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
					}

					ret += "if (" + obj + " == null) {\n";

					if (i == queryParam.size() - 2)
						ret += obj + " = new javamoprt.MOPMultiMapNode(" + printMultiSignature(i + 1) + ");\n";
					else
						ret += obj + " = new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(i + 1) + ");\n";

					ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";

					if (i == 0)
						ret += retrieveTree() + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";
					else
						ret += map + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";

					ret += "} else {\n";
					if (i == 0)
						ret += tempRefs.get(p.getName()) + " = " + retrieveTree() + ".cachedKey;\n";
					else
						ret += tempRefs.get(p.getName()) + " = " + map + ".cachedKey;\n";
					ret += "}\n";

					ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
				}
			}
		} else {
			for (int i = 0; i < queryParam.size(); i++) {
				MOPParameter p = queryParam.get_lexicographic(i);

				if (i == queryParam.size() - 1) {
					if (i == 0) {
						ret += objs + " = " + retrieveTree() + ".getAll(" + p.getName() + ");\n";
						ret += "if(" + objs + " != null){\n";
						ret += obj + " = " + objs + "[" + current_index_id + "];\n";
						ret += "} else {\n";
						ret += obj + " = null;\n";
						ret += "}\n";
					} else {
						ret += "if (" + obj + " != null) {\n";
						ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
						ret += objs + " = " + map + ".getAll(" + p.getName() + ");\n";
						ret += "if(" + objs + " != null){\n";
						ret += obj + " = " + objs + "[" + current_index_id + "];\n";
						ret += "} else {\n";
						ret += obj + " = null;\n";
						ret += "}";
						ret += "}\n";
					}
				} else {
					if (i == 0) {
						ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
					} else {
						ret += "if (" + obj + " != null) {\n";
						ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						ret += "}\n";
					}
				}
			}
		}

		return ret;
	}

	protected String lookupExactMonitorFullParam(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs) {
		String ret = "";

		for (int i = 0; i < queryParam.size(); i++) {
			MOPParameter p = queryParam.get_lexicographic(i);

			if (i == queryParam.size() - 1) {
				if (i == 0) {
					ret += objs + " = " + retrieveTree() + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += wrapper + " = null;\n";
					ret += "}\n";
				} else {
					ret += objs + " = " + lastMap + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += wrapper + " = null;\n";
					ret += "}\n";

				}
			} else if (i < queryParam.size() - 1) {
				if (i == 0) {
					ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
				} else {
					ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
				}

				ret += "if (" + obj + " == null) {\n";

				if (i == queryParam.size() - 2)
					ret += obj + " = new javamoprt.MOPMultiMapNode(" + printMultiSignature(i + 1) + ");\n";
				else
					ret += obj + " = new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(i + 1) + ");\n";

				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
				ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
				ret += "}\n";

				if (i == 0)
					ret += retrieveTree() + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";
				else
					ret += map + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";

				ret += "} else {\n";
				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";

				if (i == 0)
					ret += tempRefs.get(p.getName()) + " = " + retrieveTree() + ".cachedKey;\n";
				else
					ret += tempRefs.get(p.getName()) + " = " + map + ".cachedKey;\n";

				ret += "}\n";

				ret += "}\n";

				if (i == queryParam.size() - 2) {
					ret += lastMap + " = (javamoprt.MOPMap)" + obj + ";\n";
				} else {
					ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
				}
			}
		}

		return ret;
	}

	protected String lookupExactMonitorPartialParam(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj,
			HashMap<String, MOPVariable> tempRefs) {
		String ret = "";

		for (int i = 0; i < queryParam.size(); i++) {
			MOPParameter p = queryParam.get_lexicographic(i);

			if (i == queryParam.size() - 1) {
				if (i == 0) {
					ret += objs + " = " + retrieveTree() + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += set + " = " + "(" + monitorSet.getName() + ")" + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += set + " = null;\n";
					ret += "}\n";
				} else {
					ret += objs + " = " + lastMap + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += set + " = " + "(" + monitorSet.getName() + ")" + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += set + " = null;\n";
					ret += "}\n";
				}

				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "} else {\n";
				ret += set + " = new " + monitorSet.getName() + "();\n";

				
				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
				if (i == 0) {
					ret += "if (" + retrieveTree() + ".cachedKey != null){\n";
					ret += tempRefs.get(p.getName()) + " = " + retrieveTree() + ".cachedKey;\n";
				} else {
					ret += "if (" + map + ".cachedKey != null){\n";
					ret += tempRefs.get(p.getName()) + " = " + map + ".cachedKey;\n";
				}
				ret += "} else {\n";
				ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
				ret += "}\n";
				
				ret += "}\n";

				if (i == 0) {
					ret += retrieveTree() + ".put(" + tempRefs.get(p.getName()) + ", " + set + ", " + current_index_id + ");\n";
				} else {
					ret += lastMap + ".put(" + tempRefs.get(p.getName()) + ", " + set + ", " + current_index_id + ");\n";
				}

				ret += wrapper + " = " + "null;\n";
				ret += "}\n";
			} else if (i < queryParam.size() - 1) {
				if (i == 0) {
					ret += objs + " = " + retrieveTree() + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += obj + " = " + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += obj + " = null;\n";
					ret += "}\n";
				} else {
					ret += objs + " = " + map + ".getAll(" + p.getName() + ");\n";
					ret += "if(" + objs + " != null){\n";
					ret += obj + " = " + objs + "[" + current_index_id + "];\n";
					ret += "} else {\n";
					ret += obj + " = null;\n";
					ret += "}\n";
				}

				ret += "if (" + obj + " == null) {\n";

				if (i == queryParam.size() - 2)
					ret += obj + " = new javamoprt.MOPMultiMapNode(" + printMultiSignature(i + 1) + ");\n";
				else
					ret += obj + " = new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(i + 1) + ");\n";

				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
				ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
				ret += "}\n";

				if (i == 0) {
					ret += retrieveTree() + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";
				} else {
					ret += map + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";
				}

				ret += "} else {\n";
				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";

				if (i == 0) {
					ret += tempRefs.get(p.getName()) + " = " + retrieveTree() + ".cachedKey;\n";
				} else {
					ret += tempRefs.get(p.getName()) + " = " + map + ".cachedKey;\n";
				}

				ret += "}\n";

				ret += "}\n";

				if (i == queryParam.size() - 2) {
					ret += lastMap + " = (javamoprt.MOPMap)" + obj + ";\n";
				} else {
					ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
				}
			}
		}

		return ret;
	}

	public String lookupExactMonitor(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs) {
		String ret = "";

		bringParameters();

		ret += "Object[] " + objs + ";\n";

		if (isFullParam) {
			ret += lookupExactMonitorFullParam(wrapper, lastMap, set, map, obj, tempRefs);
		} else {
			ret += lookupExactMonitorPartialParam(wrapper, lastMap, set, map, obj, tempRefs);
		}

		return ret;
	}

	public String checkTime(MOPVariable timeCheck, MOPVariable wrapper, MOPVariable fromWrapper, MOPVariable set, MOPVariable map, MOPVariable obj) {
		String ret = "";

		bringParameters();

		if (isFullParam) {
			if (queryParam.size() == 1) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + retrieveTree() + ".get(" + queryParam.get_lexicographic(0).getName() + ", " + current_index_id + ");\n";

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get_lexicographic(i);

					if (i == queryParam.size() - 1) {
						ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + map + ".get(" + p.getName() + ", " + current_index_id + ");\n";
					} else if (i < queryParam.size() - 1) {
						if (i == 0) {
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						}

						ret += "if (" + obj + " != null) {\n";
						ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
					}
				}

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				for (int i = 0; i < queryParam.size() - 1; i++) {
					ret += "}\n";
				}
			}
		} else {
			if (queryParam.size() == 1) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + retrieveTree() + ".get(" + queryParam.get_lexicographic(0).getName() + ", " + current_index_id + ");\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				ret += "}\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get_lexicographic(i);

					if (i == queryParam.size() - 1) {
						ret += set + " = " + "(" + monitorSet.getName() + ")" + map + ".get(" + p.getName() + ", " + current_index_id + ");\n";
						ret += "if (" + set + " != null){\n";
						ret += monitorSet.getNode(wrapper, set);
					} else if (i < queryParam.size() - 1) {
						if (i == 0) {
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						}

						ret += "if (" + obj + " != null) {\n";
						ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
					}
				}

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				for (int i = 0; i < queryParam.size(); i++) {
					ret += "}\n";
				}
			}
		}

		return ret;
	}

	public String getCachedValue(MOPVariable obj) {
		String ret = "";

		bringParameters();

		if (this.cache == null)
			return ret;

		ret += this.cache.getCacheValue(queryParam, obj, current_index_id);
		ret += "\n";

		return ret;
	}

	public String setCacheKeys() {
		String ret = "";

		bringParameters();

		if (this.cache == null)
			return ret;

		ret += this.cache.setCacheKeys(queryParam);

		return ret;
	}

	public String setCacheValue(MOPVariable monitor) {
		String ret = "";

		bringParameters();

		if (this.cache == null)
			return ret;

		ret += this.cache.setCacheValue(objs);

		return ret;
	}

	public boolean containsSet() {
		for (IndexingTree tree : trees) {
			if (tree.containsSet())
				return true;
		}
		return false;
	}

	public String retrieveTree() {
		if (perthread) {
			return "((javamoprt.MOPMap)" + name + ".get())";
		} else {
			return name.toString();
		}
	}

	public String printMultiSignature(int ith) {
		String ret = "";

		if (ith < 0 || ith >= queryTypes.size())
			return ret;

		ret += "new javamoprt.MOPMultiMapSignature[]{\n";

		boolean first = true;
		for (IndexingTree tree : trees) {
			if (first)
				first = false;
			else
				ret += ",\n";

			ret += "new javamoprt.MOPMultiMapSignature(";
			if (ith == queryTypes.size() - 1) {
				if (tree.queryParam.size() == tree.fullParam.size()) {
					ret += "javamoprt.MOPMultiMapSignature.MAP_OF_MONITOR";
				} else {
					ret += "javamoprt.MOPMultiMapSignature.MAP_OF_SET";
				}
			} else {
				ret += "javamoprt.MOPMultiMapSignature.MAP_OF_MAP";
			}
			ret += ", ";
			ret += tree.fullParam.getIdnum(tree.queryParam.get_lexicographic(ith));
			ret += ")";
		}

		ret += "\n";
		ret += "}\n";

		return ret;
	}

	public String toString() {
		String ret = "";

		bringParameters();

		if (declPrinted)
			return ret;

		declPrinted = true;

		if (perthread) {
			ret += "static final ThreadLocal " + name + " = new ThreadLocal() {\n";
			ret += "protected Object initialValue(){\n";
			ret += "return ";

			if (queryTypes.size() == 1) {
				ret += "new javamoprt.MOPMultiMapNode(" + printMultiSignature(0) + ");\n";
			} else {
				ret += "new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(0) + ");\n";
			}

			ret += "}\n";
			ret += "};\n";
		} else {
			if (queryTypes.size() == 1) {
				ret += "static javamoprt.MOPMultiMapNode ";
				ret += name;
				ret += " = ";
				ret += "new javamoprt.MOPMultiMapNode(" + printMultiSignature(0) + ");\n";
			} else {
				ret += "static javamoprt.MOPMultiMapOfMap ";
				ret += name;
				ret += " = ";
				ret += "new javamoprt.MOPMultiMapOfMap(" + printMultiSignature(0) + ");\n";
			}
		}

		if (cache != null)
			ret += cache;

		return ret;
	}

}
