package com.runtimeverification.rvmonitor.logicpluginshells.tfsm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.runtimeverification.rvmonitor.c.rvc.CSpecification;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.parser.FSMParser;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;

public class CTFSM extends LogicPluginShell {
    private CSpecification cSpec;
    private boolean parametric;

    public CTFSM() {
        super();
        monitorType = "TFSM";
        outputLanguage = "C";
        this.cSpec = null;
    }

    public CTFSM(CSpecification cSpec, boolean parametric) {
        super();
        monitorType = "TFSM";
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
        if (parametric) {
            return getParametricMonitorCode(logicOutput);
        } else {
            return getNonParametricMonitorCode(logicOutput);
        }
    }

    private Properties getNonParametricMonitorCode(
            LogicRepositoryType logicOutput) throws RVMException {
        String rvcPrefix = "__RVC_";
        Properties result = new Properties();

        String monitor = logicOutput.getProperty().getFormula();
        String specName = logicOutput.getSpecName() + "_";
        String constSpecName = specName.toUpperCase();

        result.setProperty("rvcPrefix", rvcPrefix);
        result.setProperty("specName", specName);
        result.setProperty("constSpecName", constSpecName);

        FSMInput fsmInput = null;
        try {
            fsmInput = FSMParser.parse(new ByteArrayInputStream(monitor
                    .getBytes()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException(
                    "FSM to C LogicPluginShell cannot parse FSM formula");
        }

        if (fsmInput == null)
            throw new RVMException(
                    "FSM to C LogicPluginShell cannot parse FSM formula");

        List<String> monitoredEvents;
        Map<String, String> eventArrays = new HashMap<String, String>(
                allEvents.size());
        monitoredEvents = allEvents;

        Map<String, Integer> EventNum = new HashMap<String, Integer>();
        int countEvent = 1;

        Map<String, String> constEventNames = new HashMap(allEvents.size());

        for (String event : monitoredEvents) {
            EventNum.put(event, new Integer(countEvent));
            String constEventName = rvcPrefix + constSpecName
                    + event.toUpperCase();
            eventArrays.put(event, "static int " + constEventName + "[] = {");
            constEventNames.put(event, constEventName);
        }

        Map<String, Integer> stateNum = new HashMap<String, Integer>();
        int countState = 0;

        if (fsmInput.getItems() != null) {
            for (FSMItem i : fsmInput.getItems()) {
                String stateName = i.getState();
                stateNum.put(stateName, new Integer(countState));
                countState++;
            }
        }

        result.setProperty("reset", "void\n" + rvcPrefix + specName
                + "reset(void)\n{\n  __RVC_state = 0;\n }\n");

        for (FSMItem i : fsmInput.getItems()) {
            int num = stateNum.get(i.getState());
            Set<String> unseenEvents = new HashSet<String>(monitoredEvents);
            int defaultState = -1;
            for (FSMTransition t : i.getTransitions()) {
                if (t.isDefaultFlag()) {
                    defaultState = stateNum.get(t.getStateName());
                } else {
                    String eventName = t.getEventName();
                    unseenEvents.remove(eventName);
                    String arrayString = eventArrays.get(eventName);
                    eventArrays
                            .put(eventName,
                                    arrayString
                                            + stateNum.get(t.getStateName())
                                            + ", ");
                }
            }
            for (String event : unseenEvents) {
                String arrayString = eventArrays.get(event);
                eventArrays.put(event, arrayString + defaultState + ",");
            }
        }

        for (String event : monitoredEvents) {
            String arrayString = eventArrays.get(event);
            eventArrays.put(event, arrayString + "};\n");
        }

        String monitoringbodyString = "";

        for (String eventArray : eventArrays.values()) {
            monitoringbodyString += eventArray;
        }

        addStateDeclaration(result);

        // figure out which categories we actually care about
        HashSet<String> cats = new HashSet();
        for (String category : logicOutput.getCategories().split("\\s+")) {
            cats.add(category);
        }
        String catString = "";

        String condString = "";
        // add fail if necessary
        if (cats.contains("fail")) {
            catString += "int " + rvcPrefix + specName + "fail = 0;\n";
            condString += "  " + rvcPrefix + specName
                    + "fail = __RVC_state == -1;\n";
        }

        for (String stateName : stateNum.keySet()) {
            if (!cats.contains(stateName))
                continue;
            String catName = rvcPrefix + specName + stateName;
            condString += "  " + catName + " = __RVC_state == "
                    + stateNum.get(stateName) + ";\n";
            catString += "int " + catName + " = 0;\n";
        }

        if (fsmInput.getAliases() != null) {
            for (FSMAlias a : fsmInput.getAliases()) {
                String stateName = a.getGroupName();
                if (!cats.contains(stateName))
                    continue;
                String conditionStr = "";
                boolean firstFlag = true;
                for (String state : a.getStates()) {
                    if (firstFlag) {
                        conditionStr += "__RVC_state == " + stateNum.get(state);
                        firstFlag = false;
                    } else {
                        conditionStr += " || __RVC_state == "
                                + stateNum.get(state);
                    }
                }
                String catName = rvcPrefix + specName + stateName;
                condString += "  " + catName + " = " + conditionStr + ";\n";
                catString += "int " + catName + " = 0;\n";
            }
        }

        // TODO: fixme
        if (catString.equals("")) {
            throw new RuntimeException(
                    "Invalid handlers specified.  Make sure your logic supports your specified handlers");
        }

        StringBuilder headerDecs = new StringBuilder();
        StringBuilder eventFuncs = new StringBuilder();

        String resetName = rvcPrefix + specName + "reset";

        // add timeout if necessary
        if (cats.contains("timeout")) {
            monitoringbodyString += "#include <sys/time.h>\n"
                    + "#include <signal.h>\n\n"
                    + "void "
                    + rvcPrefix
                    + "Handle_Timeout(void) {\n  "
                    + cSpec.getHandlers().get("timeout")
                            .replaceAll("__RESET", resetName + "()\n")
                    + "\n}\n\n"
                    + "void "
                    + rvcPrefix
                    + "Trigger_Timeout() {\n"
                    + "  struct itimerval it_val;\n"
                    + "  if (signal(SIGALRM, (void (*)(int)) "
                    + rvcPrefix
                    + "Handle_Timeout) == SIG_ERR) {\n"
                    + "    perror(\"Unable to catch SIGALRM\");\n    exit(1);\n"
                    + "  }\n"
                    + "  it_val.it_value.tv_sec = 1;\n"
                    + "  it_val.it_value.tv_usec = 0;\n"
                    + "  it_val.it_interval.tv_sec = 0;\n"
                    + "  it_val.it_interval.tv_usec = 0;\n"
                    + "  if (setitimer(ITIMER_REAL, &it_val, NULL) == -1) {\n"
                    + "    perror(\"error calling setitimer()\");\n    exit(1);\n"
                    + "  }\n" + "}\n";
            condString += "  " + rvcPrefix + "Trigger_Timeout();\n";
        }

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
            eventFuncs.append("__RVC_state = " + constEventNames.get(eventName)
                    + "[__RVC_state];\n");
            eventFuncs.append(condString);
            for (String category : cSpec.getHandlers().keySet()) {
                if (category.toLowerCase().compareTo("timeout") == 0)
                    continue;
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
        result.setProperty("monitoring body", monitoringbodyString);

        result.setProperty("categories", catString);

        return result;
    }

    private Properties getParametricMonitorCode(LogicRepositoryType logicOutput)
            throws RVMException {
        String rvcPrefix = "__RVC_";
        Properties result = new Properties();

        String monitor = logicOutput.getProperty().getFormula();
        String specName = logicOutput.getSpecName() + "_";
        String constSpecName = specName.toUpperCase();

        result.setProperty("rvcPrefix", rvcPrefix);
        result.setProperty("specName", specName);
        result.setProperty("constSpecName", constSpecName);

        FSMInput fsmInput = null;
        try {
            fsmInput = FSMParser.parse(new ByteArrayInputStream(monitor
                    .getBytes()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RVMException(
                    "FSM to C LogicPluginShell cannot parse FSM formula");
        }

        if (fsmInput == null)
            throw new RVMException(
                    "FSM to C LogicPluginShell cannot parse FSM formula");

        List<String> monitoredEvents;
        Map<String, String> eventArrays = new HashMap<String, String>(
                allEvents.size());
        monitoredEvents = allEvents;

        Map<String, Integer> EventNum = new HashMap<String, Integer>();
        int countEvent = 1;

        Map<String, String> constEventNames = new HashMap(allEvents.size());

        for (String event : monitoredEvents) {
            EventNum.put(event, new Integer(countEvent));
            String constEventName = rvcPrefix + constSpecName
                    + event.toUpperCase();
            eventArrays.put(event, "static int " + constEventName + "[] = {");
            constEventNames.put(event, constEventName);
        }

        Map<String, Integer> stateNum = new HashMap<String, Integer>();
        int countState = 0;

        if (fsmInput.getItems() != null) {
            for (FSMItem i : fsmInput.getItems()) {
                String stateName = i.getState();
                stateNum.put(stateName, new Integer(countState));
                countState++;
            }
        }

        result.setProperty("reset", "void\n"
                + rvcPrefix
                + specName
                + "reset(void *key)\n{\n  __RV_find(list,key)->__RVC_state = 0;\n }\n");

        for (FSMItem i : fsmInput.getItems()) {
            int num = stateNum.get(i.getState());
            Set<String> unseenEvents = new HashSet<String>(monitoredEvents);
            int defaultState = -1;
            for (FSMTransition t : i.getTransitions()) {
                if (t.isDefaultFlag()) {
                    defaultState = stateNum.get(t.getStateName());
                } else {
                    String eventName = t.getEventName();
                    unseenEvents.remove(eventName);
                    String arrayString = eventArrays.get(eventName);
                    eventArrays
                            .put(eventName,
                                    arrayString
                                            + stateNum.get(t.getStateName())
                                            + ", ");
                }
            }
            for (String event : unseenEvents) {
                String arrayString = eventArrays.get(event);
                eventArrays.put(event, arrayString + defaultState + ",");
            }
        }

        for (String event : monitoredEvents) {
            String arrayString = eventArrays.get(event);
            eventArrays.put(event, arrayString + "};\n");
        }

        String monitoringbodyString = "";

        for (String eventArray : eventArrays.values()) {
            monitoringbodyString += eventArray;
        }

        // figure out which categories we actually care about
        HashSet<String> cats = new HashSet();
        for (String category : logicOutput.getCategories().split("\\s+")) {
            cats.add(category);
        }

        addParametricStateDeclaration(result, cats);
        String catString = "";

        String condString = "";
        // add fail if necessary
        if (cats.contains("fail")) {
            condString += "  mon->" + rvcPrefix + specName
                    + "fail = mon->__RVC_state == -1;\n";
        }

        for (String stateName : stateNum.keySet()) {
            if (!cats.contains(stateName))
                continue;
            String catName = rvcPrefix + specName + stateName;
            condString += "  mon->" + catName + " = mon->__RVC_state == "
                    + stateNum.get(stateName) + ";\n";
        }

        if (fsmInput.getAliases() != null) {
            for (FSMAlias a : fsmInput.getAliases()) {
                String stateName = a.getGroupName();
                if (!cats.contains(stateName))
                    continue;
                String conditionStr = "";
                boolean firstFlag = true;
                for (String state : a.getStates()) {
                    if (firstFlag) {
                        conditionStr += "mon->__RVC_state == "
                                + stateNum.get(state);
                        firstFlag = false;
                    } else {
                        conditionStr += " || mon->__RVC_state == "
                                + stateNum.get(state);
                    }
                }
                String catName = rvcPrefix + specName + stateName;
                condString += "  mon->" + catName + " = " + conditionStr
                        + ";\n";
            }
        }

        StringBuilder headerDecs = new StringBuilder();
        StringBuilder eventFuncs = new StringBuilder();

        String resetName = rvcPrefix + specName + "reset";

        headerDecs.append("void\n");
        headerDecs.append(resetName + "(void *key);\n");

        for (String eventName : monitoredEvents) {
            headerDecs.append("void\n");
            eventFuncs.append("void\n");
            String funcDecl = rvcPrefix + specName + eventName
                    + cSpec.getPParameters().get(eventName);
            headerDecs.append(funcDecl + ";\n");
            eventFuncs.append(funcDecl + "\n");
            eventFuncs.append("{\n");
            eventFuncs.append(cSpec.getEvents().get(eventName) + "\n");
            eventFuncs.append("if(list == NULL){\n");
            eventFuncs.append("  list = __RV_new_RV_list(10);\n");
            eventFuncs.append("}\n");
            eventFuncs.append("__RV_monitor *mon = __RV_find(list, key);\n");
            eventFuncs.append("if(mon == NULL){\n");
            eventFuncs
                    .append("  __RV_tag_monitor *tm = __RV_new_RV_tag_monitor(key,0);\n");
            eventFuncs.append("  mon = tm->monitor;\n");
            eventFuncs.append("  __RV_append(list,tm);\n");
            eventFuncs.append("}\n");
            eventFuncs.append("mon->__RVC_state = "
                    + constEventNames.get(eventName) + "[mon->__RVC_state];\n");
            eventFuncs.append(condString);
            for (String category : cSpec.getHandlers().keySet()) {
                eventFuncs.append("if(mon->" + rvcPrefix + specName + category
                        + ")\n{\n");
                eventFuncs.append(cSpec.getHandlers().get(category)
                        .replaceAll("__RESET", resetName + "()\n"));
                eventFuncs.append("}\n");
            }
            eventFuncs.append("}\n\n");
        }

        result.setProperty("header declarations", headerDecs.toString());
        result.setProperty("event functions", eventFuncs.toString());
        result.setProperty("monitoring body", monitoringbodyString);

        result.setProperty("categories", catString);

        return result;
    }

    static private void addStateDeclaration(Properties p) {
        String stateDecl = "static int __RVC_state = 0; \n";
        p.setProperty("state declaration", stateDecl);
    }

    static private void addParametricStateDeclaration(Properties p,
            HashSet<String> cats) {
        String specName = (String) p.getProperty("specName");
        String rvcPrefix = (String) p.getProperty("rvcPrefix");
        String ret = "typedef struct monitor {\n" + "  int __RVC_state;\n";
        for (String cat : cats) {
            ret += "int " + rvcPrefix + specName + cat + ";\n";
        }
        ret += "} __RV_monitor;\n"
                + "\n"
                + "typedef struct tag_mon {\n"
                + "  void *key;\n"
                + "  __RV_monitor *monitor;\n"
                + "} __RV_tag_monitor;\n"
                + "\n"
                + "typedef struct list{\n"
                + "  int current_index;\n"
                + "  int length;\n"
                + "  __RV_tag_monitor **data;\n"
                + "} __RV_list;\n"
                + "\n"
                + "static __RV_list *list = NULL;\n"
                + "static void __RV_delete_RV_list(__RV_list *list){\n"
                + "  if(list == NULL) return;\n"
                + "  free(list->data);\n"
                + "  free(list);\n"
                + "}\n"
                + "\n"
                + "static __RV_list *__RV_new_RV_list(int size){\n"
                + "  __RV_list *ret = (__RV_list *) malloc(sizeof(__RV_list));\n"
                + "  ret->current_index = 0;\n"
                + "  ret->length = size;\n"
                + "  ret->data = (__RV_tag_monitor **) malloc(sizeof(__RV_tag_monitor *) * size);\n"
                + "  return ret; \n"
                + "}\n"
                + "\n"
                + "static __RV_tag_monitor  *__RV_new_RV_tag_monitor(void *key, int state){\n"
                + "  __RV_monitor *mon = (__RV_monitor *) malloc(sizeof(__RV_monitor));\n"
                + "  __RV_tag_monitor *ret = (__RV_tag_monitor *) malloc(sizeof(__RV_tag_monitor));\n"
                + "  \n"
                + "  mon->__RVC_state = state;\n"
                + "  ret->monitor = mon;\n"
                + "  ret->key = key;\n"
                + "  return ret;\n"
                + "}\n"
                + "\n"
                + "static void __RV_append(__RV_list *list, __RV_tag_monitor *elem){\n"
                + "  if(list->current_index >= list->length) {\n"
                + "    int i;\n"
                + "    __RV_tag_monitor **tmp \n"
                + "       = (__RV_tag_monitor **) malloc(sizeof(__RV_tag_monitor *) * list->length * 2);\n"
                + "    for(i = 0; i < list->length; ++i){\n"
                + "      tmp[i] = list->data[i];\n"
                + "    } \n"
                + "    list->length *= 2;\n"
                + "    free(list->data);\n"
                + "    list->data = tmp;\n"
                + "  } \n"
                + "  list->data[(list->current_index)++] = elem;\n"
                + "}\n"
                + "\n"
                + "static __RV_monitor *__RV_find(__RV_list *list, void *tag){\n"
                + "  int i;\n"
                + "  for(i = 0; i < list->current_index; ++i){\n"
                + "    if(list->data[i]->key == tag){\n"
                + "      return list->data[i]->monitor;\n" + "    }\n"
                + "  }\n" + "  return NULL;\n" + "}\n" + "\n";
        p.setProperty("state declaration", ret);
    }

    @Override
    public LogicPluginShellResult process(LogicRepositoryType logicOutputXML,
            String events) throws RVMException {
        if (logicOutputXML.getProperty().getLogic().toLowerCase()
                .compareTo(monitorType.toLowerCase()) != 0)
            throw new RVMException(
                    "Wrong type of monitor is given to FSM Monitor.");
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
