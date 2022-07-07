package com.runtimeverification.rvmonitor.java.rt.observable;

import com.runtimeverification.rvmonitor.java.rt.util.TraceUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UniqueMonitorTraceCollector extends AllMonitorTraceCollector {

    public UniqueMonitorTraceCollector(PrintWriter writer, boolean doAnalysis, boolean writeLocationMap) {
        super(writer, doAnalysis, writeLocationMap);
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (isDoAnalysis()) {
            analyzeUniqueTraces();
        }
    }

    private void analyzeUniqueTraces() {
        try (PrintWriter uniqueWriter = new PrintWriter(TraceUtil.getAbsolutePath("unique-traces.txt"))) {
            uniqueWriter.println("=== UNIQUE TRACES ===");
            List<Map.Entry<List<String>, Integer>> freqList = new ArrayList<>(getFrequencies().entrySet());
            freqList.sort(Map.Entry.comparingByValue());
            List<Integer> sizes = new ArrayList<>();
            for (Map.Entry<List<String>, Integer> entry : freqList) {
                uniqueWriter.println(entry.getValue() + " " + entry.getKey());
                sizes.add(entry.getKey().size());
            }
            DecimalFormat format = new DecimalFormat("0.00");
            Collections.sort(sizes);
            uniqueWriter.println("=== END UNIQUE TRACES ===");
            uniqueWriter.println("Min Trace Frequency: " + freqList.get(0).getValue());
            uniqueWriter.println("Max Trace Frequency: " + freqList.get(freqList.size() - 1).getValue());
            uniqueWriter.println("Average Trace Frequency: " + format.format(getAverage(getFrequencies().values())));
            uniqueWriter.println("Min Trace Size: " + sizes.get(0));
            uniqueWriter.println("Max Trace Size: " + sizes.get(sizes.size() - 1));
            uniqueWriter.println("Average Trace Size: " + format.format(getAverage(sizes)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private double getAverage(Collection<Integer> values) {
        Double sum = 0.0;
        for (Integer value : values) {
            sum += value;
        }
        return sum / values.size();
    }
}
