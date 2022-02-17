package com.runtimeverification.rvmonitor.logicpluginshells.srs;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.srs.pma.ShellPatternMatchAutomaton;
import com.runtimeverification.rvmonitor.logicpluginshells.srs.pma.Symbol;
import com.runtimeverification.rvmonitor.logicpluginshells.srs.pma.parser.PMAParser;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;

public class JavaSRS extends LogicPluginShell {
    public JavaSRS() {
        super();
        monitorType = "SRS";
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

        ShellPatternMatchAutomaton pmaInput;

        String monitor = logicOutput.getProperty().getFormula();
        try {
            pmaInput = PMAParser.parse(
                    new ByteArrayInputStream(monitor.getBytes())).getPMA();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException(
                    "SRS to Java Plugin cannot parse SRS formula");
        }

        List<String> monitoredEvents;
        monitoredEvents = allEvents;

        Map<Symbol, Integer> EventNum = pmaInput.getSymToNum();
        // System.out.println(EventNum);
        //
        StringBuilder monitoredEventsStr = new StringBuilder();
        //
        int countEvent = EventNum.size();
        // System.out.println(countEvent);
        int begin = -1, end = -1;
        if (pmaInput.hasBegin())
            begin = EventNum.get(Symbol.get("^"));
        if (pmaInput.hasEnd())
            end = EventNum.get(Symbol.get("$"));
        for (String event : monitoredEvents) {
            Symbol s = Symbol.get(event);
            if (!EventNum.containsKey(s)) {
                EventNum.put(s, new Integer(countEvent++));
            }
            monitoredEventsStr.append("\n" + event + ":{\n");
            if (pmaInput.hasBegin()) {
                monitoredEventsStr.append("if($l$.headElem() != " + begin
                        + ") {\n");
                monitoredEventsStr.append("  $l$.addFront(" + begin + ");");
                monitoredEventsStr.append("\n}\n");
            }
            if (pmaInput.hasEnd()) {
                monitoredEventsStr.append("if($l$.tailElem() != " + end
                        + ") {\n");
                monitoredEventsStr.append("  $l$.add(" + end + ");");
                monitoredEventsStr.append("\n}\n");
                monitoredEventsStr.append("$l$.insertBeforeTail("
                        + EventNum.get(s) + ");\n");
            } else {
                monitoredEventsStr
                        .append("$l$.add(" + EventNum.get(s) + ");\n");
            }
            monitoredEventsStr.append("int out = rewrite($l$);\n}}\n\n");
        }
        result.setProperty("monitored events", monitoredEventsStr.toString());
        result.setProperty("state declaration", "RVMIntSpliceList $l$;\n" + pmaInput.toImplString()
                + rewriteStr);
        result.setProperty("state declaration for set", "RVMIntSpliceList $l$;\n"
                + pmaInput.toImplString() + rewriteStr);
        result.setProperty("reset", "$l$ = new RVMIntSpliceList();\n");
        result.setProperty("initialization", "$l$ = new RVMIntSpliceList();\n");
        result.setProperty("monitoring body", "");
        result.setProperty("fail condition", "out == 0\n");
        result.setProperty("nonfail condition", "out != 0\n");
        result.setProperty("succeed condition", "out == 1\n");
        return result;
    }

    @Override
    public LogicPluginShellResult process(LogicRepositoryType logicOutputXML,
            String events) throws RVMException {
        if (logicOutputXML.getProperty().getLogic().toLowerCase()
                .compareTo(monitorType.toLowerCase()) != 0)
            throw new RVMException(
                    "Wrong type of monitor is given to SRS Monitor.");
        allEvents = getEvents(events);
        LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
        logicShellResult.startEvents = allEvents;
        logicShellResult.properties = getMonitorCode(logicOutputXML);
        logicShellResult.properties = addEnableSets(
                logicShellResult.properties, logicOutputXML);

        return logicShellResult;
    }

    static String rewriteStr = " static private int rewrite(RVMIntSpliceList l){\n"
            + "    if(l.isEmpty()) return -1;\n"
            + "    RVMSLIntIterator first;\n"
            + "    RVMSLIntIterator second;\n"
            + "    RVMSLIntIterator lastRepl = null;\n"
            + "    int currentState;\n"
            + "    RVMPMATransitionImpl trans;\n"
            + "    int symbol; \n"
            + "    boolean changed;\n"
            + "    boolean atOrPastLastChange;\n"
            + "    do {\n"
            + "    currentState = 0;\n"
            + "    atOrPastLastChange = false;\n"
            + "    changed = false;\n"
            + "    first = l.head();\n"
            + "    second = l.head();\n"
            + "    symbol = second.get();\n"
            + "    while(true){\n"
            + "      trans = $pma$[currentState][symbol];\n"
            + "      if(currentState == 0 && trans.state.number == 0){\n"
            + "        if(!first.next()) break;\n"
            + "      }\n"
            + "      else {\n"
            + "        first.next(trans.action);\n"
            + "      }\n"
            + "      if(trans.state.category == 0){\n"
            + "        return 0;\n"
            + "      }\n"
            + "      if(trans.state.category == 1){\n"
            + "        return 1;\n"
            + "      }\n"
            + "      if(trans.state.replacement != null){\n"
            + "        first.nonDestructiveSplice(second, trans.state.replacement);\n"
            + "        if(l.isEmpty()) return -1;\n"
            + "        changed = true;\n"
            + "        atOrPastLastChange = false; \n"
            + "        lastRepl = second;\n"
            + "        second = first.copy();\n"
            + "        currentState = 0;\n"
            + "        symbol = second.get();\n"
            + "        if(symbol == -1) break;\n"
            + "        continue;\n"
            + "      }\n"
            + "      currentState = trans.state.number;\n"
            + "      //normal transition\n"
            + "      if(trans.action == 0){\n"
            + "        if(!second.next()) break;\n"
            + "        if(!changed){\n"
            + "          if(second.equals(lastRepl)){\n"
            + "            atOrPastLastChange = true; \n"
            + "          }\n"
            + "          if(atOrPastLastChange && currentState == 0){\n"
            + "            return -1;\n"
            + "          }\n"
            + "        }\n"
            + "        symbol = second.get();\n"
            + "      }\n"
            + "      //fail transition, need to reconsider he same symbol in next state\n"
            + "    }\n"
            + "    } while(changed);\n"
            + "    return -1;\n"
            + "  }\n\n";

}
