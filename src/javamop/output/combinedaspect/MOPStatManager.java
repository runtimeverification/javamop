package javamop.output.combinedaspect;

import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.Main;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class MOPStatManager {

	HashMap<JavaMOPSpec, MOPStatistics> stats = new HashMap<JavaMOPSpec, MOPStatistics>();

	public MOPStatManager(String name, List<JavaMOPSpec> specs) throws MOPException {
		for (JavaMOPSpec spec : specs) {
			stats.put(spec, new MOPStatistics(name, spec));
		}
	}

	public MOPStatistics getStat(JavaMOPSpec spec){
		return stats.get(spec);
	}
	
	public String fieldDecl() {
		String ret = "";

		if (!Main.statistics)
			return ret;

		ret += "// Declarations for Statistics \n";
		for (MOPStatistics stat : stats.values()) {
			ret += stat.fieldDecl();
		}
		ret += "\n";

		return ret;
	}

	public String advice() {
		String ret = "";

		if (!Main.statistics)
			return ret;

		ret += "\n";
		ret += "// advices for Statistics \n";
		for (MOPStatistics stat : stats.values()) {
			ret += stat.advice();
		}

		return ret;
	}

}
