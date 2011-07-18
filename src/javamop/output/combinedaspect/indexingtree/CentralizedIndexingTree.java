package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class CentralizedIndexingTree extends IndexingTree {
	public CentralizedIndexingTree(String name, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet,
			WrapperMonitor monitor, boolean perthread) throws MOPException {
		super(name, queryParam, contentParam, fullParam, monitorSet, monitor, perthread);

		if (anycontent) {
			if (fullParam.size() == 0) {
				if (queryParam.size() == 0) {
					this.name = new MOPVariable(name + "_Monitor");
				} else {
					throw new MOPException("Event parameters cannot exceed the parameters for the specification");
				}
			} else {
				if (queryParam.size() == 0) {
					this.name = new MOPVariable(name + "_Set");
				} else {
					this.name = new MOPVariable(name + "_" + queryParam.parameterStringUnderscore() + "_Map");
				}
			}
		} else {
			if (!contentParam.contains(queryParam))
				throw new MOPException("[Internal] contentParam should contain queryParam");
			if (contentParam.size() <= queryParam.size())
				throw new MOPException("[Internal] contentParam should be larger than queryParam");

			if (contentParam.size() == 0) {
				if (queryParam.size() == 0) {
					this.name = new MOPVariable(name + "_Monitor");
				} else {
					throw new MOPException("Event parameters cannot exceed the parameters for the content");
				}
			} else {
				if (queryParam.size() == 0) {
					this.name = new MOPVariable(name + "__To__" + contentParam.parameterStringUnderscore() + "_Set");
				} else {
					this.name = new MOPVariable(name + "_" + queryParam.parameterStringUnderscore() + "__To__" + contentParam.parameterStringUnderscore() + "_Map");
				}
			}
		}

		if (queryParam.size() != 0)
			this.cache = new IndexingCache(this.name, this.queryParam, this.fullParam, perthread);
	}

	public String addMonitor(MOPVariable map, MOPVariable obj, MOPVariable monitors, HashMap<String, MOPVariable> mopRefs, MOPVariable monitor) {
		String ret = "";

		if (queryParam.equals(fullParam))
			return ret;

		if (queryParam.size() == 0) {
			ret += monitorSet.addMonitor(retrieveTree(), monitor);
		} else {
			ret += map + " = " + retrieveTree() + ";\n";

			for (int i = 0; i < queryParam.size(); i++) {
				MOPParameter p = queryParam.get(i);

				if (i != 0) {
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
			ret += monitorSet.addMonitor(monitors.toString(), monitor);
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
	public String addMonitorAfterLookup(MOPVariable map, MOPVariable set, MOPVariable monitorVar, HashMap<String, MOPVariable> mopRefs) {
		String ret = "";

		boolean isFullParam = false;
		if (queryParam.equals(fullParam))
			isFullParam = true;

		if (queryParam.size() == 0) {
			if (isFullParam){
				if(perthread)
					ret += name + ".set(" + "(" + monitor.getOutermostName() + ")" + monitor + ");\n";
				else
					ret += name + " = " + "(" + monitor.getOutermostName() + ")" + monitor + ";\n";
			}else
				ret += monitorSet.addMonitor(retrieveTree(), monitorVar);
		} else if (queryParam.size() == 1) {
			if (isFullParam)
				ret += retrieveTree() + ".put(" + monitorVar + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + monitorVar + ");\n";
			else
				ret += monitorSet.addMonitor(set.toString(), monitorVar);
		} else {
			if (isFullParam)
				ret += map + ".put(" + monitorVar + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + monitorVar + ");\n";
			else
				ret += monitorSet.addMonitor(set.toString(), monitorVar);
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
		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		boolean isFullParam = false;
		if (queryParam.equals(fullParam))
			isFullParam = true;

		if(isFullParam){
			if (queryParam.size() == 0){
				if(perthread)
					ret += name + ".set(" + "(" + monitor.getOutermostName() + ")" + wrapper + ");\n";
				else
					ret += name + " = " + "(" + monitor.getOutermostName() + ")" + wrapper + ";\n";
			} else if (queryParam.size() == 1) {
				ret += retrieveTree() + ".put(" + wrapper + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + wrapper + ");\n";
			} else {
				ret += lastMap + ".put(" + wrapper + "." + mopRefs.get(queryParam.get(queryParam.size() - 1).getName()) + ", " + wrapper + ");\n";
			}
		} else {
			if (queryParam.size() == 0){
				ret += monitorSet.setNode(retrieveTree(), wrapper);
			} else if (queryParam.size() == 1) {
				ret += monitorSet.setNode(set.toString(), wrapper);
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
		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		if (queryParam.equals(fullParam))
			return ret;

		if (queryParam.size() == 0) {
			ret += monitorSet.addMonitor(retrieveTree(), wrapper);
		} else {
			ret += monitorSet.addMonitor(set.toString(), wrapper);
		}
		return ret;
	}

	public String lookup(MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs, boolean creative) {
		String ret = "";

		if (queryParam.size() == 0) {
			ret += obj + " = " + retrieveTree() + ";\n";
		} else {
			if (creative) {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						if (i == 0)
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						else
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
					} else if (i < queryParam.size() - 1) {
						if (i == 0)
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						else
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						
						MOPParameter nextP = queryParam.get(i + 1);
						
						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 2 && fullParam.size() == queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(nextP) + ");\n";
						else if (i == queryParam.size() - 2 && fullParam.size() != queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(nextP) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(nextP) + ");\n";

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
					MOPParameter p = queryParam.get(i);

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

	public String lookupExactMonitor(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj,
			HashMap<String, MOPVariable> tempRefs) {
		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		boolean isFullParam = false;
		if (queryParam.equals(fullParam))
			isFullParam = true;

		if (isFullParam) {
			if (queryParam.size() == 0) {
				if(perthread)
					ret += wrapper + " = " + retrieveTree() + ";\n";
				else
					ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + retrieveTree() + ";\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						if (i == 0) {
							ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + lastMap + ".get(" + p.getName() + ");\n";
						}
					} else if (i < queryParam.size() - 1) {
						if (i == 0) {
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						}

						MOPParameter nextP = queryParam.get(i + 1);

						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 2 && fullParam.size() == queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(nextP) + ");\n";
						else if (i == queryParam.size() - 2 && fullParam.size() != queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(nextP) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(nextP) + ");\n";

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
			}
		} else {
			if (queryParam.size() == 0) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + retrieveTree() + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "} else {\n";
				ret += wrapper + " = null;\n";
				ret += "}\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						if (i == 0) {
							ret += set + " = " + "(" + monitorSet.getName() + ")" + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += set + " = " + "(" + monitorSet.getName() + ")" + lastMap + ".get(" + p.getName() + ");\n";
						}
						
						ret += "if (" + set + " != null){\n";
						ret += monitorSet.getNode(wrapper, set);
						ret += "} else {\n";
						ret += set + " = new " + monitorSet.getName() + "();\n";

						ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
						ret += tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
						ret += "}\n";
						
						if (i == 0) {
							ret += retrieveTree() + ".put(" + tempRefs.get(p.getName()) + ", " + set + ");\n";
						} else {
							ret += lastMap + ".put(" + tempRefs.get(p.getName()) + ", " + set + ");\n";
						}
						
						ret += wrapper + " = " + "null;\n";
						ret += "}\n";
					} else if (i < queryParam.size() - 1) {
						if (i == 0) {
							ret += obj + " = " + retrieveTree() + ".get(" + p.getName() + ");\n";
						} else {
							ret += obj + " = " + map + ".get(" + p.getName() + ");\n";
						}

						MOPParameter nextP = queryParam.get(i + 1);

						ret += "if (" + obj + " == null) {\n";

						if (i == queryParam.size() - 2 && fullParam.size() == queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(nextP) + ");\n";
						else if (i == queryParam.size() - 2 && fullParam.size() != queryParam.size())
							ret += obj + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(nextP) + ");\n";
						else
							ret += obj + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(nextP) + ");\n";

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
			}
		}

		return ret;
	}

	public String checkTime(MOPVariable timeCheck, MOPVariable wrapper, MOPVariable fromWrapper, MOPVariable set, MOPVariable map, MOPVariable obj) {
		String ret = "";

		if (contentParam != null && !contentParam.equals(queryParam))
			return ret;

		boolean isFullParam = false;
		if (queryParam.equals(fullParam))
			isFullParam = true;

		if (isFullParam) {
			if (queryParam.size() == 0) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + retrieveTree() + ";\n";
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
			} else if (queryParam.size() == 1) {
				ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + retrieveTree() + ".get(" + queryParam.get(0).getName() + ");\n";

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						ret += wrapper + " = " + "(" + monitor.getOutermostName() + ")" + map + ".get(" + p.getName() + ");\n";
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
			if (queryParam.size() == 0) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + retrieveTree() + ";\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);
				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";
				ret += "}\n";
			} else if (queryParam.size() == 1) {
				ret += set + " = " + "(" + monitorSet.getName() + ")" + retrieveTree() + ".get(" + queryParam.get(0).getName() + ");\n";
				ret += "if (" + set + " != null){\n";
				ret += monitorSet.getNode(wrapper, set);

				ret += "if (" + wrapper + " != null && (" + monitor.getDisable(wrapper) + " > " + monitor.getTau(fromWrapper);
				ret += " || " + monitor.getTau(wrapper) + " < " + monitor.getTau(fromWrapper) + ")) {\n";
				ret += timeCheck + " = " + "false;\n";
				ret += "}\n";

				ret += "}\n";
			} else {
				for (int i = 0; i < queryParam.size(); i++) {
					MOPParameter p = queryParam.get(i);

					if (i == queryParam.size() - 1) {
						ret += set + " = " + "(" + monitorSet.getName() + ")" + map + ".get(" + p.getName() + ");\n";
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

		if (this.cache == null)
			return ret;

		ret += this.cache.getCacheValue(obj);
		ret += "\n";

		return ret;
	}

	public String setCacheKeys() {
		String ret = "";

		if (this.cache == null)
			return ret;

		ret += this.cache.setCacheKeys();

		return ret;
	}

	public String setCacheValue(MOPVariable monitor) {
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
	
	public String retrieveTree(){
		if(perthread){
			String ret = "";
			
			ret += "(";
			
			if (fullParam.size() == 0) {
				if (queryParam.size() == 0) {
					ret += "(" + monitor.getOutermostName() + ")";
				} else {
					ret += "***** A bug in indexing tree *****\n";
				}
			} else if (queryParam.size() == 0) {
				ret += "(javamoprt.MOPSet)";
			} else {
				ret += "(javamoprt.MOPMap)";
			}

			ret += name + ".get()";
			ret += ")";
			
			return ret;
			
		} else {
			return name.toString();
		}
	}

	public String toString() {
		String ret = "";

		if(perthread){
			ret += "static final ThreadLocal " + name + " = new ThreadLocal() {\n";
			ret += "protected Object initialValue(){\n";
			ret += "return ";

			if (fullParam.size() == 0) {
				if (queryParam.size() == 0) {
					ret += "new " + monitor.getOutermostName() + "();\n";
				} else {
					ret += "***** A bug in indexing tree *****\n";
				}
			} else if (queryParam.size() == 0) {
				ret += "new " + monitorSet.getName() + "();\n";
			} else if (queryParam.size() == 1) {
				if (queryParam.size() == fullParam.size()) {
					ret += "new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
				} else {
					ret += "new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
				}
			} else {
				ret += "new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
			}
			
			ret += "}\n";
			ret += "};\n";
		} else {
			if (fullParam.size() == 0) {
				if (queryParam.size() == 0) {
					ret += "static " + monitor.getOutermostName() + " " + name + " = new " + monitor.getOutermostName() + "();\n";
				} else {
					ret += "***** A bug in indexing tree *****";
				}
			} else if (queryParam.size() == 0) {
				ret += "static javamoprt.MOPSet " + name + " = new " + monitorSet.getName() + "();\n";
			} else if (queryParam.size() == 1) {
				if (queryParam.size() == fullParam.size()) {
					ret += "static javamoprt.MOPMap " + name + " = new javamoprt.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
				} else {
					ret += "static javamoprt.MOPMap " + name + " = new javamoprt.MOPMapOfSet(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
				}
			} else {
				ret += "static javamoprt.MOPMap " + name + " = new javamoprt.MOPMapOfMap(" + fullParam.getIdnum(queryParam.get(0)) + ");\n";
			}
		}

		if (cache != null)
			ret += cache;

		return ret;
	}

}
