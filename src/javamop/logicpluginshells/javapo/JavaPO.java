package javamop.logicpluginshells.javapo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import javamop.MOPException;
import javamop.logicpluginshells.LogicPluginShell;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.parser.logicrepositorysyntax.LogicRepositoryType;

public class JavaPO extends LogicPluginShell {
	public JavaPO() {
		super();
		monitorType = "PO";
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

		PartialOrders partialOrders = null;
		try {
			partialOrders = POParser.parse(monitor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MOPException("PO to Java Plugin cannot parse PO formula");
		}

		/*
		 * deadlock checking (optional)
		 * 
		 * graph should have at least one node that has no parent.
		 * 
		 * Also, there should be no loop
		 */
		// do something here

		/*
		 * collecting monitored events
		 * 
		 * any event in parent or child should be monitored
		 */
		HashSet<String> monitoredEvents = new HashSet<String>();
		for (PartialOrder po : partialOrders.getOrders()) {
			monitoredEvents.add(po.getEvent());
			monitoredEvents.addAll(po.getCondition().getAllNodes());
		}

		ArrayList<BlockCondition> blockConditions = new ArrayList<BlockCondition>();
		for (PartialOrder po : partialOrders.getOrders()) {
			Condition condition = po.getCondition();
			blockConditions.addAll(condition.getBlockConditions());
		}

		ArrayList<SimpleCondition> simpleConditions = new ArrayList<SimpleCondition>();
		for (PartialOrder po : partialOrders.getOrders()) {
			Condition condition = po.getCondition();
			simpleConditions.addAll(condition.getSimpleConditions());
		}

		/*
		 * Setting the schedule name
		 */

		if (partialOrders.getName() != null)
			result.put("constructor", "edu.illinois.imunit.asserts.ScheduleAssert.currentSchedule = \"" + partialOrders.getName() + "\";\n");

		/*
		 * declare all needed fields
		 */
		String fieldDecl = "";
		for (String event : monitoredEvents) {
			fieldDecl += "boolean $" + event + "_on$ = false;\n";
			for (BlockCondition bc : blockConditions) {
				if (bc.getBeforeEvent().equals(event)) {
					fieldDecl += "Thread $" + event + "_Thread$ = null;\n";
				}
			}
		}
		result.put("state declaration", fieldDecl);

		// no initialization code
		// result.put("initialization", "");

		/*
		 * reset code
		 */
		String reset = "";
		for (String event : monitoredEvents) {
			reset += "$" + event + "_on$ = false;\n";
			for (BlockCondition bc : blockConditions) {
				if (bc.getBeforeEvent().equals(event)) {
					reset += "$" + event + "_Thread$ = null;\n";
				}
			}
		}

		reset += "notifyAll();\n";
		result.put("reset", reset);

		/*
		 * monitor specific codes
		 */
		String monitoredEventsStr = "";
		for (String event : monitoredEvents) {
			monitoredEventsStr += event + ":{\n";

			monitoredEventsStr += "synchronized(this){\n";

			for (PartialOrder po : partialOrders.getOrders()) {
				if (po.getEvent().equals(event)) {
					if (po.getCheck()) { // checking mode code
						monitoredEventsStr += "if (!(" + ConditionToExp.convert(po.getCondition()) + ")) {\n";
						monitoredEventsStr += "System.err.println(\"SchUnit Schedule Check: Found a violation of the given schedule.\");\n";
						monitoredEventsStr += "}\n";
					} else {
						monitoredEventsStr += "while (!(" + ConditionToExp.convert(po.getCondition()) + ")) {\n";
						monitoredEventsStr += "try{\n";

						if (po.getCondition().getBlockConditions().size() != 0)
							monitoredEventsStr += "wait(50);\n";
						else
							monitoredEventsStr += "wait();\n";

						monitoredEventsStr += "} catch (InterruptedException $e$){\n";
						monitoredEventsStr += "System.err.println(\"Event " + event + "has been interrupted.\");\n";
						monitoredEventsStr += "}\n";
						monitoredEventsStr += "}\n";
					}
				}
			}

			for (BlockCondition bc : blockConditions) {
				if (bc.getBeforeEvent().equals(event)) {
					monitoredEventsStr += "$" + event + "_Thread$ = Thread.currentThread();\n";
				}
				if (bc.getBlockEvent() != null && bc.getBlockEvent().equals(event)) {
					monitoredEventsStr += "$" + bc.getBeforeEvent() + "_Thread$ = null;\n";
				}
			}

			monitoredEventsStr += "$" + event + "_on$ = true;\n";

			monitoredEventsStr += "notifyAll();\n";
			monitoredEventsStr += "}\n";

			monitoredEventsStr += "}\n\n";
		}
		result.put("monitored events", monitoredEventsStr);

		// no need
		// result.put("monitoring body", "");

		// It does not have any category. Skip this
		// result.put("" + " condition", "");

		return result;
	}

	public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws MOPException {
		if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0)
			throw new MOPException("Wrong type of monitor is given to PO Monitor.");
		allEvents = getEvents(events);

		LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
		logicShellResult.properties = getMonitorCode(logicOutputXML);

		return logicShellResult;
	}
}
