package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class DecentralizedIndexingTree extends IndexingTree {
	MOPParameter firstKey;

	public DecentralizedIndexingTree(String name, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, WrapperMonitor monitor,
			boolean perthread) throws MOPException {
		super(name, queryParam, contentParam, fullParam, monitorSet, monitor, perthread);

		if (perthread)
			throw new MOPException("decentralized perthread specification is not supported");

		if (fullParam.size() == 0) {
			if (queryParam.size() == 0) {
				this.name = new MOPVariable(name + "_Monitor");
			} else {
				throw new MOPException("Event parameters cannot exceed the parameters for the specification");
			}
		} else if (fullParam.size() == 1) {
			if (queryParam.size() == 0) {
				this.name = new MOPVariable(name + "_Set");
			} else if (queryParam.size() == 1) {
				this.name = new MOPVariable(name + "_" + queryParam.parameterStringUnderscore() + "_Monitor");
			} else {
				throw new MOPException("Event parameters cannot exceed the parameters for the specification");
			}
		} else {
			if (queryParam.size() == 0) {
				this.name = new MOPVariable(name + "_Set");
			} else if (queryParam.size() == 1) {
				this.name = new MOPVariable(name + "_" + queryParam.parameterStringUnderscore() + "_Set");
			} else {
				this.name = new MOPVariable(name + "_" + queryParam.parameterStringUnderscore() + "_Map");
			}
		}

		if (queryParam.size() != 0)
			this.firstKey = queryParam.get(0);

		if (queryParam.size() > 1)
			this.cache = new IndexingCache(this.name, this.queryParam, this.fullParam, perthread);
	}

	public String addMonitor(MOPVariable map, MOPVariable obj, MOPVariable monitors, HashMap<String, MOPVariable> mopRefs, MOPVariable monitor) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.addMonitor(map, obj, monitors, mopRefs, monitor);
		}

		String ret = "";

		if (queryParam.equals(fullParam))
			return ret;

		if (queryParam.size() == 0) {
			ret += monitorSet.addMonitor(name.toString(), monitor);
		} else if (queryParam.size() == 1) {
			ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
			ret += firstKey.getName() + "." + name + " = new " + monitorSet.getName() + "();\n";
			ret += "}\n";
			ret += firstKey.getName() + "." + monitorSet.addMonitor(name.toString(), monitor);
		} else {
			ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
			if (queryParam.size() == 2) {
				ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
			} else {
				ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
			}
			ret += "}\n";

			ret += map + " = " + firstKey.getName() + "." + name + ";\n";

			for (int i = 1; i < queryParam.size(); i++) {
				MOPParameter p = queryParam.get(i);

				if (i != 1) {
					ret += "if (" + obj + " == null) {\n";

					if (i == queryParam.size() - 1) {
						ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(p) + ");\n";
					} else
						ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(p) + ");\n";

					ret += map + ".put(";
					ret += monitor + "." + mopRefs.get(queryParam.get(i - 1).getName()) + ", " + obj + ");\n";
					ret += "}\n";

					ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
				}

				ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
			}

			ret += monitors + " = ";
			ret += "(" + monitorSet.getName() + ")" + obj + ";\n";
			ret += "if (" + monitors + " == null) {\n";
			ret += monitors + " = new " + monitorSet.getName() + "();\n";
			ret += map + ".put(";
			ret += monitor + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName());
			ret += ", " + monitors + ");\n";
			ret += "}\n";
			ret += monitors + ".add(" + monitor + ");\n";
		}

		return ret;
	}

	/*
	 * addMonitorAfterLookup adds the given wrapper to the indexing tree.
	 * 
	 * It does not care about the nodes at monitorSets.
	 * 
	 * Also, it assumes that this indexing tree was accessed already by using
	 * lastMap and set.
	 */
	public String addMonitorAfterLookup(MOPVariable map, MOPVariable monitorVar, HashMap<String, MOPVariable> mopRefs) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.addMonitorAfterLookup(map, monitorVar, mopRefs);
		}

		String ret = "";

		if (queryParam.size() == 0) {
			ret += name + " = " + "(" + monitor.getOutermostName() + ")" + monitorVar + ";\n";
		} else if (queryParam.size() == 1) {
			ret += firstKey.getName() + "." + name + " = " + monitorVar + ";\n";
		} else {
			ret += map + ".put(" + monitorVar + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + monitorVar + ");\n";
		}

		return ret;
	}

	/*
	 * addExactWrapper adds the given wrapper to the corresponding node of the
	 * indexing tree so that lookupExactMonitor accesses it.
	 * 
	 * It does not update any content of set.
	 * 
	 * Also, it assumes that this indexing tree was accessed already by using
	 * lastMap and set.
	 */
	public String addExactWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.addExactWrapper(wrapper, lastMap, set, mopRefs);
		}

		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		if (isFullParam) {
			if (queryParam.size() == 0) {
				ret += name + " = " + "(" + monitor.getOutermostName() + ")" + wrapper + ";\n";
			} else if (queryParam.size() == 1) {
				ret += firstKey.getName() + "." + name + " = " + wrapper + ";\n";
			} else {
				ret += lastMap + ".put(" + wrapper + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + wrapper + ");\n";
			}
		} else {
			if (queryParam.size() == 0) {
				ret += monitorSet.setNode(name.toString(), wrapper);
			} else if (queryParam.size() == 1) {
				ret += firstKey.getName() + "." + monitorSet.setNode(name.toString(), wrapper);
			} else {
				ret += monitorSet.setNode(set.toString(), wrapper);
			}
		}

		return ret;
	}

	/*
	 * addWrapper adds the given wrapper to the corresponding set of the
	 * indexing tree.
	 * 
	 * It does not update any node of set.
	 * 
	 * Also, it assumes that this indexing tree was accessed already by using
	 * lastMap and set.
	 */
	public String addWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.addWrapper(wrapper, lastMap, set, mopRefs);
		}

		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		if (queryParam.equals(fullParam))
			return ret;

		if (queryParam.size() == 0) {
			ret += monitorSet.addMonitor(name.toString(), wrapper);
		} else if (queryParam.size() == 1) {
			ret += firstKey.getName() + "." + monitorSet.addMonitor(name.toString(), wrapper);
		} else {
			ret += monitorSet.addMonitor(set.toString(), wrapper);
		}

		return ret;
	}

	public String lookup(MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs, boolean creative) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.lookup(map, obj, tempRefs, creative);
		}

		String ret = "";

		if (queryParam.size() == 2) {
			if (creative) {
				ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
				if (fullParam.size() == queryParam.size()) {
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
				} else {
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
				}
				ret += "}\n";
			}

			ret += map + " = " + firstKey.getName() + "." + name + ";\n";
			ret += "\n";
		} else if (queryParam.size() > 2) {
			if (creative) {
				ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
				ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
				ret += "}\n";
			}

			ret += map + " = " + firstKey.getName() + "." + name + ";\n";
			ret += "\n";
		}

		if (queryParam.size() == 0) {
			ret += obj + " = " + name + ";\n";
		} else if (queryParam.size() == 1) {
			if (creative && fullParam.size() > 1) {
				ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
				ret += firstKey.getName() + "." + name + " = new " + monitorSet.getName() + "();\n";
				ret += "}\n";
			}
			ret += obj + " = " + firstKey.getName() + "." + name + ";\n";
		} else {
			if (creative) {
				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i != 1) {
						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 1 && fullParam.size() == queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(p) + ");\n";
						else if (i == queryParam.size() - 1 && fullParam.size() != queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(p) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(p) + ");\n";

						ret += tempRefs.get(queryParam.get(i - 1).getName()) + " = new javamoprt.MOPWeakReference(" + queryParam.get(i - 1).getName() + ");\n";

						ret += map + ".put(" + tempRefs.get(queryParam.get(i - 1).getName()) + ", " + obj + ");\n";
						ret += "} else {\n";
						ret += tempRefs.get(queryParam.get(i - 1).getName()) + " = " + map + ".cachedKey;\n";
						ret += "}\n";

						ret += map + " = (javamoprt.MOPMap)" + obj + ";\n";
					}

					ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
				}
			} else {
				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == 1) {
						ret += "if (" + map + " != null) {\n";
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						ret += "} else {\n";
						ret += obj + " = null;\n";
						ret += "}\n";
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

	public String lookupExactMonitor(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.lookupExactMonitor(wrapper, lastMap, set, map, obj, tempRefs);
		}

		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		if (isFullParam) {
			if (queryParam.size() == 0) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + name + ";\n";
			} else if (queryParam.size() == 1) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + firstKey.getName() + "." + name + ";\n";
			} else {
				if (queryParam.size() == 2) {
					// null check
					ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
					ret += "}\n";
					// save the map to lastMap
					ret += lastMap + " = " + firstKey.getName() + "." + name + ";\n";
				} else if (queryParam.size() > 2) {
					// null check
					ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
					ret += "}\n";
					// save the map to map
					ret += map + " = " + firstKey.getName() + "." + name + ";\n";
				}

				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + lastMap + ".get(" + p.getName() + ");\n";
					} else if (i < queryParam.size() - 1) {
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";

						MOPParameter nextP = queryParam.get(i + 1);

						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 2)
							ret += obj + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(nextP) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(nextP) + ");\n";

						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
						ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
						ret += "}\n";
						ret += map + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";

						ret += "} else {\n";
						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
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
			}
		} else {
			if (queryParam.size() == 0) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + name + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "} else {\n";
				ret += wrapper + " = null;\n";
				ret += "}\n";
			} else if (queryParam.size() == 1) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + firstKey.getName() + "." + name + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "} else {\n";
				ret += wrapper + " = null;\n";
				ret += "}\n";
			} else {
				if (queryParam.size() == 2) {
					// null check
					ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
					ret += "}\n";
					// save the map to lastMap
					ret += lastMap + " = " + firstKey.getName() + "." + name + ";\n";
				} else if (queryParam.size() > 2) {
					// null check
					ret += "if (" + firstKey.getName() + "." + name + " == null) {\n";
					ret += firstKey.getName() + "." + name + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(1)) + ");\n";
					ret += "}\n";
					// save the map to map
					ret += map + " = " + firstKey.getName() + "." + name + ";\n";
				}

				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						ret += set + " = " + "(" + monitorSet.getName() + ")" + lastMap + ".get(" + p.getName() + ");\n";

						ret += "if (" + set + " != null){\n";
						ret += monitorSet.getNode(wrapper, set);
						ret += "} else {\n";
						ret += set + " = new " + monitorSet.getName() + "();\n";
						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
						ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
						ret += "}\n";
						ret += lastMap + ".put(" + tempRefs.get(p.getName()) + ", " + set + ");\n";
						ret += wrapper + " = " + "null;\n";
						ret += "}\n";
					} else if (i < queryParam.size() - 1) {
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";

						MOPParameter nextP = queryParam.get(i + 1);

						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 2)
							ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(nextP) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(nextP) + ");\n";

						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
						ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
						ret += "}\n";
						ret += map + ".put(" + tempRefs.get(p.getName()) + ", " + obj + ");\n";

						ret += "} else {\n";
						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
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
			}
		}

		return ret;
	}

	public String checkTime(MOPVariable timeCheck, MOPVariable wrapper, MOPVariable fromWrapper, MOPVariable set, MOPVariable map, MOPVariable obj) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.checkTime(timeCheck, wrapper, fromWrapper, set, map, obj);
		}

		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		if (isFullParam) {
			if (queryParam.size() == 0) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + name + ";\n";
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
			} else if (queryParam.size() == 1) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + firstKey.getName() + "." + name + ";\n";
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
			} else if (queryParam.size() == 2) {
				ret += "if (" + firstKey.getName() + "." + name + " != null) {\n";
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + "(" + "(javamoprt.MOPMap)" + firstKey.getName() + "." + name + ")" + ".get("
						+ queryParam.get(1).getName() + ");\n";

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				ret += "}\n";

			} else {
				ret += "if (" + firstKey.getName() + "." + name + " != null) {\n";
				ret += map + " = (javamoprt.MOPMap)" + firstKey.getName() + "." + name + ";\n";

				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);
					if (i == queryParam.size() - 1) {
						ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + map + ".get(" + p.getName() + ");\n";
					} else if (i < queryParam.size() - 1) {
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";

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
			if (queryParam.size() == 0) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + name + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
				ret += "}\n";
			} else if (queryParam.size() == 1) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + firstKey.getName() + "." + name + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
				ret += "}\n";
			} else if (queryParam.size() == 2) {
				ret += "if (" + firstKey.getName() + "." + name + " != null) {\n";
				ret += set + " = " + "(" + monitorSet.getName() + ")" + "(" + "(javamoprt.MOPMap)" + firstKey.getName() + "." + name + ")" + ".get(" + queryParam.get(1).getName()
						+ ");\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				ret += "}\n";
				ret += "}\n";
			} else {
				ret += "if (" + firstKey.getName() + "." + name + " != null) {\n";
				ret += map + " = (javamoprt.MOPMap)" + firstKey.getName() + "." + name + ";\n";

				for (int i = 1; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);
					if (i == queryParam.size() - 1) {
						ret += set + " = " + "(" + monitorSet.getName() + ")" + map + ".get(" + p.getName() + ");\n";
						ret += "if (" + set + " != null){\n";
						ret += monitorSet.getNode(wrapper, set);
					} else if (i < queryParam.size() - 1) {
						ret += obj + " = " + map + ".get(" + p.getName() + ");\n";

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
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.getCachedValue(obj);
		}

		String ret = "";

		if (this.cache == null)
			return ret;

		ret += this.cache.getCacheValue(obj);
		ret += "\n";

		return ret;
	}

	public String setCacheKeys() {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.setCacheKeys();
		}

		String ret = "";

		if (this.cache == null)
			return ret;

		ret += this.cache.setCacheKeys();

		return ret;
	}

	public String setCacheValue(MOPVariable monitor) {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.setCacheValue(monitor);
		}

		String ret = "";

		if (this.cache == null)
			return ret;

		ret += this.cache.setCacheValue(monitor);

		return ret;
	}

	public boolean containsSet() {
		if (queryParam.equals(fullParam))
			return false;
		if (queryParam.equals(contentParam))
			return false;
		return true;
	}

	public String retrieveTree() {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.retrieveTree();
		}

		return name.toString();
	}

	public String toString() {
		if (combinedIndexingTree != null) {
			combinedIndexingTree.current_index_id = this.index_id;
			return combinedIndexingTree.toString();
		}

		String ret = "";

		if (fullParam.size() == 0) {
			if (queryParam.size() == 0) {
				ret += "static " + monitor.getOutermostName() + " " + name + " = new " + monitor.getOutermostName() + "();\n";
			} else {
				ret += "***** A bug in indexing tree *****\n";
			}
		} else if (fullParam.size() == 1) {
			if (queryParam.size() == 0) {
				ret += "static javamoprt.MOPSet " + name + " = new " + monitorSet.getName() + "();\n";
			} else if (queryParam.size() == 1) {
				ret += monitor.getOutermostName() + " " + firstKey.getType() + "." + name + " = null;\n";
			} else {
				ret += "***** A bug in indexing tree *****\n";
			}
		} else if (fullParam.size() == 2) {
			if (queryParam.size() == 0) {
				ret += "static javamoprt.MOPSet " + name + " = new " + monitorSet.getName() + "();\n";
			} else if (queryParam.size() == 1) {
				ret += "static javamoprt.MOPSet " + firstKey.getType() + "." + name + " = null;\n";
			} else if (queryParam.size() == 2) {
				ret += "static javamoprt.MOPMap " + firstKey.getType() + "." + name + " = null;\n";
			} else {
				ret += "***** A bug in indexing tree *****\n";
			}
		} else if (fullParam.size() > 2) {
			if (queryParam.size() == 0) {
				ret += "static javamoprt.MOPSet " + name + " = new " + monitorSet.getName() + "();\n";
			} else if (queryParam.size() == 1) {
				ret += "static javamoprt.MOPSet " + firstKey.getType() + "." + name + " = null;\n";
			} else if (queryParam.size() == 2) {
				ret += "static javamoprt.MOPMap " + firstKey.getType() + "." + name + " = null;\n";
			} else {
				ret += "static javamoprt.MOPMap " + firstKey.getType() + "." + name + " = null;\n";
			}
		}

		if (cache != null)
			ret += cache;

		return ret;
	}

}
