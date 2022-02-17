package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.parser.EREParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class EREPlugin extends LogicPlugin {
    
    /**
     * Processes an extended regular expression definition into a finite state machine.
     * @param logicInputXML Plugin input containing an extended regular expression.
     * @return Plugin output containing a finite state machine.
     */
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
    
    /**
     * Main method to run the plugin as a standalone executable.
     * Reads plugin input as XML through stdin, and prints plugin output as XML through stdout.
     * @param args Unused.
     */
    public static void main(String[] args) {
        
        try {
            // Parse Input
            LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);
            
            // use plugin main function
            if(plugin == null) {
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
