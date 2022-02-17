package com.runtimeverification.rvmonitor.logicpluginshells.pda;

import java.util.ArrayList;
import java.util.Properties;

import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.ast.PDA;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.parser.PDAParser;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;

public class JavaPDA extends LogicPluginShell {
    public JavaPDA() {
        super();
        monitorType = "PDA";
        outputLanguage = "java";
    }

    ArrayList<String> allEvents;

    private ArrayList<String> getEvents(String eventStr) throws RVMException {
        ArrayList<String> events = new ArrayList<String>();

        for (String event : eventStr.trim().split(" ")) {
            if (event.trim().length() != 0)
                events.add(event.trim());
        }

        return events;
    }

    private Properties getMonitorCode(LogicRepositoryType logicOutput)
            throws RVMException {
        Properties result = new Properties();
        String monitor = logicOutput.getProperty().getFormula();

        PDA code = null;
        try {
            code = PDAParser.parse(monitor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException(
                    "PTCaRet to Java Plugin cannot parse PTCaRet formula");
        }

        return result;
    }

    @Override
    public LogicPluginShellResult process(LogicRepositoryType logicOutputXML,
            String events) throws RVMException {
        if (logicOutputXML.getProperty().getLogic().toLowerCase()
                .compareTo(monitorType.toLowerCase()) != 0)
            throw new RVMException(
                    "Wrong type of monitor is given to PTCaRet Monitor.");
        allEvents = getEvents(events);

        LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
        // logicShellResult.startEvents =
        // getEvents(logicOutputXML.getCreationEvents());
        logicShellResult.startEvents = allEvents;
        logicShellResult.properties = getMonitorCode(logicOutputXML);
        logicShellResult.properties = addEnableSets(
                logicShellResult.properties, logicOutputXML);

        return logicShellResult;
    }

}
