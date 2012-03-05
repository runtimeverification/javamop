package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.RefTree;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class ScalableSpecialAdviceBody extends AdviceBody {
	AroundAdviceLocalDecl aroundLocalDecl = null;
	AroundAdviceReturn aroundAdviceReturn = null;
	IndexingTree indexingTree;

	MOPVariable obj = new MOPVariable("obj");
	MOPVariable monitor = new MOPVariable("monitor");
	MOPVariable monitors = new MOPVariable("monitors");
	MOPVariable m = new MOPVariable("m");

	HashMap<String, MOPVariable> tempRefs = new HashMap<String, MOPVariable>();
	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();

	public ScalableSpecialAdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) {
		super(mopSpec, event, combinedAspect);

		if (mopSpec.has__SKIP() || event.getPos().equals("around"))
			aroundLocalDecl = new AroundAdviceLocalDecl();
		if (event.getPos().equals("around"))
			aroundAdviceReturn = new AroundAdviceReturn(event.getRetType(), event.getParametersWithoutThreadVar());

		for (MOPParameter p : event.getMOPParametersOnSpec()) {
			tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
		}
		if (event.isStartEvent()) {
			for (MOPParameter p : event.getMOPParametersOnSpec()) {
				mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
			}
		}

		for (MOPParameters param : indexingTrees.keySet()) {
			if (param.equals(event.getMOPParametersOnSpec()))
				this.indexingTree = indexingTrees.get(param);
		}
	}

	public String toString() {
		String ret = "";

		if (aroundLocalDecl != null)
			ret += aroundLocalDecl;

		if (mopSpec.getParameters().size() == 0) {
			ret += monitorClass.Monitoring(indexingTree.getName(), event, null, null);
		} else {
			if (event.getMOPParametersOnSpec().size() != 0) {
				ret += "Object " + obj + " = null;\n";
			}

			ret += "javamoprt.MOPMap " + m + ";\n";
			ret += monitorName + " " + monitor + " = null;\n";

			if (event.isStartEvent()) {
				ret += monitorSet.getName() + " " + monitors + " = null;\n";
			}

			for (MOPVariable tempRef : tempRefs.values()) {
				ret += "javamoprt.MOPWeakReference " + tempRef + ";\n";
			}

			if (ret.length() != 0)
				ret += "\n";

			if (event.getMOPParametersOnSpec().size() != 0) {
				// cache
				if (indexingTree.hasCache()){
					ret += indexingTree.getCachedValue(obj);
					ret += "if(" + obj + " == null) {\n";
				}
				
				if (event.isStartEvent()) {
					for (MOPParameter p : event.getMOPParametersOnSpec()) {
						MOPVariable tempRef = tempRefs.get(p.getName());
						RefTree refTree = refTrees.get(p.getType().toString());
						
						ret += refTree.get(tempRef, p);
					}
					
					// lookup a map to retrieve monitor(s)
					ret += indexingTree.lookup(m, obj, tempRefs, event.isStartEvent());

					ret += "\n";
					ret += monitor + " = (" + monitorName + ") " + obj + ";\n";
					ret += "if (" + monitor + " == null" + "){\n";
					ret += monitor + " = new " + monitorName + "();\n";

					for (int i = 0; i < event.getMOPParametersOnSpec().size(); i++) {
						MOPParameter p = indexingTree.getQueryParam(i);

						ret += monitor + "." + mopRefs.get(p.getName()) + " = ";
						ret += tempRefs.get(p.getName()) + ";\n";
					}
					ret += indexingTree.addMonitorAfterLookup(m, monitor, mopRefs);

					for (IndexingTree indexingTree2 : indexingTrees.values()) {
						if (indexingTree2 == indexingTree)
							continue;

						ret += "\n";
						ret += indexingTree2.addMonitor(m, obj, monitors, mopRefs, monitor);
					}

					ret += "}\n";

				} else {
					for (MOPParameter p : event.getMOPParametersOnSpec()) {
						MOPVariable tempRef = tempRefs.get(p.getName());
						RefTree refTree = refTrees.get(p.getType().toString());
						
						ret += refTree.getRefNonCreative(tempRef, p);
					}
					
					ret += "if (";
					for (int i = 0; i < event.getMOPParametersOnSpec().size(); i++) {
						MOPParameter p = indexingTree.getQueryParam(i);
						MOPVariable tempRef = tempRefs.get(p.getName());
						
						if(i != 0)
							ret += " && ";
						
						ret += tempRef + " != javamoprt.MOPRefMap.NULRef";
					}
					ret += "){\n";

					// lookup a map to retrieve monitor(s)
					ret += indexingTree.lookup(m, obj, tempRefs, event.isStartEvent());
				}

				if (indexingTree.hasCache()) {
					ret += indexingTree.setCacheKeys();

					if (event.isStartEvent()) {
						ret += indexingTree.setCacheValue(monitor);
						ret += "} else {\n";
						ret += monitor + " = (" + monitorName + ") " + obj + ";\n";
						ret += "}\n";
					} else {
						ret += indexingTree.setCacheValue(obj);
						ret += "}\n";
					}
				} else {
					if (!event.isStartEvent()) {
						ret += monitor + " = (" + monitorName + ") " + obj + ";\n";
					}
				}
				
				if (!event.isStartEvent()) {
					ret += "}\n";
				}

				
				
			}

			if (event.isStartEvent()) {
				ret += monitorClass.Monitoring(monitor, event, null, null);
			} else if (mopSpec.getParameters().size() == event.getMOPParametersOnSpec().size()) {

				ret += monitor + " = (" + monitorName + ")" + obj + ";\n";
				ret += "if(" + monitor + " != null) {\n";
				ret += monitorClass.Monitoring(monitor, event, null, null);
				ret += "}\n";

			} else if (event.getMOPParametersOnSpec().size() == 0) {
				ret += monitorSet.Monitoring(indexingTree.getName(), event, null, null);
			} else {
				ret += monitorSet.Monitoring(obj, event, null, null);
			}
		}

		if (aroundAdviceReturn != null)
			ret += aroundAdviceReturn;

		return ret;
	}
}
