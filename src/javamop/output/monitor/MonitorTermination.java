package javamop.output.monitor;

import java.util.List;

import javamop.MOPNameSpace;
import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.aspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class MonitorTermination {

	MOPParameters parameters;
	List<EventDefinition> events;
	OptimizedCoenableSet coenableSet;

	MOPStatistics stat;
	
	public MonitorTermination(String name, JavaMOPSpec mopSpec, List<EventDefinition> events, OptimizedCoenableSet coenableSet){
		this.parameters = mopSpec.getParameters();
		this.events = events;
		this.coenableSet = coenableSet;
		
		this.stat = new MOPStatistics(name, mopSpec);
	}
	
	public String copyAliveParameters(MOPVariable toMonitor, MOPVariable fromMonitor){
		String ret = "";
		
		for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
			MOPVariable alive_parameter = new MOPVariable("alive_parameters_" + j);
			
			ret += toMonitor + "." + alive_parameter + " = " + fromMonitor + "." + alive_parameter + ";\n";
		}
		
		return ret;
	}
	
	public String toString(){
		String ret = "";
		
		for (MOPParameter param : parameters) {
			ret += "public javamoprt.MOPWeakReference " + MOPNameSpace.getMOPVar("MOPRef_" + param.getName()) + ";\n";
		}
		ret += "\n";

		for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
			ret += "//alive_parameters_" + j + " = " + coenableSet.getParameterGroups().get(j) + "\n";
			ret += "public boolean " + new MOPVariable("alive_parameters_" + j) + " = true;\n";
		}
		ret += "\n";
		
		ret += "public final void endObject(int idnum){\n";

		ret += "switch(idnum){\n";
		for (int i = 0; i < parameters.size(); i++) {
			ret += "case " + i + ":\n";

			for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
				if (coenableSet.getParameterGroups().get(j).contains(parameters.get(i)))
					ret += MOPNameSpace.getMOPVar("alive_parameters_" + j) + " = false;\n";
			}

			ret += "break;\n";
		}
		ret += "}\n";

		// do endObject event
		ret += "switch(MOP_lastevent) {\n";
		ret += "case -1:\n";
		ret += "return;\n";
		for (EventDefinition event : this.events) {
			ret += "case " + event.getIdNum() + ":\n";
			ret += "//" + event.getId() + "\n";

			MOPParameterSet simplifiedDNF = coenableSet.getEnable(event.getId());
			if (simplifiedDNF.size() == 1 && simplifiedDNF.get(0).size() == 0) {
				ret += "return;\n";
			} else {
				boolean firstFlag = true;

				ret += "//";
				for (MOPParameters param : simplifiedDNF) {
					if (firstFlag) {
						firstFlag = false;
					} else {
						ret += " || ";
					}
					boolean firstFlag2 = true;
					for (MOPParameter s : param) {
						if (firstFlag2) {
							firstFlag2 = false;
						} else {
							ret += " && ";
						}

						ret += "alive_" + s.getName();
					}
				}
				ret += "\n";

				ret += "if(!(";
				firstFlag = true;
				for (MOPParameters param : simplifiedDNF) {
					if (firstFlag) {
						firstFlag = false;
					} else {
						ret += " || ";
					}
					ret += "alive_parameters_" + coenableSet.getParameterGroups().getIdnum(param);
				}
				ret += ")){\n";
				ret += "MOP_terminated = true;\n";
				
				if (Main.statistics) {
					ret += stat.incTerminatedMonitor();
				}
				
				ret += "return;\n";
				ret += "}\n";
				ret += "break;\n";
				ret += "\n";
			}
		}
		ret += "}\n";

		ret += "return;\n";
		
		ret += "}\n";
		ret += "\n";

		if (Main.statistics) {
			ret += "protected void finalize() throws Throwable {\n";
			ret += "try {\n";
			ret += 	stat.incCollectedMonitor();
			ret += "} finally {\n";
			ret += "super.finalize();\n";
			ret += "}\n";
			ret += "}\n";
		}
		
		return ret;
	}
}
