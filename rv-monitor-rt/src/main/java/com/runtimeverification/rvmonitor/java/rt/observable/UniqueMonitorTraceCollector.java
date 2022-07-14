package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;

public class UniqueMonitorTraceCollector extends AllMonitorTraceCollector {

    private PrintWriter uniqueWriter;

    List<Integer> frequencies = new ArrayList<>();
    List<Integer> lengths = new ArrayList<>();

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

    private String getFrequencyMap(PatriciaTrie<List<String>> trie) {
        StringBuilder builder = new StringBuilder();
        Collection<List<String>> values = trie.values();
        HashSet<List<String>> set = new HashSet<>(values);
        for (List<String> trace : set) {
            int frequency = Collections.frequency(values, trace);
            frequencies.add(frequency);
            lengths.add(trace.size());
            builder.append(frequency + " " + trace + "\n");
        }
        return builder.toString();
    }

    private void analyzeUniqueTraces() {
        uniqueWriter.println("=== UNIQUE TRACES ===");
        DecimalFormat format = new DecimalFormat("0.00");
        uniqueWriter.println(getFrequencyMap(traceDB));
        uniqueWriter.println("=== END UNIQUE TRACES ===");
        Collections.sort(frequencies);
        Collections.sort(lengths);
        uniqueWriter.println("Min Trace Frequency: " + frequencies.get(0));
        uniqueWriter.println("Max Trace Frequency: " + frequencies.get(frequencies.size() -1 ));
        uniqueWriter.println("Average Trace Frequency: " + format.format(average(frequencies)));
        uniqueWriter.println("Min Trace Size: " + lengths.get(0));
        uniqueWriter.println("Max Trace Size: " + lengths.get(lengths.size() -1 ));
        uniqueWriter.println("Average Trace Size: " + format.format(average(lengths)));
        uniqueWriter.flush();
        uniqueWriter.close();
    }

    private double average(List<Integer> uniqueDepths) {
        double sum = 0.0;
        for (int depth : uniqueDepths) {
            sum += depth;
        }
        return sum / uniqueDepths.size();
    }
}
