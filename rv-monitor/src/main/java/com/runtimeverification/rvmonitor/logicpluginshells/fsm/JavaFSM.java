package com.runtimeverification.rvmonitor.logicpluginshells.fsm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.parser.FSMParser;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;

public class JavaFSM extends LogicPluginShell {

    public JavaFSM() {
        super();
        monitorType = "FSM";
        outputLanguage = "java";
    }

    ArrayList<String> allEvents;

    private ArrayList<String> getEvents(String eventStr) {
        ArrayList<String> events = new ArrayList<>();

        for (String event : eventStr.trim().split(" ")) {
            if (event.trim().length() != 0) {
                events.add(event.trim());
            }
        }

        return events;
    }

    private Properties getIntermediateMonitorCode(LogicRepositoryType logicOutput) throws RVMException {
        Properties result = new Properties();

        String formula = logicOutput.getProperty().getFormula();

        FSMInput fsmInput;
        try {
            fsmInput = FSMParser.parse(new ByteArrayInputStream(formula.getBytes()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException("FSM to Java Plugin cannot parse FSM formula");
        }

        StringBuilder monitoredEventsStr = new StringBuilder();

        for (String event : allEvents) {
            monitoredEventsStr.append(event)
                    .append(":{\n  $state$ = $transition_")
                    .append(event)
                    .append("$[$state$];\n}\n\n");
        }

        result.setProperty("monitored events", monitoredEventsStr.toString());

        StringBuilder monitoredEventsStrForSet = new StringBuilder();

        for (String event : allEvents) {
            monitoredEventsStrForSet.append(event)
                    .append(":{\n  $monitor$.$state$ = $transition_")
                    .append(event)
                    .append("$[$monitor$.$state$];\n}\n\n");
        }

        result.setProperty("monitored events for set", monitoredEventsStrForSet.toString());

        Map<String, Integer> StateNum = new HashMap<>(); // maps state names to unique IDs
        Map<Integer, FSMItem> StateName = new HashMap<>(); // maps the unique IDs to states (not just state names)
        int countState = 0;

        for (FSMItem item : fsmInput.getItems()) {
            StateNum.put(item.getState(), countState);
            StateName.put(countState, item);
            countState++;
        }

        StringBuilder transitionArray = new StringBuilder();

        for (String event : allEvents) {
            transitionArray.append("static final int $transition_").append(event).append("$[] = {");
            for (int i = 0; i < countState; i++) {
                FSMItem state = StateName.get(i);

                int default_transition = countState;

                for (FSMTransition transition : state.getTransitions()) {
                    if (transition.isDefaultFlag()) {
                        if (StateNum.get(transition.getStateName()) == null) {
                            throw new RVMException("Incorrect Monitor");
                        }
                        default_transition = StateNum.get(transition.getStateName());
                    }
                }

                boolean found = false;
                for (FSMTransition transition : state.getTransitions()) {
                    if (!transition.isDefaultFlag() && transition.getEventName().equals(event)) {
                        found = true;
                        if (i != 0)
                            transitionArray.append(", ");
                        transitionArray.append(StateNum.get(transition.getStateName()));
                    }
                }

                if (!found) {
                    if (i != 0)
                        transitionArray.append(", ");
                    transitionArray.append(default_transition);
                }
            }

            if (countState > 0)
                transitionArray.append(", ");
            transitionArray.append(countState);

            transitionArray.append("};;\n");
        }

        result.setProperty("state declaration", "int $state$;\n" + transitionArray);
        result.setProperty("state declaration for set", transitionArray.toString());
        result.setProperty("reset", "$state$ = 0;\n");
        result.setProperty("initialization", "$state$ = 0;\n");

        result.setProperty("monitoring body", "");

        for (String state : StateNum.keySet()) {
            result.setProperty(state.toLowerCase() + " condition", "$state$ == " + StateNum.get(state));
        }
        result.setProperty("fail condition", "$state$ == " + countState + "\n");
        result.setProperty("nonfail condition", "$state$ != " + countState + "\n");
        if (fsmInput.getAliases() != null) {
            for (FSMAlias a : fsmInput.getAliases()) {
                String stateName = a.getGroupName();
                StringBuilder conditionStr = new StringBuilder();
                boolean firstFlag = true;
                for (String state : a.getStates()) {
                    if (firstFlag) {
                        conditionStr.append("$state$ == ").append(StateNum.get(state));
                        firstFlag = false;
                    } else {
                        conditionStr.append("|| $state$ == ").append(StateNum.get(state));
                    }
                }

                if (a.getStates().size() == 0)
                    conditionStr = new StringBuilder("false");

                result.setProperty(stateName.toLowerCase() + " condition", conditionStr.toString());
            }
        }

        return result;
    }

    @Override
    public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws RVMException {
        if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0) {
            throw new RVMException("Wrong type of monitor is given to FSM Monitor.");
        }
        allEvents = getEvents(events);

        LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
        logicShellResult.startEvents = getEvents(logicOutputXML.getCreationEvents());
        logicShellResult.properties = getIntermediateMonitorCode(logicOutputXML);
        logicShellResult.properties = addEnableSets(logicShellResult.properties, logicOutputXML);

        return logicShellResult;
    }
}
