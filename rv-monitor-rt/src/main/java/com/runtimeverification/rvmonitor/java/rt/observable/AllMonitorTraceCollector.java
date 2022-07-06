package com.runtimeverification.rvmonitor.java.rt.observable;

import com.runtimeverification.rvmonitor.java.rt.util.TraceUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllMonitorTraceCollector extends MonitorTraceCollector {

    private boolean doAnalysis;
    private boolean writeLocationMap;

    private Map<List<String>, Integer> frequencies;

    public boolean isDoAnalysis() {
        return doAnalysis;
    }

    public Map<List<String>, Integer> getFrequencies() {
        return Collections.unmodifiableMap(frequencies);
    }

    public AllMonitorTraceCollector(PrintWriter writer, boolean doAnalysis, boolean writeLocationMap) {
        super(writer);
        this.doAnalysis = doAnalysis;
        this.writeLocationMap = writeLocationMap;
    }

    @Override
    public void onCompleted() {
        if (doAnalysis) {
            processTracesWithAnalysis();
        } else {
            processTracesWithoutAnalysis();
        }
        writer.flush();
        writer.close();
        if (writeLocationMap) {
            writeLocationMapToFile();
        }
    }

    private void writeLocationMapToFile() {
        try (PrintWriter locationWriter = new PrintWriter("/tmp/locations.txt")) {
            locationWriter.println("=== LOCATION MAP ===");
            List<Map.Entry<String, Integer>> locations = new ArrayList<>(TraceUtil.getLocationMap().entrySet());
            locations.sort(Map.Entry.comparingByValue());
            for (Map.Entry<String, Integer> location : locations) {
                locationWriter.println(location.getValue() + " " + location.getKey());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    private void processTracesWithoutAnalysis() {
        for(Map.Entry<String, List<String>> entry : traceDB.entrySet()) {
            this.writer.println(entry.getKey() + entry.getValue());
        }
        this.writer.println("=== END OF TRACE ===");
        this.writer.println("Total number of traces: " + traceDB.size());
    }

    private void processTracesWithAnalysis() {
        frequencies = new HashMap<>();
        for(Map.Entry<String, List<String>> entry : traceDB.entrySet()) {
            this.writer.println(entry.getKey() + entry.getValue());
            if (frequencies.get(entry.getValue()) == null) {
                frequencies.put(entry.getValue(), 1);
            } else {
                frequencies.put(entry.getValue(), frequencies.get(entry.getValue()) + 1);
            }
        }
        this.writer.println("=== END OF TRACE ===");
        this.writer.println("Total number of traces: " + traceDB.size());
        this.writer.println("Total number of unique traces: " + frequencies.keySet().size());
    }
}
