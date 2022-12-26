package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.FSMParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Symbol;

public class FSMPlugin extends LogicPlugin {
    
    /**
     * Apply the Finite State Machine plugin to some input.
     * @param logicInputXML The bare Finite State Machine input.
     * @return The annotated and minimized Finite State Machine output.
     */
    public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
        
        String logic = logicInputXML.getProperty().getLogic();
        logic = logic.toUpperCase();
        
        if (!logic.equals("FSM")) {
            throw new LogicException("incorrect logic type: " + logic);
        }

        HashSet<Symbol> events = new HashSet<>();
        ArrayList<State> categories = new ArrayList<>();
        
        for (String event : (logicInputXML.getEvents().trim()).split("\\s+")){
            events.add(Symbol.get(event));
        }
        
        for (String category : (logicInputXML.getCategories().trim()).split("\\s+")){
            categories.add(State.get(category));
        }
        
        String fsm = logicInputXML.getProperty().getFormula();
        
        // parse the input fsm in order to acquire the
        // stateMap, aliases, and state list
        FSMParser fsmParser = FSMParser.parse(fsm);
        LogicRepositoryType logicOutputXML = logicInputXML;
        logicOutputXML.getMessage().add("START STATE: " + fsmParser.getStartState());
        logicOutputXML.getMessage().add("START STATES: " + fsmParser.getStates());
        logicOutputXML.getMessage().add("START EVENTS: " + fsmParser.getEvents());
        logicOutputXML.getMessage().add("START ALIASES: " + fsmParser.getAliases());
        logicOutputXML.getMessage().add("START STATE-MAP: " + fsmParser.getStateMap());
        // check to see that all used states are defined
        fsmParser.check();

        // check to see that all used events are defined
        HashSet<Symbol> usedEvents = fsmParser.getEvents();
        if (!usedEvents.equals(events)) {
            boolean error = false;
            String msg = "The following events are used but not defined: ";
            for (Symbol sym : usedEvents) {
                if (!events.contains(sym)) {
                    msg += sym + " ";
                    error = true;
                }
            }
            if (error) {
                throw new LogicException(msg);
            }
        }

        // minimize the FSM
        FSMMin fsmMin = new FSMMin(fsmParser.getStartState(), events, fsmParser.getStates(), categories, fsmParser.getAliases(), fsmParser.getStateMap());
        logicOutputXML.getMessage().add("MINIMIZED: " + fsmMin.FSMString());
        // compute the enables
        FSMEnables fsmEnables = new FSMEnables(fsmMin.getStartState(), events, fsmMin.getStates(), categories, fsmMin.getAliases(), fsmMin.getStateMap());
        logicOutputXML.getMessage().add("REACHABILITY: " + fsmEnables.reachability);
        logicOutputXML.getMessage().add("STATEMAP: " + fsmEnables.stateMap);
        // compute the coenables
        FSMCoenables fsmCoenables = new FSMCoenables(fsmMin.getStartState(), events, fsmMin.getStates(), categories, fsmMin.getAliases(), fsmMin.getStateMap());
        logicOutputXML.getMessage().add("FULL STATE: " + fsmCoenables.fullStateMap);
        logicOutputXML.getMessage().add("INVERSE STATE: " + fsmCoenables.inversedStateMap);
        logicOutputXML.getMessage().add("REACHABLE STATES: " + fsmCoenables.reachableStates);

        logicOutputXML.getMessage().add("done");
        
        logicOutputXML.getProperty().setFormula(fsmMin.FSMString());
        logicOutputXML.setCreationEvents(fsmEnables.creationEvents());
        logicOutputXML.setEnableSets(fsmEnables.toString() + fsmCoenables.toString());
        
        return logicOutputXML;
        
    }
    
    /**
     * The instance of the plugin used in the standalone utility.
     */
    static protected FSMPlugin plugin = new FSMPlugin();
    
    /**
     * Run the plugin as a standalone program, accepting input from stdin and producing output on stdout.
     */
    public static void main(String[] args) {

        LogicRepositoryType logicOutputXML = null;
        try {
            // Parse Input
            LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);

            // use plugin main function
            if (plugin == null) {
                throw new LogicException("Each plugin should initiate plugin field.");
            }
            logicOutputXML = plugin.process(logicInputData.getXML());

            if (logicOutputXML == null) {
                throw new LogicException("no output from the plugin.");
            }

            ByteArrayOutputStream logicOutput = new LogicRepositoryData(logicOutputXML).getOutputStream();
            logicOutputXML.getMessage().add(String.valueOf(logicOutput));
        } catch (LogicException e) {
            logicOutputXML.getMessage().add(e.getMessage());
        }

    }
    
}
