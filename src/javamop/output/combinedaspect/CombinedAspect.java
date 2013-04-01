package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.EventManager;
import javamop.output.combinedaspect.indexingtree.IndexingDecl;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.IndexingTreeManager;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class CombinedAspect {
	String name;
	public HashMap<JavaMOPSpec, MonitorSet> monitorSets;
	public HashMap<JavaMOPSpec, SuffixMonitor> monitors;
	public HashMap<JavaMOPSpec, EnableSet> enableSets;
	public HashMap<JavaMOPSpec, HashSet<MOPParameter>> setOfParametersForDisable;

	MOPVariable mapManager;
	boolean versionedStack;

	List<JavaMOPSpec> specs;
	public MOPStatManager statManager;
	public LockManager lockManager;
	public TimestampManager timestampManager;
	public ActivatorManager activatorsManager;
	public IndexingTreeManager indexingTreeManager;
	public EventManager eventManager;

	public CombinedAspect(String name, MOPSpecFile mopSpecFile, HashMap<JavaMOPSpec, MonitorSet> monitorSets, HashMap<JavaMOPSpec, SuffixMonitor> monitors,
			HashMap<JavaMOPSpec, EnableSet> enableSets, boolean versionedStack) throws MOPException {
		this.name = name + "MonitorAspect";
		this.monitorSets = monitorSets;
		this.monitors = monitors;
		this.enableSets = enableSets;
		this.versionedStack = versionedStack;

		this.specs = mopSpecFile.getSpecs();
		this.statManager = new MOPStatManager(name, this.specs);
		this.lockManager = new LockManager(name, this.specs);
		this.timestampManager = new TimestampManager(name, this.specs);
		this.activatorsManager = new ActivatorManager(name, this.specs);
		this.indexingTreeManager = new IndexingTreeManager(name, this.specs, this.monitorSets, this.monitors, this.enableSets);
		
		collectDisableParameters(mopSpecFile.getSpecs());
		
		this.eventManager = new EventManager(name, this.specs, this);

		this.mapManager = new MOPVariable(name + "MapManager");
	}
	
	public void collectDisableParameters(List<JavaMOPSpec> specs){
		this.setOfParametersForDisable = new HashMap<JavaMOPSpec, HashSet<MOPParameter>>();
		for(JavaMOPSpec spec : specs){
			HashSet<MOPParameter> parametersForDisable = new HashSet<MOPParameter>();
			
			for(EventDefinition event : spec.getEvents()){
				MOPParameters eventParams = event.getMOPParametersOnSpec();
				MOPParameterSet enable = enableSets.get(spec).getEnable(event.getId());
				
				for (MOPParameters enableEntity : enable) {
					if (enableEntity.size() == 0 && !spec.hasNoParamEvent())
						continue;
					if (enableEntity.contains(eventParams))
						continue;
					
					MOPParameters unionOfEnableEntityAndParam = MOPParameters.unionSet(enableEntity, eventParams);
					
					for (MOPParameter p : unionOfEnableEntityAndParam){
						if(!enableEntity.contains(p)){
							parametersForDisable.add(p);
						}
					}
				}
			}
			
			this.setOfParametersForDisable.put(spec, parametersForDisable);
		}
	}

	public String getAspectName() {
		return name;
	}
	
	public String constructor(){
		String ret = "";
		
		HashMap<String, RefTree> refTrees = indexingTreeManager.refTrees;
		
		for(JavaMOPSpec spec : specs){
			IndexingDecl indexDecl = indexingTreeManager.getIndexingDecl(spec);
			
			for(IndexingTree indexTree : indexDecl.getIndexingTrees().values()){
				MOPParameters param = indexTree.queryParam;
				
				if(param.size() == 0)
					continue;
				
				RefTree refTree = refTrees.get(param.get(0).getType().toString());
				
				if(refTree.hostIndexingTree != indexTree)
					ret += refTree.getName() + ".addCleaningChain(" + indexTree.getName() + ");\n";
			}
			
		}
		
		return ret;
	}

	public String initCache(){
		String ret = "";
		
		for(JavaMOPSpec spec : specs){
			IndexingDecl decl = indexingTreeManager.getIndexingDecl(spec);
		
			for(IndexingTree tree : decl.getIndexingTrees().values()){
				if(tree.cache != null){
					ret += tree.cache.init();
				}
			}
		}
		
		
		return ret;
	}

	public String toString() {
		String ret = "";

		ret += this.statManager.statClass();
		
		ret += "public aspect " + this.name + " implements javamoprt.MOPObject {\n";

		ret += "javamoprt.map.MOPMapManager " + mapManager + ";\n";

		ret += this.statManager.fieldDecl2();
		
		// constructor
		ret += "public " + this.name + "(){\n";

		ret += this.eventManager.printConstructor();
		
		ret += mapManager + " = " + "new javamoprt.map.MOPMapManager();\n";
		ret += mapManager + ".start();\n";

		ret += this.statManager.constructor();
		
		//ret += constructor();
		//ret += initCache();
		
		ret += "}\n";
		ret += "\n";

		ret += this.statManager.fieldDecl();

		ret += this.lockManager.decl();

		ret += this.timestampManager.decl();

		ret += this.activatorsManager.decl();

		ret += this.indexingTreeManager.decl();

		ret += this.eventManager.advices();

		ret += this.statManager.advice();

		if(Main.dacapo2){
			ret += "after () : (execution(* avrora.Main.main(..)) || call(* dacapo.Benchmark.run(..)) || call(* org.dacapo.harness.Benchmark.run(..))) {\n";

			//ret += "System.err.println(\"reset \" + Thread.currentThread().getName());\n";
			
			ret += this.timestampManager.reset();

			ret += this.activatorsManager.reset();

			ret += this.indexingTreeManager.reset();
			
			ret += "}\n";
		}
		
		ret += "}\n";

		return ret;
	}
	
	public String toRVString() {
		String ret = "";
		ret += this.statManager.statClass();
		ret += "public aspect " + this.name + " implements rvmonitorrt.RVMObject {\n";
		
		// Constructor
		ret += "public " + this.name + "(){\n";

		ret += this.eventManager.printConstructor();
		
		//ret += mapManager + " = " + "new javamoprt.map.MOPMapManager();\n";
		//ret += mapManager + ".start();\n";

		//ret += this.statManager.constructor();
		
		//ret += constructor();
		//ret += initCache();
		
		ret += "}\n";
		ret += "\n";
		
		
		ret += this.lockManager.decl();
		
		ret += this.eventManager.advices();
		
		if(Main.dacapo2){
			ret += "after () : (execution(* avrora.Main.main(..)) || call(* dacapo.Benchmark.run(..)) || call(* org.dacapo.harness.Benchmark.run(..))) {\n";

			//ret += "System.err.println(\"reset \" + Thread.currentThread().getName());\n";
			
			ret += this.timestampManager.reset();

			ret += this.activatorsManager.reset();

			ret += this.indexingTreeManager.reset();
			
			ret += "}\n";
		}
		
		ret += "}\n";
		return ret;
	}
}
