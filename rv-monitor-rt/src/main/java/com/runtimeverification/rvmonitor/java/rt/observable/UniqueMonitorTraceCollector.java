package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class UniqueMonitorTraceCollector extends AllMonitorTraceCollector {

    private PrintWriter uniqueWriter;

    public UniqueMonitorTraceCollector(PrintWriter writer, boolean doAnalysis, boolean writeLocationMap,
                                       PrintWriter locationWriter, PrintWriter uniqueWriter) {
        super(writer, doAnalysis, writeLocationMap, locationWriter);
        this.uniqueWriter = uniqueWriter;
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (isDoAnalysis()) {
            analyzeUniqueTraces();
        }
    }

    private void analyzeUniqueTraces() {
        uniqueWriter.println("=== UNIQUE TRACES ===");
        DecimalFormat format = new DecimalFormat("0.00");
        uniqueWriter.println(traceDB.printWords());
        uniqueWriter.println("=== END UNIQUE TRACES ===");
//        uniqueWriter.println("Min Trace Frequency: " + traceDB.averageTraceFrequencies());
        uniqueWriter.println("Max Trace Frequency: " + traceDB.maxTraceFrequency());
        uniqueWriter.println("Average Trace Frequency: " + format.format(traceDB.averageTraceFrequencies()));
        uniqueWriter.println("Min Trace Size: " + traceDB.getMinLength());
        uniqueWriter.println("Max Trace Size: " + traceDB.getMaxLength());
        uniqueWriter.println("Average Trace Size: " + format.format(traceDB.averageTraceLength()));
        uniqueWriter.flush();
        uniqueWriter.close();
    }
}
