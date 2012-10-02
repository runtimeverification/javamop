package javamop.logicpluginshells.javafsm;

import java.io.ByteArrayInputStream;
import java.util.*;

import javamop.MOPException;
import javamop.parser.logicrepositorysyntax.*;
import javamop.logicpluginshells.LogicPluginShell;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.logicpluginshells.javafsm.fsmparser.*;
import javamop.logicpluginshells.javafsm.fsmparser.ast.*;

public class JavaFSM extends LogicPluginShell {
	public JavaFSM() {
		super();
		monitorType = "FSM";
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
		
		FSMInput fsmInput = null;
		try {
			fsmInput = FSMParser.parse(new ByteArrayInputStream(monitor.getBytes()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MOPException("FSM to Java Plugin cannot parse FSM formula");
		}

		if (fsmInput == null)
			throw new MOPException("FSM to Java Plugin cannot parse FSM formula");

		HasDefaultVisitor hasDefaultVisitor = new HasDefaultVisitor();
		boolean[] hasDefault = fsmInput.accept(hasDefaultVisitor, null);

		List<String> monitoredEvents;
		monitoredEvents = allEvents;
		
		Map<String, Integer> EventNum = new HashMap<String, Integer>();
		int countEvent = 1;

		String monitoredEventsStr = "";
		
		for(String event: monitoredEvents){
			EventNum.put(event, new Integer(countEvent));
			
			monitoredEventsStr += event + ":{\n  $state$ = $transition_" + event + "$[$state$];\n}\n\n";
			
			countEvent++;
		}
		
		Map<String, Integer> StateNum = new HashMap<String, Integer>();
		Map<Integer, FSMItem> StateName = new HashMap<Integer, FSMItem>();
		int countState = 0;
		
		if(fsmInput.getItems() != null){
			for(FSMItem i : fsmInput.getItems()){
				String stateName = i.getState();
				StateNum.put(stateName, new Integer(countState));
				StateName.put(new Integer(countState), i);
				countState++;
			}
		}
		
		result.put("monitored events", monitoredEventsStr);
		
		String monitoredEventsStrForSet = "";
		for(String event: monitoredEvents){
			monitoredEventsStrForSet += event + ":{\n  $monitor$.$state$ = $transition_" + event + "$[$monitor$.$state$];\n}\n\n";
		}
		result.put("monitored events for set", monitoredEventsStrForSet);
		
		String transitionArray = "";

		for(String event : this.allEvents){
			transitionArray += "static final int $transition_" + event + "$[] = {";
			for(int i = 0; i < countState; i++){
				FSMItem state = StateName.get(new Integer(i));
				
				int default_transition = countState;

				for(FSMTransition t : state.getTransitions()){
					if(t.isDefaultFlag()){
						if(StateNum.get(t.getStateName()) == null)
							throw new MOPException("Incorrect Monitor");
						
						default_transition = StateNum.get(t.getStateName());
					}
				}

				boolean found = false;
				for(FSMTransition t : state.getTransitions()){
					if(!t.isDefaultFlag() && t.getEventName().equals(event)){
						found = true;
						if(i != 0)
							transitionArray += ", ";
						transitionArray += StateNum.get(t.getStateName());
					}
				}
				
				if(!found){
					if(i != 0)
						transitionArray += ", ";
					transitionArray += default_transition;
				}
			}

			if(countState > 0)
				transitionArray += ", ";
			transitionArray += countState;
			
			transitionArray += "};;\n";
		}
		
		result.put("state declaration", "int $state$;\n" + transitionArray);
		result.put("state declaration for set", transitionArray);
		result.put("reset", "$state$ = 0;\n");
		result.put("initialization", "$state$ = 0;\n");

		result.put("monitoring body", "");
		
		for(String state : StateNum.keySet()){
			result.put(state.toLowerCase() + " condition", "$state$ == " + StateNum.get(state));
		}
		result.put("fail condition", "$state$ == " + countState + "\n");
		//DEADLOCK CONDITION
		result.put("deadlock condition", "");
		if(fsmInput.getAliases() != null){
			for(FSMAlias a : fsmInput.getAliases()){
				String stateName = a.getGroupName();
				String conditionStr = "";
				boolean firstFlag = true;
				for(String state : a.getStates()){
					if(firstFlag){
						conditionStr += "$state$ == " + StateNum.get(state);
						firstFlag = false;
					} else{
						conditionStr += "|| $state$ == " + StateNum.get(state);
					}
				}
				
				if(a.getStates().size() == 0)
					conditionStr = "false";
				
				result.put(stateName.toLowerCase() + " condition", conditionStr);
			}
		}
		
		return result;
	}

	public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws MOPException {
		if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0)
			throw new MOPException("Wrong type of monitor is given to FSM Monitor.");
		allEvents = getEvents(events);

		LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
		logicShellResult.startEvents = getEvents(logicOutputXML.getCreationEvents());
		//logicShellResult.startEvents = allEvents;
		logicShellResult.properties = getMonitorCode(logicOutputXML);
		logicShellResult.properties = addEnableSets(logicShellResult.properties, logicOutputXML);

		return logicShellResult;
	}
}
