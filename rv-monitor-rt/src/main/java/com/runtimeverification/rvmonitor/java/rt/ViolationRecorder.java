package com.runtimeverification.rvmonitor.java.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class to record violations of properties and where they happen.
 * @author A. Cody Schuffelen
 */
public class ViolationRecorder {

    public final HashMap<String, HashMap<List<StackTraceElement>, Integer>> occurrences;

    /**
     * Private constructor to make it a singleton.
     */
    private ViolationRecorder() {
        occurrences = new HashMap<String, HashMap<List<StackTraceElement>, Integer>>();
    }

    private static final ViolationRecorder instance = new ViolationRecorder();

    /**
     * Retrieve the singleton instance.
     * @return The one ViolationRecorder instance.
     */
    public static final ViolationRecorder get() {
        return instance;
    }

    /**
     * Retrieve the current stack frames.
     * @return An array of stack frames.
     */
    public static StackTraceElement[] getStack() {
        return new Exception().getStackTrace();
    }

    /**
     * Retrieve the relevant parts of the current stack frames.
     * @return A list of stack frames.
     */
    public static List<StackTraceElement> getRelevantStack() {
        return makeRelevantList(getStack());
    }

    /**
     * Retrieve the current line of code in the monitored program.
     * @return A string of the current line of code.
     */
    public static String getLineOfCode() {
        final List<StackTraceElement> relevantStack = getRelevantStack();
        if(relevantStack.size() > 0) {
            return relevantStack.get(0).toString();
        } else {
            return "(Unknown)";
        }
    }

    /**
     * Record a violation.
     * @param name The name of the property that was violated.
     * @param stack The stack trace at time of violation.
     */
    public void record(String name) {
        final List<StackTraceElement> relevantList = getRelevantStack();
        if(!occurrences.containsKey(name)) {
            occurrences.put(name, new HashMap<List<StackTraceElement>, Integer>());
        }
        final HashMap<List<StackTraceElement>, Integer> violations = occurrences.get(name);
        if(!violations.containsKey(relevantList)) {
            violations.put(relevantList, new Integer(1));
        } else {
            violations.put(relevantList, new Integer(violations.get(relevantList).intValue()
                + 1));
        }
    }

    /**
     * Filter out the javamop and rv-monitor classes from the stack trace, as they are
     * not relevant to the property.
     * @param elements The stack trace at time of violation.
     * @return The relevant parts of the stack trace at time of violation.
     */
    private static List<StackTraceElement> makeRelevantList(StackTraceElement[] elements) {
        final ArrayList<StackTraceElement> relevantList = new ArrayList<StackTraceElement>();
        for(int i = 0; i < elements.length; i++) {
            final String fileName = elements[i].getFileName();
            final String className = elements[i].getClassName();
            // when file is generated at runtime, fileName is null
            // also check for nullity of className, just in case
            if((fileName != null && className != null)
                    && (className.startsWith("com.runtimeverification.rvmonitor.")
                    || className.startsWith("javamop.")
                    || fileName.contains(".aj")
                    || className.startsWith("mop.")
                    || className.startsWith("rvm."))) {
            } else {
                relevantList.add(elements[i]);
            }
        }
        return relevantList;
    }

    /**
     * Produce a minimal summary of the properties violated, and how often they were violated.
     * @return A shorter string with the violated properties and counts.
     */
    public String toStringMinimal() {
        final StringBuilder ret = new StringBuilder();
        for(Map.Entry<String, HashMap<List<StackTraceElement>, Integer>> type :
                occurrences.entrySet()) {
            int total = 0;
            for(Integer count : type.getValue().values()) {
                total += count.intValue();
            }
            ret.append(type.getKey()).append(": ").append(total).append("\n");
        }
        return ret.toString();
    }

    /**
     * Verbose output of violated properties. Has more detailed stack trace output on where
     * properties are violated.
     * @return A long string with violated properties and stack traces.
     */
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        for(Map.Entry<String, HashMap<List<StackTraceElement>, Integer>> type :
                occurrences.entrySet()) {
            for(Map.Entry<List<StackTraceElement>, Integer> traces : type.getValue().entrySet()) {
                ret.append(type.getKey()).append(": ").append(traces.getValue()).append("\n");
                ret.append(traces.getKey()).append("\n");
            }
        }
        return ret.toString();
    }
}
