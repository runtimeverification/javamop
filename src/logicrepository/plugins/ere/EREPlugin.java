package logicrepository.plugins.ere;

import java.io.*;
import java.util.*;
import logicrepository.LogicException;
import logicrepository.LogicRepositoryData;
import logicrepository.Main;
import logicrepository.parser.logicrepositorysyntax.*;
import logicrepository.plugins.*;

public class EREPlugin extends LogicPlugin {
	
	public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
		String logicStr = logicInputXML.getProperty().getFormula();
		String eventsStr = logicInputXML.getEvents();
		eventsStr.replaceAll("\\s+", " ");

		String[] eventStrings = eventsStr.split(" ");
		Symbol[] events = new Symbol[eventStrings.length];
		for (int i = 0; i < eventStrings.length; ++i) {
			events[i] = Symbol.get(eventStrings[i]);
		}

		EREParser ereParser = EREParser.parse(logicStr);
		ERE ere = ereParser.getERE();

		FSM fsm = FSM.get(ere, events);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		fsm.print(ps);

		String output = os.toString();

		String logic = "fsm";
		String formula = output;

		LogicRepositoryType logicOutputXML = logicInputXML;
		logicOutputXML.getProperty().setLogic(logic);
		logicOutputXML.getProperty().setFormula(formula);

		return logicOutputXML;
	}
	
	static protected EREPlugin plugin = new EREPlugin();
	
	public static void main(String[] args) {

		try {
			// Parse Input
			LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);

			// use plugin main function
			if(plugin == null){
				throw new LogicException("Each plugin should initiate plugin field.");
			}
			LogicRepositoryType logicOutputXML = plugin.process(logicInputData.getXML());

			if (logicOutputXML == null) {
				throw new LogicException("no output from the plugin.");
			}

			ByteArrayOutputStream logicOutput = new LogicRepositoryData(logicOutputXML).getOutputStream();
			System.out.println(logicOutput);
		} catch (LogicException e) {
			System.out.println(e);
		}

	}
}
