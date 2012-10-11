package javamop.logicpluginshells.javacfg;

import java.io.ByteArrayInputStream;
import java.util.*;

import javamop.MOPException;
import javamop.parser.logicrepositorysyntax.*;
import javamop.logicpluginshells.LogicPluginShell;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.logicpluginshells.javacfg.cfgutil.*;

public class JavaCFG extends LogicPluginShell {
	public JavaCFG() {
		super();
		monitorType = "cfg";
	}

	ArrayList<String> allEvents;

	private ArrayList<String> getEvents(String eventStr) throws MOPException {
		ArrayList<String> events = new ArrayList<String>();

		for (String event : eventStr.trim().split(" ")) {
			if (event.trim().length() != 0)
				events.add(event.trim());
		}

		return events;
	}

	private Properties getMonitorCode(LogicRepositoryType logicOutput) throws MOPException {
		Properties result = new Properties();

		String monitor = logicOutput.getProperty().getFormula();

		CFG g = null;
		try {
			g = CFGParser.parse(new ByteArrayInputStream(monitor.getBytes())).getCFG();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MOPException("CFG to Java Plugin cannot parse CFG formula");
		}

		if (g == null)
			throw new MOPException("CFG to Java Plugin cannot parse CFG formula");
		g.simplify();

		List<String> monitoredEvents;
		monitoredEvents = allEvents;

		// NB EOF will always be 0
		Map<String, Integer> tsmap = new HashMap<String, Integer>();
		int tnum = 1;

		String monitoredEventsStr = "";

		for (String event : monitoredEvents) {
			tsmap.put(event, new Integer(tnum));

			monitoredEventsStr += event + ":{\n  $event$ = " + tnum + ";\n}\n\n";

			tnum++;
		}

		HashMap<Terminal, Integer> tmap = new HashMap<Terminal, Integer>();
		for (String event : monitoredEvents) {
			tmap.put(new Terminal(event), tsmap.get(event));
		}

		LR lr = new LR(g, tmap);

		if (!(tmap.keySet().containsAll(g.terminals())))
			throw new MOPException("Terminals in CFG differ from declared events");

		result.put("monitored events", monitoredEventsStr);

		result.put("state declaration", GLRGen.state(lr) + "\n" + GLRGen.intstack);
		result.put("reset", GLRGen.reset(lr));
		result.put("initialization", GLRGen.init(lr));

		result.put("monitoring body", GLRGen.body());

		result.put("match condition", "$cat$ == 0\n");
		result.put("fail condition", "$cat$ == 2\n");
		
		result.put("nonfail condition", "$cat$ != 2\n");

		result.put("clone", "$ret$.$stacks$ = new ArrayList<IntStack>();\n" 
				+ "for(int $i$ = 0; $i$ < this.$stacks$.size(); $i$++){\n"
				+ "IntStack $stack$ = this.$stacks$.get($i$);\n"
				+ "$ret$.$stacks$.add($stack$.fclone());\n" 
				+ "}\n");
		
		
		result.put("hashcode","if($stacks$.size() == 0) return 0;\n"
				+"return $stacks$.size() ^ $stacks$.get($stacks$.size() - 1).hashCode();");
		result.put("equals","if(o == null) return false;\n"
				+ "if(! (o instanceof @MONITORCLASS)) return false ;\n"
				+ "@MONITORCLASS m = (@MONITORCLASS) o;\n"
				+ "if ($stacks$.size() != m.$stacks$.size()) return false;\n"
				+ "for(int $i$ = 0; $i$ < $stacks$.size(); $i$++){\n"
				+ "IntStack $stack$ = $stacks$.get($i$);\n"
				+ "IntStack $stack2$ = m.$stacks$.get($i$);\n"
				+ "if($stack$.curr_index != $stack2$.curr_index) return false;\n"
				+ "for(int $j$ = 0; $j$ < $stack$.curr_index; $j$++){\n"
				+ "if($stack$.data[$j$] != $stack2$.data[$j$]) return false;\n"
				+ "}\n"
				+ "}\n"
				+ "return true;\n"
				);
		
		
		return result;
	}

	public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws MOPException {
		if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0)
			throw new MOPException("Wrong type of monitor is given to CFG Monitor.");
		allEvents = getEvents(events);

		LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
		logicShellResult.startEvents = getEvents(logicOutputXML.getCreationEvents());
		//logicShellResult.startEvents = allEvents;
		logicShellResult.properties = getMonitorCode(logicOutputXML);
		logicShellResult.properties = addEnableSets(logicShellResult.properties, logicOutputXML);

		return logicShellResult;
	}
}
