package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvmonitor.java.rt.util.TraceUtil;

public class AllMonitorTraceCollector extends MonitorTraceCollector {

    private boolean doAnalysis;
    private boolean writeLocationMap;

    private PrintWriter locationMapWriter;

    public boolean isDoAnalysis() {
        return doAnalysis;
    }

    public AllMonitorTraceCollector(PrintWriter writer, boolean doAnalysis, boolean writeLocationMap,
                                    PrintWriter locationMapWriter, String dbPath) {
        super(writer, dbPath);
        this.doAnalysis = doAnalysis;
        this.writeLocationMap = writeLocationMap;
        this.locationMapWriter = locationMapWriter;
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
        locationMapWriter.println("=== LOCATION MAP ===");
        List<Map.Entry<String, Integer>> locations = new ArrayList<>(TraceUtil.getLocationMap().entrySet());
        locations.sort(Map.Entry.comparingByValue());
        for (Map.Entry<String, Integer> location : locations) {
            locationMapWriter.println(location.getValue() + " " + location.getKey());
        }
        locationMapWriter.close();
        locationMapWriter.flush();
    }

    private void processTracesWithoutAnalysis() {
        this.writer.println("=== END OF TRACE ===");
        this.writer.println("Total number of traces: " + traceDB.size());
    }

    private void processTracesWithAnalysis() {
        this.writer.println("=== END OF TRACE ===");
        this.writer.println("Total number of traces: " + traceDB.size());
        this.writer.println("Total number of unique traces: " + traceDB.uniqueTraces());
    }
}
