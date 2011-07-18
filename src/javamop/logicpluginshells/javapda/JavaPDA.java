package javamop.logicpluginshells.javapda;

import java.util.ArrayList;
import java.util.Properties;

import javamop.MOPException;
import javamop.logicpluginshells.LogicPluginShell;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.logicpluginshells.javapda.ast.PDA;
import javamop.logicpluginshells.javapda.parser.PDAParser;
import javamop.parser.logicrepositorysyntax.LogicRepositoryType;

public class JavaPDA  extends LogicPluginShell {
	public JavaPDA() {
		super();
		monitorType = "PDA";
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

		PDA code = null;
		try {
			code = PDAParser.parse(monitor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MOPException("PTCaRet to Java Plugin cannot parse PTCaRet formula");
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return result;
	}
	
	public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws MOPException {
		if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0)
			throw new MOPException("Wrong type of monitor is given to PTCaRet Monitor.");
		allEvents = getEvents(events);

		LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
		// logicShellResult.startEvents =
		// getEvents(logicOutputXML.getCreationEvents());
		logicShellResult.startEvents = allEvents;
		logicShellResult.properties = getMonitorCode(logicOutputXML);
		logicShellResult.properties = addEnableSets(logicShellResult.properties, logicOutputXML);

		return logicShellResult;
	}

}
