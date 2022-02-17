package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.FSMParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.Symbol;

public class TFSMPlugin extends LogicPlugin {

	public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {

		String logic = logicInputXML.getProperty().getLogic();
		logic = logic.toUpperCase();

		if (!logic.equals("TFSM")) {
			throw new LogicException("incorrect logic type: " + logic);
		}

		ArrayList<Symbol> events = new ArrayList<Symbol>();
		ArrayList<State> categories = new ArrayList<State>();

		for (String event : (logicInputXML.getEvents().trim()).split("\\s+")){
			events.add(Symbol.get(event));
		}

		for (String category : (logicInputXML.getCategories().trim()).split("\\s+")){
			categories.add(State.get(category));
		}

		String fsm = logicInputXML.getProperty().getFormula();

		// parse the imput fsm in order to acquire the
		// stateMap, aliases, and state list
		FSMParser fsmParser = FSMParser.parse(fsm);
		// check to see that all used states are defined
		fsmParser.check();

		// check to see that all used events are defined
		HashSet<Symbol> defEvents = new HashSet(events);
		HashSet<Symbol> usedEvents = fsmParser.getEvents();
		if (!usedEvents.equals(defEvents)) {
			boolean error = false;
			String msg = "The following events are used but not defined: ";
			for (Symbol sym : usedEvents) {
				if (!defEvents.contains(sym)) {
					msg += sym + " ";
					error = true;
				}
			}
			if (error)
				throw new LogicException(msg);
		}

		// minimize the FSM
		FSMMin fsmMin = new FSMMin(fsmParser.getStartState(), events, fsmParser.getStates(), categories, fsmParser.getAliases(), fsmParser.getStateMap());
		// compute the enables
		FSMEnables fsmEnables = new FSMEnables(fsmMin.getStartState(), events, fsmMin.getStates(), categories, fsmMin.getAliases(), fsmMin.getStateMap());
		// compute the coenables
		FSMCoenables fsmCoenables = new FSMCoenables(fsmMin.getStartState(), events, fsmMin.getStates(), categories, fsmMin.getAliases(), fsmMin.getStateMap());

		LogicRepositoryType logicOutputXML = logicInputXML;
		logicOutputXML.getMessage().add("done");

		logicOutputXML.getProperty().setFormula(fsmMin.FSMString());
		logicOutputXML.setCreationEvents(fsmEnables.creationEvents());
		logicOutputXML.setEnableSets(fsmEnables.toString() + fsmCoenables.toString());

		return logicOutputXML;

	}

	static protected TFSMPlugin plugin = new TFSMPlugin();

	public static void main(String[] args) {

		try {
			// Parse Input
			LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);

			// use plugin main function
			if (plugin == null) {
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
