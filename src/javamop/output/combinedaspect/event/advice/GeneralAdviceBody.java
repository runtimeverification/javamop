package javamop.output.combinedaspect.event.advice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.MOPStatManager;
import javamop.output.combinedaspect.indexingtree.IndexingCache;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.MonitorInfo;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameters;

public class GeneralAdviceBody extends AdviceBody {
	public MOPStatManager statManager;
	
	public IndexingTree indexingTree;
	public IndexingCache cache = null;

	public HashMap<MOPParameterPair, IndexingTree> indexingTreesForCopy;
	public ArrayList<MOPParameterPair> paramPairsForCopy;

	MOPVariable timestamp;

	public HashSet<MOPParameter> parametersForDisable;

	boolean isFullBinding;
	boolean isConnected;
	MonitorInfo monitorInfo;

	LocalVariables localVars;
	
	boolean doDisable = false;
	
	GlobalLock lock;
	
	String aspectName;

	// assumes: mopSpec.getParameters().size() != 0
	public GeneralAdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		super(mopSpec, event, combinedAspect);

		this.isFullBinding = mopSpec.isFullBinding();
		this.isConnected = mopSpec.isConnected();

		this.monitorInfo = monitorClass.getMonitorInfo();

		this.timestamp = combinedAspect.timestampManager.getTimestamp(mopSpec);
		this.indexingTree = indexingTrees.get(eventParams);
		this.cache = this.indexingTree.getCache();
		this.parametersForDisable = combinedAspect.setOfParametersForDisable.get(mopSpec);

		this.indexingTreesForCopy = indexingDecl.getIndexingTreesForCopy();

		this.localVars = new LocalVariables(mopSpec, event, combinedAspect);

		this.paramPairsForCopy = indexingDecl.getCopyParamForEvent(event);
		
		this.doDisable = doDisable();
		
		this.statManager = combinedAspect.statManager;
		this.aspectName = combinedAspect.getAspectName();
		lock = new GlobalLock(new MOPVariable(this.aspectName + "." + combinedAspect.lockManager.getLock().getName()));
	}

	public boolean doDisable(){
		for (MOPParameter p : eventParams) {
			if (parametersForDisable.contains(p)) {
				return true;
			}
		}

		return false;
	}
	
	// opt done
	public RefTree getRefTree(MOPParameter p) {
		return refTrees.get(p.getType().toString());
	}

	// opt done
	public boolean isUsingMonitor() {
		if (!indexingTree.containsSet())
			return true;

		if (event.isStartEvent()) {
			return true;
		}

		if (paramPairsForCopy != null && paramPairsForCopy.size() > 0) {
			return true;
		}
		if (doDisable && !this.mopSpec.isEnforce()) {
			return true;
		}
		
		return false;
	}

	/***********************/

	// opt done
	public String refRetrievalFromCache() {
		String ret = "";

		if (this.cache == null)
			return ret;

		ret += cache.getCacheKeys(localVars);

		return ret;
	}

	// opt done
	public String refRetrievalFromTree() {
		String ret = "";

		for (MOPParameter p : this.eventParams) {
			MOPVariable tempRef = localVars.getTempRef(p);

			if((!isGeneral && !event.isStartEvent()) || (isGeneral && !event.isStartEvent() && paramPairsForCopy.size() == 0 && !doDisable))
				ret += getRefTree(p).getRefNonCreative(tempRef, p);
			else
				ret += getRefTree(p).get(tempRef, p);
		}

		return ret;
	}

	// opt done
	public String cacheRetrieval() {
		String ret = "";

		if (cache == null)
			return ret;

		String keyComparison = cache.getKeyComparison();

		ret += "// Cache Retrieval\n";

		ret += "if (" + keyComparison + ") {\n";
		{
			ret += refRetrievalFromCache();
			ret += "\n";
			if (indexingTree.containsSet()) {
				MOPVariable mainSet = localVars.get("mainSet");
				ret += cache.getCacheSet(mainSet);
			}
			if (isUsingMonitor()) {
				MOPVariable mainMonitor = localVars.get("mainMonitor");
				ret += cache.getCacheNode(mainMonitor);
			}
		}
		ret += "} else {\n";
		{
			ret += refRetrievalFromTree();

//			if((!isGeneral && !event.isStartEvent()) || (isGeneral && !event.isStartEvent() && paramPairsForCopy.size() == 0 && !doDisable)){
//				MOPVariable cacheHit = localVars.get("cacheHit");
//				ret += cacheHit + " = false;\n";
//			}
		}
		ret += "}\n";

		ret += "\n";

		return ret;
	}

	// opt done
	public String retrieveIndexingTree()  throws MOPException {
		String ret = "";

		if (isUsingMonitor()) {
			if (indexingTree.containsSet()) {
				ret += indexingTree.lookupNodeAndSet(localVars, "mainMonitor", "mainMap", "mainSet", event.isStartEvent());
			} else {
				ret += indexingTree.lookupNode(localVars, "mainMonitor", "mainMap", "mainSet", event.isStartEvent());
			}
		} else {
			if (indexingTree.containsSet()) {
				ret += indexingTree.lookupSet(localVars, "mainMonitor", "mainMap", "mainSet", event.isStartEvent());
			}
		}

		ret += "\n";

		return ret;
	}

	// opt done
	public String copyStateFromList(MOPParameterPair paramPair, IndexingTree indexingTreeForCopy)  throws MOPException {
		String ret = "";

		IndexingTree targetIndexingTree = null;

		MOPParameters fromParams = paramPair.getParam2();
		MOPParameters toParams = MOPParameters.unionSet(fromParams, eventParams);
		toParams = mopSpec.getParameters().sortParam(toParams);

		for (MOPParameters param : indexingTrees.keySet()) {
			if (param.equals(toParams)) {
				targetIndexingTree = indexingTrees.get(param);
			}
		}
		if (targetIndexingTree == null)
			throw new Error("[Internal] cannot find the indexing tree");

		MOPVariable origSet = localVars.get("origSet");
		MOPVariable origMonitor = localVars.get("origMonitor");

		MOPVariable lastMonitor = localVars.get("lastMonitor");

		MOPVariable numAlive = new MOPVariable("numAlive");
		MOPVariable i = new MOPVariable("i");
		MOPVariable timeCheck = new MOPVariable("timeCheck");

		MOPParameters newParam = new MOPParameters();
		for (MOPParameter p : fromParams) {
			if (eventParams.contains(p))
				continue;
			newParam.add(p);
		}

		ret += "int " + numAlive + " = 0;\n";
		ret += "for(int " + i + " = 0; " + i + " < " + origSet + ".size; " + i + "++) {\n";
		{
			ret += origMonitor + " = " + origSet + ".elementData[" + i + "];\n";
			for (MOPParameter p : newParam)
				ret += p.getType() + " " + p.getName() + " = " + "(" + p.getType() + ")" + origMonitor + "." + monitorClass.getMOPRef(p) + ".get();\n";

			ret += "if (!" + origMonitor + ".MOP_terminated";
			for (MOPParameter p : newParam)
				ret += " && " + p.getName() + " != null";
			ret += ") {\n";
			{
				ret += origSet + ".elementData[" + numAlive + "] = " + origMonitor + ";\n";
				ret += numAlive + "++;\n";
				ret += "\n";

				for (MOPParameter p : newParam) {
					MOPVariable tempRef = localVars.getTempRef(p);
					ret += tempRef + " = " + origMonitor + "." + monitorClass.getMOPRef(p) + ";\n";
				}
				
				ret += "\n";

				ret += targetIndexingTree.lookupNode(localVars, "lastMonitor", "lastMap", "lastSet", true);

				ret += "if (" + lastMonitor + " == null) {\n";
				{
					ret += "boolean " + timeCheck + " = true;\n\n";
					for (MOPParameter p : eventParams) {

						if (!fromParams.contains(p)) {
							MOPVariable tau = new MOPVariable("tau");
							String tempRefTau = getTempRefTau(p);

							ret += "if (" + getTempRefDisable(p) + " > " + origMonitor + "." + tau;
							ret += "|| (" + tempRefTau + " > 0 && " + tempRefTau + " < " + origMonitor + "." + tau + ")) {\n";
							{
								ret += timeCheck + " = false;\n";
							}
							ret += "}\n";
						}
					}
					ret += "\n";

					ret += "if (" + timeCheck + ") {\n";
					{
						ret += statManager.incMonitor(mopSpec);

						ret += lastMonitor + " = " + "(" + monitorClass.getOutermostName() + ")" + origMonitor + ".clone();\n";

						for (MOPParameter p : toParams) {
							if (!fromParams.contains(p)) {
								MOPVariable tempRef = localVars.getTempRef(p);
								
								ret += lastMonitor + "." + monitorClass.getMOPRef(p) + " = " + tempRef + ";\n";
								
								RefTree refTree = getRefTree(p);

								int tagNumber = refTree.getTagNumber(mopSpec);

								if (!refTree.isTagging())
									continue;

								if (tagNumber == -1) {
									ret += "if (" + tempRef + "." + "tau == -1){\n";
									ret += tempRef + "." + "tau = " + origMonitor + ".tau" + ";\n";
									ret += "}\n";
								} else {
									ret += "if (" + tempRef + "." + "tau[" + tagNumber + "] == -1){\n";
									ret += tempRef + "." + "tau[" + tagNumber + "] = " + origMonitor + ".tau" + ";\n";
									ret += "}\n";
								}
							}
						}

						ret += targetIndexingTree.attachNode(localVars, "lastMonitor", "lastMap", "lastSet");

						if (monitorInfo != null)
							ret += monitorInfo.expand(lastMonitor, monitorClass, toParams);

						for (MOPParameters param : indexingTrees.keySet()) {
							if (toParams.contains(param) && !toParams.equals(param)) {
								IndexingTree indexingTree = indexingTrees.get(param);

								ret += "\n";
								if (indexingTree == this.indexingTree)
									ret += indexingTree.addMonitor(localVars, "lastMonitor", "mainMap", "mainSet");
								else
									ret += indexingTree.addMonitor(localVars, "lastMonitor");
							}
						}

						for (MOPParameterPair paramPair2 : indexingTreesForCopy.keySet()) {
							if (paramPair2.getParam2().equals(toParams)) {
								IndexingTree indexingTree = indexingTreesForCopy.get(paramPair2);

								ret += "\n";
								ret += indexingTree.addMonitor(localVars, "lastMonitor");
							}
						}
					}
					ret += "}\n";
				}
				ret += "}\n";
			}
			ret += "}\n";
		}
		ret += "}\n\n";
		ret += "for(int " + i + " = " + numAlive + "; " + i + " < " + origSet + ".size; " + i + "++) {\n";
		{
			ret += origSet + ".elementData[" + i + "] = null;\n";
		}
		ret += "}\n";
		ret += origSet + ".size = " + numAlive + ";\n";

		return ret;
	}

	public String copyStateFromMonitor(MOPParameterPair paramPair, IndexingTree indexingTreeForCopy) throws MOPException {
		String ret = "";

		MOPParameters fromParams = paramPair.getParam2();
		MOPParameters toParams = eventParams;

		MOPVariable origMonitor = localVars.get("origMonitor");
		MOPVariable mainMonitor = localVars.get("mainMonitor");

		MOPVariable timeCheck = new MOPVariable("timeCheck");

		ret += "boolean " + timeCheck + " = true;\n\n";
		for (MOPParameter p : eventParams) {
			if (!fromParams.contains(p)) {
				MOPVariable tau = new MOPVariable("tau");

				ret += "if (" + getTempRefDisable(p) + " > " + origMonitor + "." + tau + ") {\n";
				{
					ret += timeCheck + " = false;\n";
				}
				ret += "}\n";
			}
		}
		ret += "\n";

		ret += "if (" + timeCheck + ") {\n";
		{
			ret += statManager.incMonitor(mopSpec);
			
			ret += mainMonitor + " = " + "(" + monitorClass.getOutermostName() + ")" + origMonitor + ".clone();\n";

			for (MOPParameter p : toParams) {
				if (!fromParams.contains(p)) {
					MOPVariable tempRef = localVars.getTempRef(p);
					ret += mainMonitor + "." + monitorClass.getMOPRef(p) + " = " + tempRef + ";\n";
					
					RefTree refTree = getRefTree(p);

					int tagNumber = refTree.getTagNumber(mopSpec);

					if (!refTree.isTagging())
						continue;

					if (tagNumber == -1) {
						ret += "if (" + tempRef + "." + "tau == -1){\n";
						ret += tempRef + "." + "tau = " + origMonitor + ".tau" + ";\n";
						ret += "}\n";
					} else {
						ret += "if (" + tempRef + "." + "tau[" + tagNumber + "] == -1){\n";
						ret += tempRef + "." + "tau[" + tagNumber + "] = " + origMonitor + ".tau" + ";\n";
						ret += "}\n";
					}
				}
			}

			if (event.isStartEvent())
				ret += indexingTree.attachNode(localVars, "mainMonitor", "mainMap", "mainSet");
			else
				ret += indexingTree.addMonitor(localVars, "mainMonitor", "mainMap", "mainSet");

			if (monitorInfo != null)
				ret += monitorInfo.expand(mainMonitor, monitorClass, toParams);

			for (MOPParameters param : indexingTrees.keySet()) {
				if (!param.equals(toParams) && toParams.contains(param)) {
					IndexingTree indexingTree = indexingTrees.get(param);

					ret += "\n";
					ret += indexingTree.addMonitor(localVars, "mainMonitor");
				}
			}

			for (MOPParameterPair paramPair2 : indexingTreesForCopy.keySet()) {
				if (paramPair2.getParam2().equals(toParams)) {
					IndexingTree indexingTree = indexingTreesForCopy.get(paramPair2);

					ret += "\n";
					ret += indexingTree.addMonitor(localVars, "mainMonitor");
				}
			}
		}
		ret += "}\n";

		return ret;
	}

	public String copyState() throws MOPException {
		String ret = "";
		
		if(!isGeneral)
			return ret;

		for (MOPParameterPair paramPair : paramPairsForCopy) {
			IndexingTree indexingTreeForCopy = null;
			for (MOPParameterPair paramPair2 : indexingTreesForCopy.keySet()){
				if(paramPair.equals(paramPair2)){
					indexingTreeForCopy = indexingTreesForCopy.get(paramPair2);
					break;
				}
			}

			if (!event.getMOPParametersOnSpec().contains(paramPair.getParam2())) {
				MOPVariable origSet = localVars.get("origSet");

				ret += indexingTreeForCopy.lookupSet(localVars, "origMonitor", "origMap", "origSet", false);
				ret += "if (" + origSet + "!= null) {\n";
				{
					ret += copyStateFromList(paramPair, indexingTreeForCopy);
				}
				ret += "}\n";
			} else {
				MOPVariable mainMonitor = localVars.get("mainMonitor");
				MOPVariable origMonitor = localVars.get("origMonitor");

				if (indexingTreeForCopy == null) {
					for (MOPParameters param : indexingTrees.keySet()) {
						if (param.equals(paramPair.getParam2())) {
							indexingTreeForCopy = indexingTrees.get(param);
						}
					}
				}
				
				ret += "if (" + mainMonitor + " == null) {\n";
				{
					ret += indexingTreeForCopy.lookupNode(localVars, "origMonitor", "origMap", "origSet", false);
					ret += "if (" + origMonitor + " != null) {\n";
					{
						ret += copyStateFromMonitor(paramPair, indexingTreeForCopy);
					}
					ret += "}\n";
				}
				ret += "}\n";
			}
		}

		return ret;
	}

	// opt done
	public String setMonitorRefs(MOPVariable monitor) {
		String ret = "";

		for (MOPParameter p : eventParams) {
			MOPVariable tempRef = localVars.getTempRef(p);

			ret += monitor + "." + monitorClass.getMOPRef(p) + " = " + tempRef + ";\n";
		}

		if (ret.length() > 0)
			ret += "\n";

		return ret;
	}

	// opt done
	public String addToCurrentTree() throws MOPException {
		String ret = "";

		if (!event.isStartEvent())
			return ret;

		ret += indexingTree.attachNode(localVars, "mainMonitor", "mainMap", "mainSet");

		return ret;
	}

	// opt done
	public String setTau() {
		String ret = "";

		if (!isGeneral)
			return ret;

		MOPVariable mainMonitor = localVars.get("mainMonitor");

		ret += mainMonitor + ".tau = " + timestamp + ";\n";

		for (MOPParameter p : eventParams) {
			MOPVariable tempRef = localVars.getTempRef(p);
			RefTree refTree = getRefTree(p);

			int tagNumber = refTree.getTagNumber(mopSpec);

			if (!refTree.isTagging())
				continue;

			if (tagNumber == -1) {
				ret += "if (" + tempRef + "." + "tau == -1){\n";
				ret += tempRef + "." + "tau = " + timestamp + ";\n";
				ret += "}\n";
			} else {
				ret += "if (" + tempRef + "." + "tau[" + tagNumber + "] == -1){\n";
				ret += tempRef + "." + "tau[" + tagNumber + "] = " + timestamp + ";\n";
				ret += "}\n";
			}
		}

		ret += timestamp + "++;\n";

		return ret;
	}

	// opt done
	public String addToAllCompatibleTrees() throws MOPException {
		String ret = "";

		for (MOPParameters param : indexingTrees.keySet()) {
			if (param.equals(eventParams))
				continue;

			if (!eventParams.contains(param))
				continue;

			IndexingTree indexingTree = indexingTrees.get(param);

			ret += "\n";
			ret += indexingTree.addMonitor(localVars, "mainMonitor");
		}

		for (MOPParameterPair paramPair : indexingTreesForCopy.keySet()) {
			if (!paramPair.getParam2().equals(eventParams))
				continue;

			IndexingTree indexingTree = indexingTreesForCopy.get(paramPair);

			ret += "\n";
			ret += indexingTree.addMonitor(localVars, "mainMonitor");
		}

		return ret;
	}

	// opt done
	public String createNewMonitor(boolean doWrap) throws MOPException {
		String ret = "";

		if (!event.isStartEvent())
			return ret;

		MOPVariable mainMonitor = localVars.get("mainMonitor");

		ret += statManager.incMonitor(mopSpec);

		ret += mainMonitor + " = new " + monitorClass.getOutermostName() + "();\n";
		if (monitorInfo != null)
			ret += monitorInfo.newInfo(mainMonitor, eventParams);

		ret += "\n";
		ret += setMonitorRefs(mainMonitor);
		ret += addToCurrentTree();
		ret += setTau();
		ret += addToAllCompatibleTrees();

		if (doWrap) {
			ret = "if (" + mainMonitor + " == null) {\n" + ret + "}\n";
		}

		return ret;
	}

	// opt done
	public String getTempRefDisable(MOPParameter p) {
		String ret = "";

		RefTree refTree = getRefTree(p);
		if (!refTree.isTagging())
			return ret;

		if (parametersForDisable.contains(p)) {
			MOPVariable tempRef = localVars.getTempRef(p);

			int tagNumber = refTree.getTagNumber(mopSpec);

			if (tagNumber == -1) {
				ret += tempRef + "." + "disable";
			} else {
				ret += tempRef + "." + "disable[" + tagNumber + "]";
			}
		}

		return ret;
	}
	
	public String getTempRefTau(MOPParameter p) {
		String ret = "";

		RefTree refTree = getRefTree(p);
		if (!refTree.isTagging())
			return ret;

		if (parametersForDisable.contains(p)) {
			MOPVariable tempRef = localVars.getTempRef(p);

			int tagNumber = refTree.getTagNumber(mopSpec);

			if (tagNumber == -1) {
				ret += tempRef + "." + "tau";
			} else {
				ret += tempRef + "." + "tau[" + tagNumber + "]";
			}
		}

		return ret;
	}


	// opt done
	public String setDisable() {
		String ret = "";

		if (!isGeneral)
			return ret;


		for (MOPParameter p : eventParams) {
			if (parametersForDisable.contains(p)) {
				RefTree refTree = getRefTree(p);
				if (!refTree.isTagging())
					continue;

				ret += getTempRefDisable(p) + " = " + timestamp + ";\n";
			}
		}

		if (ret.length() > 0) {
			ret = "\n" + ret;
			ret += timestamp + "++;\n";
		}

		return ret;
	}

	// opt done
	public String handleNoMonitor() throws MOPException {
		String ret = "";

		String copyState = copyState();
		String createNewMonitor = createNewMonitor(copyState.length() > 0);
		String setDisable = setDisable();

		if (copyState.length() > 0 || createNewMonitor.length() > 0 || setDisable.length() > 0) {
			MOPVariable mainMonitor = localVars.get("mainMonitor");

			ret += "if (" + mainMonitor + " == null) {\n";
			{
				ret += copyState;
				ret += createNewMonitor;
				ret += setDisable;
			}
			ret += "}\n";
			ret += "\n";
		}

		return ret;
	}

	// opt done
	public String setCache() {
		String ret = "";

		if(cache == null)
			return ret;
		
		ret += cache.setCacheKeys(localVars);
		if (indexingTree.containsSet()) {
			MOPVariable mainSet = localVars.get("mainSet");
			ret += cache.setCacheSet(mainSet);
		}

		if (indexingTree.cache.hasNode){
			MOPVariable mainMonitor = localVars.get("mainMonitor");
			ret += cache.setCacheNode(mainMonitor);
		}

		return ret;
	}

	// opt done
	public String handleCacheMiss() throws MOPException {
		String ret = "";

		ret += retrieveIndexingTree();

		ret += handleNoMonitor();

		ret += setCache();

		return ret;
	}

	// opt done
	public String cacheResultWrap(String handleCacheMiss) {
		String ret = "";
		String cacheResultCondition = "";

		if (!indexingTree.hasCache())
			return handleCacheMiss;

		if (indexingTree.containsSet()) {
			MOPVariable mainSet = localVars.get("mainSet");
			cacheResultCondition += mainSet + " == null";
		}
		if (isUsingMonitor()) {
			MOPVariable mainMonitor = localVars.get("mainMonitor");
			if (cacheResultCondition.length() > 0)
				cacheResultCondition += " || ";
			cacheResultCondition += mainMonitor + " == null";
		}
		
		if((!isGeneral && !event.isStartEvent()) || (isGeneral && !event.isStartEvent() && paramPairsForCopy.size() == 0 && !doDisable)){
//			MOPVariable cacheHit = localVars.get("cacheHit");
			
			if (cacheResultCondition.length() > 0){
				cacheResultCondition = "(" + cacheResultCondition + ")";
			}

//			if (cacheResultCondition.length() > 0)
//				cacheResultCondition = " && " + cacheResultCondition;
//			cacheResultCondition = "!" + cacheHit + cacheResultCondition; 
			
			for (MOPParameter p : this.eventParams) {
				MOPVariable tempRef = localVars.getTempRef(p);
				cacheResultCondition += " && " + tempRef + " != " + getRefTree(p).getType() + ".NULRef";
			}
		}

		ret += "if (" + cacheResultCondition + ") {\n";
		{
			ret += handleCacheMiss;
		}
		ret += "}\n";

		ret += "\n";

		return ret;
	}

	// opt done
	public String monitoring(boolean isShutdownHook) {
		String ret = "";

		if (indexingTree.containsSet()) {
			MOPVariable mainSet = localVars.get("mainSet");

			ret += monitorSet.Monitoring(mainSet, event, null, null, this.lock, isShutdownHook);
		} else if (event.isStartEvent() && isFullParam) {
			MOPVariable mainMonitor = localVars.get("mainMonitor");
			
			ret += monitorClass.Monitoring(mainMonitor, event, null, null, this.lock, this.aspectName, false, isShutdownHook);
		} else {
			MOPVariable mainMonitor = localVars.get("mainMonitor");

			ret += "if (" + mainMonitor + " != null " + ") {\n";
			{
				ret += monitorClass.Monitoring(mainMonitor, event, null, null, this.lock, this.aspectName, false, isShutdownHook);
			}
			ret += "}\n";
		}

		return ret;
	}

	
	public String toString() {
		String ret = "";
		localVars.init();

		if (indexingTree.hasCache()) {
			ret += cacheRetrieval();
		} else {
			ret += refRetrievalFromTree();
			ret += "\n";
		}

		String handleCacheMiss;

		try{
			handleCacheMiss = handleCacheMiss();
		} catch (MOPException e){
			ret += "*** Error under GeneralAdviceBody ***\n";
			ret += e.getMessage();
			return ret;
		}

		ret += cacheResultWrap(handleCacheMiss);
		
		ret += monitoring(false);

		ret = localVars.varDecl() + ret;

		return ret;
	}
	
	public String toStringForShutdownHook() {
		String ret = "";
		localVars.init();

		if (indexingTree.hasCache()) {
			ret += cacheRetrieval();
		} else {
			ret += refRetrievalFromTree();
			ret += "\n";
		}

		String handleCacheMiss;

		try{
			handleCacheMiss = handleCacheMiss();
		} catch (MOPException e){
			ret += "*** Error under GeneralAdviceBody ***\n";
			ret += e.getMessage();
			return ret;
		}

		ret += cacheResultWrap(handleCacheMiss);
		
		ret += monitoring(true);

		ret = localVars.varDecl() + ret;

		return ret;
	}
	
}
