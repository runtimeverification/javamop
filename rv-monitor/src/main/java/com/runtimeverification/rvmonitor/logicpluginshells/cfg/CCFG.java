package com.runtimeverification.rvmonitor.logicpluginshells.cfg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.runtimeverification.rvmonitor.c.rvc.CSpecification;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.parser.CFGParser;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.util.CFG;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.util.GLRGen;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.util.LR;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.util.Terminal;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.FileUtils;
import com.runtimeverification.rvmonitor.util.RVMException;

public class CCFG extends LogicPluginShell {
    private CSpecification cSpec;
    private boolean parametric;

    public CCFG() {
        super();
        monitorType = "CFG";
        outputLanguage = "C";
        this.cSpec = null;
    }

    public CCFG(CSpecification cSpec, boolean parametric) {
        super();
        monitorType = "CFG";
        outputLanguage = "C";
        this.cSpec = cSpec;
        this.parametric = parametric;
    }

    ArrayList<String> allEvents;

    private ArrayList<String> getEvents(String eventStr) {
        ArrayList<String> events = new ArrayList<String>();

        for (String event : eventStr.trim().split(" ")) {
            if (event.trim().length() != 0)
                events.add(event.trim());
        }

        return events;
    }

    private Properties getMonitorCode(LogicRepositoryType logicOutput)
            throws RVMException {

        String rvcPrefix = "__RVC_";
        Properties result = new Properties();

        String monitor = logicOutput.getProperty().getFormula();
        String specName = logicOutput.getSpecName() + "_";
        String constSpecName = specName.toUpperCase();

        result.setProperty("rvcPrefix", rvcPrefix);
        result.setProperty("specName", specName);
        result.setProperty("constSpecName", constSpecName);

        CFG g = null;
        try {
            g = CFGParser.parse(new ByteArrayInputStream(monitor.getBytes()))
                    .getCFG();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException("CCFG Shell cannot parse CFG formula");
        }

        if (g == null)
            throw new RVMException("CCFG Shell cannot parse CFG formula");
        g.simplify();

        List<String> monitoredEvents;
        monitoredEvents = allEvents;

        Map<String, String> constEventNames = new HashMap<String, String>();

        for (String event : monitoredEvents) {
            String constEventName = rvcPrefix + constSpecName
                    + event.toUpperCase();
            constEventNames.put(event, constEventName);
        }

        // NB EOF will always be 0
        Map<String, Integer> tsmap = new HashMap<String, Integer>();
        int tnum = 1;

        for (String event : monitoredEvents) {
            tsmap.put(event, new Integer(tnum));

            tnum++;
        }

        HashMap<Terminal, Integer> tmap = new HashMap<Terminal, Integer>();
        for (String event : monitoredEvents) {
            tmap.put(new Terminal(event), tsmap.get(event));
        }

        LR lr = new LR(g, tmap);

        if (!(tmap.keySet().containsAll(g.terminals())))
            throw new RVMException(
                    "Terminals in CFG differ from declared events");

        // result.put("monitored events", monitoredEventsStr);

        result.setProperty("state declaration", FileUtils.extractFileFromJar(this, "int_stack.h")
                + GLRGen.cstate(lr) + "\n");

        String resetName = rvcPrefix + specName + "reset";
        result.setProperty("reset", "void\n" + resetName + "(void){\n" + GLRGen.creset(lr) + "}\n"
                + "void\n __RV_init(void){\n" + GLRGen.cinit(lr)
                + "}\n");

        result.setProperty("monitoring body", FileUtils.extractFileFromJar(this, "monitor.h"));

        String catString = "int " + rvcPrefix + specName + "match = 0;\n"
                + "int " + rvcPrefix + specName + "fail = 0;\n";

        String condString = rvcPrefix + specName + "match = __RV_cat == 0;\n"
                + rvcPrefix + specName + "fail = __RV_cat == 2;\n";

        result.setProperty("categories", catString);

        // result.put("match condition", "__RV_cat == 0\n");
        // result.put("fail condition", "__RV_cat == 2\n");

        // result.put("nonfail condition", "__RV_cat != 2\n");

        StringBuilder headerDecs = new StringBuilder();
        StringBuilder eventFuncs = new StringBuilder();

        headerDecs.append("void\n");
        headerDecs.append(resetName + "(void);\n");
        for (String eventName : monitoredEvents) {
            headerDecs.append("void\n");
            eventFuncs.append("void\n");
            String funcDecl = rvcPrefix + specName + eventName
                    + cSpec.getParameters().get(eventName);
            headerDecs.append(funcDecl + ";\n");
            eventFuncs.append(funcDecl + "\n");
            eventFuncs.append("{\n");
            eventFuncs.append(cSpec.getEvents().get(eventName) + "\n");
            eventFuncs.append("if(__RV_stacks_inst == NULL){\n");
            eventFuncs
                    .append("__RV_stacks_inst = __RV_new_RV_stacks(5);\n __RV_init();\n}\n");
            eventFuncs.append("monitor(" + tsmap.get(eventName) + ");\n");
            eventFuncs.append(condString);
            for (String category : cSpec.getHandlers().keySet()) {
                eventFuncs.append("if(" + rvcPrefix + specName + category
                        + ")\n{\n");
                eventFuncs.append(cSpec.getHandlers().get(category)
                        .replaceAll("__RESET", resetName + "()\n"));
                eventFuncs.append("}\n");
            }
            eventFuncs.append("}\n\n");
        }

        result.setProperty("header declarations", headerDecs.toString());
        result.setProperty("event functions", eventFuncs.toString());

        return result;
    }

    @Override
    public LogicPluginShellResult process(LogicRepositoryType logicOutputXML,
            String events) throws RVMException {
        if (logicOutputXML.getProperty().getLogic().toLowerCase()
                .compareTo(monitorType.toLowerCase()) != 0)
            throw new RVMException(
                    "Wrong type of monitor is given to CFG Monitor.");
        allEvents = getEvents(events);

        LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
        logicShellResult.startEvents = getEvents(logicOutputXML
                .getCreationEvents());
        // logicShellResult.startEvents = allEvents;
        logicShellResult.properties = getMonitorCode(logicOutputXML);
        logicShellResult.properties = addEnableSets(
                logicShellResult.properties, logicOutputXML);

        return logicShellResult;
    }
}
