package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.EnableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.Advice;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class EventManager {

    public ArrayList<Advice> advices = new ArrayList<Advice>();
    public ArrayList<EndObject> endObjectEvents = new ArrayList<EndObject>();
    public ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
    public ArrayList<StartThread> startThreadEvents = new ArrayList<StartThread>();
    public EndProgram endProgramEvent = null;

    public TreeMap<RVMonitorSpec, MonitorSet> monitorSets;
    public TreeMap<RVMonitorSpec, SuffixMonitor> monitors;
    public TreeMap<RVMonitorSpec, EnableSet> enableSets;

    private boolean isCodeGenerated = false;

    public EventManager(String name, List<RVMonitorSpec> specs,
            CombinedOutput combinedOutput) throws RVMException {
        this.monitorSets = combinedOutput.monitorSets;
        this.monitors = combinedOutput.monitors;
        this.enableSets = combinedOutput.enableSets;

        this.endProgramEvent = new EndProgram(name);

        for (RVMonitorSpec spec : specs) {
            if (spec.isEnforce()) {
                endThreadEvents.add(new ThreadStatusMonitor(spec,
                        combinedOutput));
            }
            for (EventDefinition event : spec.getEvents()) {
                // normal event
                if (!event.isEndObject() && !event.isEndProgram()
                        && !event.isEndThread() && !event.isStartThread()) {
                    advices.add(new Advice(spec, event, combinedOutput));
                }

                // endObject
                if (event.isEndObject()) {
                    endObjectEvents.add(new EndObject(spec, event,
                            combinedOutput));
                }

                // endThread
                if (event.isEndThread()) {
                    endThreadEvents.add(new EndThread(spec, event,
                            combinedOutput));
                }

                // startThread
                if (event.isStartThread()) {
                    startThreadEvents.add(new StartThread(spec, event,
                            combinedOutput));
                }

                // endProgram
                if (event.isEndProgram()) {
                    endProgramEvent.addEndProgramEvent(spec, event,
                            combinedOutput);
                }

            } // end of for event

        } // end of for spec

        endProgramEvent.registerEndThreadEvents(endThreadEvents);

    }

    public String printConstructor() {
        String ret = "";

        if (endProgramEvent != null) {
            ret += endProgramEvent.printAddStatement();
        }

        return ret;
    }

    public String advices() {
        String ret = "";

        int numAdvice = 1;
        for (Advice advice : advices) {
            if (Main.empty_advicebody) {
                ret += "// " + numAdvice++ + "\n";
            }

            ret += advice;
            ret += "\n";
            if (advice.beCounted) {
                ret += "\n";
                ret += "// Declaration of the count variable for above pointcut\n";
                ret += "static int " + advice.getPointCutName() + "_count = 0;";
                ret += "\n\n\n";
            }
        }

        for (EndObject endObject : endObjectEvents) {
            ret += endObject.printDecl();
            ret += "\n";
        }

        for (EndThread endThread : endThreadEvents) {
            ret += endThread.printAdvices();
            ret += "\n";
        }

        for (StartThread startThread : startThreadEvents) {
            ret += startThread.printAdvices();
            ret += "\n";
        }

        ret += endProgramEvent.printHookThread();

        return ret;
    }

    public void generateCode() {
        if (!this.isCodeGenerated) {
            for (Advice advice : this.advices)
                advice.generateCode();
        }

        this.isCodeGenerated = true;
    }
}
