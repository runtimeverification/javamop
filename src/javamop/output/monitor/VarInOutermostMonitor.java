package javamop.output.monitor;

import java.util.List;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class VarInOutermostMonitor {
	MOPParameters parameters;
	List<EventDefinition> events;

	boolean isGeneral = false;
	
	MOPVariable tau = new MOPVariable("tau");

	public VarInOutermostMonitor(String name, JavaMOPSpec mopSpec, List<EventDefinition> events){
		this.parameters = mopSpec.getParameters();
		this.events = events;
		this.isGeneral = mopSpec.isGeneral();
	}
	
	public String toString(){
		String ret = "";

		if(isGeneral){
			ret += "public long " + tau + " = -1;\n";
		}
		
		return ret;
	}		
}
