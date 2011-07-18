package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class TimestampManager {

	HashMap<JavaMOPSpec, MOPVariable> timestamps = new HashMap<JavaMOPSpec, MOPVariable>();

	public TimestampManager(String name, List<JavaMOPSpec> specs) throws MOPException {
		for (JavaMOPSpec spec : specs) {
			if (spec.isGeneral())
				timestamps.put(spec, new MOPVariable(spec.getName() + "_timestamp"));
		}
	}
	
	public MOPVariable getTimestamp(JavaMOPSpec spec){
		return timestamps.get(spec);
	}

	public String decl() {
		String ret = "";

		if (timestamps.size() <= 0)
			return ret;

		ret += "// Declarations for Timestamps \n";
		for (MOPVariable timestamp : timestamps.values()) {
			ret += "static long " + timestamp + " = 1;\n";
		}
		ret += "\n";

		return ret;
	}

}
