package com.runtimeverification.rvmonitor.java.rt.observable;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTreeValue;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.util.TraceUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MonitorTraceCollector implements IInternalBehaviorObserver{

    private final PrintWriter writer;

    private final Map<String, List<String>> traceDB;

    public MonitorTraceCollector(PrintWriter writer) {
        this.writer = writer;
        this.traceDB = new HashMap<>();
    }

    @Override
    public void onMonitorTransitioned(AbstractMonitor monitor) {
        traceDB.put(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace);
    }

    @Override
    public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractMonitorSet<TMonitor> set) {
        for (int i = 0; i < set.getSize(); ++i) {
            // AbstractMonitor is the only parent of all monitor types and it implements IMonitor
            AbstractMonitor monitor = (AbstractMonitor) set.get(i);
            traceDB.put(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace);
        }
    }

    @Override
    public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractPartitionedMonitorSet<TMonitor> set) {
        for (AbstractPartitionedMonitorSet<TMonitor>.MonitorIterator i = set.monitorIterator(true); i.moveNext(); ) {
            // AbstractMonitor is the only parent of all monitor types and it implements IMonitor
            AbstractMonitor monitor = (AbstractMonitor) i.getMonitor();
            traceDB.put(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace);
        }
    }

    @Override
    public void onCompleted() {
        Map<List<String>, Integer> frequencies =  new HashMap<>();
        for(Entry<String, List<String>> entry : traceDB.entrySet()) {
            this.writer.println(entry.getKey() + entry.getValue());
            if (frequencies.get(entry.getValue()) == null) {
                frequencies.put(entry.getValue(), 1);
            } else {
                frequencies.put(entry.getValue(), frequencies.get(entry.getValue()) + 1);
            }
        }
        this.writer.println("=== END OF TRACE ===");
        this.writer.println("Total number of traces: " + traceDB.size());
//        Set<List<String>> unique = new HashSet<>(traceDB.values());
        this.writer.println("Total number of unique traces: " + frequencies.keySet().size());
        try (PrintWriter locationWriter = new PrintWriter("/tmp/locations.txt");
        PrintWriter uniqueWriter = new PrintWriter("/tmp/unique-traces.txt")) {
            locationWriter.println("=== LOCATION MAP ===");
            List<Entry<String, Integer>> locations = new ArrayList<>(TraceUtil.getLocationMap().entrySet());
            locations.sort(Entry.comparingByValue());
            for(Entry<String, Integer> location : locations) {
                locationWriter.println(location.getValue() + " " + location.getKey());
            }

            uniqueWriter.println("=== UNIQUE TRACES ===");
            List<Entry<List<String>, Integer>> freqList = new ArrayList<>(frequencies.entrySet());
            freqList.sort(Entry.comparingByValue());
            Integer maxSize = 0;
            Integer minSize = 1000;
            Integer maxFreq = 0;
            Integer minFreq = 1000;
            List<Integer> sizes = new ArrayList<>();
            for (Entry<List<String>, Integer> entry : freqList) {
                uniqueWriter.println(entry.getValue() + " " + entry.getKey());
                int size = entry.getKey().size();
                sizes.add(size);
                int freq = entry.getValue();
                maxSize = size > maxSize ? size : maxSize;
                minSize = size < minSize ? size : minSize;
                maxFreq = freq > maxFreq ? freq : maxFreq;
                minFreq = freq < minFreq ? freq : minFreq;
            }
            DecimalFormat format = new DecimalFormat("0.00");
            uniqueWriter.println("=== END UNIQUE TRACES ===");
            uniqueWriter.println("Min Trace Frequency: " + minFreq);
            uniqueWriter.println("Max Trace Frequency: " + maxFreq);
            uniqueWriter.println("Average Trace Frequency: " + format.format(getAverage(frequencies.values())));
            uniqueWriter.println("Min Trace Size: " + minSize);
            uniqueWriter.println("Max Trace Size: " + maxSize);
            uniqueWriter.println("Average Trace Size: " + format.format(getAverage(sizes)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.writer.flush();
        this.writer.close();
    }

    private double getAverage(Collection<Integer> values) {
        Double sum = 0.0;
        for (Integer value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    // TODO: We do not use any of the following methods; what's the runtime cost of keeping them?
    @Override
    public void onEventMethodEnter(String evtname, Object... args) {

    }

    @Override
    public void onIndexingTreeCacheHit(String cachename, Object cachevalue) {

    }

    @Override
    public void onIndexingTreeCacheMissed(String cachename) {

    }

    @Override
    public void onIndexingTreeCacheUpdated(String cachename, Object cachevalue) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeLookup(AbstractIndexingTree<TWeakRef, TValue> tree, LookupPurpose purpose, Object retrieved, Object... keys) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onTimeCheck(AbstractIndexingTree<TWeakRef, TValue> tree, IDisableHolder source, IDisableHolder candidate, boolean definable, Object... keys) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeNodeInserted(AbstractIndexingTree<TWeakRef, TValue> tree, Object inserted, Object... keys) {

    }

    @Override
    public void onNewMonitorCreated(AbstractMonitor created) {

    }

    @Override
    public void onMonitorCloned(AbstractMonitor existing, AbstractMonitor created) {

    }

    @Override
    public void onDisableFieldUpdated(IDisableHolder affected) {

    }

    @Override
    public void onEventMethodLeave() {

    }
}
