package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.io.*;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.*;
import com.runtimeverification.rvmonitor.logicrepository.plugins.*;
import com.runtimeverification.rvmonitor.logicrepository.plugins.srs.parser.SRSParser;

public class SRSPlugin extends LogicPlugin {
    
    public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
        String logicStr = logicInputXML.getProperty().getFormula();
        String eventsStr = logicInputXML.getEvents();
        eventsStr.replaceAll("\\s+", " ");
        
        String[] eventStrings = eventsStr.split(" ");
        Symbol[] events = new Symbol[eventStrings.length];
        for (int i = 0; i < eventStrings.length; ++i) {
            events[i] = Symbol.get(eventStrings[i]);
        }
        
        SRSParser srsParser = SRSParser.parse(logicStr);
        PatternMatchAutomaton pma = srsParser.getPMA(events);
        
        String output = pma.toString();
        
        
        LogicRepositoryType logicOutputXML = logicInputXML;
        logicOutputXML.getProperty().setFormula(output);
        logicOutputXML.getMessage().add("done");
        
        return logicOutputXML;
    }
    
    static protected SRSPlugin plugin = new SRSPlugin();
    
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
