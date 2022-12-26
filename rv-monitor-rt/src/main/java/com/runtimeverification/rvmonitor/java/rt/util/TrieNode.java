package com.runtimeverification.rvmonitor.java.rt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TrieNode {

    private static String root = "#root#";

    private String currentEvent;

    private HashMap<String, TrieNode> children;

    private Set<String> monitors;

    private TrieNode rootNode;

    private int size;

    private long maxLength;

    private int minLength;

    int uniqueTraces;

    List<Integer> uniqueDepths;

    List<Integer> uniqueFrequencies;

    boolean uniqueComputed;

    StringBuilder builder;

    public int getSize() {
        return size;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public TrieNode(String event) {
        currentEvent = event;
        children = new HashMap<>();
        monitors = new HashSet<>();
        minLength = 1;
        uniqueDepths = new ArrayList<>();
        uniqueFrequencies = new ArrayList<>();
        builder = new StringBuilder();
    }

    public TrieNode() {
        this(root);
    }

    public void addChild(TrieNode child) {
        if (!hasChild(child.currentEvent)) {
            children.put(child.currentEvent, child);
        }
    }

    public boolean hasChild(String event) {
        return children.containsKey(event);
    }

    public TrieNode getChildNode(String event) {
        return children.get(event);
    }

    public void construct(Map<String, List<String>> traces) {
        if (rootNode == null) {
            rootNode = new TrieNode(root);
        }
        for (Map.Entry<String, List<String>> entry : traces.entrySet()) {
            updateLengths(entry.getValue());
            TrieNode currentNode =  rootNode;
            for (String event : entry.getValue()) {
                if (!currentNode.hasChild(event)) {
                    TrieNode newNode =  new TrieNode(event);
                    currentNode.addChild(newNode);
                }
                currentNode = currentNode.getChildNode(event);
            }
            currentNode.monitors.add(entry.getKey());
            size++;
        }
    }

    /**
     * Each trace is assumed to be monotonically increasing, so if the monitor is already in a trie node but we have not
     * reached the end of the trace we are adding, it means that we put a prefix of the current trace in the trie and
     * associated it with this monitor, so we remove the monitor name from the trie node as a way to update the trace
     * for that monitor.
     */
    public void put(String monitor, List<String> trace) {
        if (rootNode == null) {
            rootNode = new TrieNode(root);
        }
        updateLengths(trace);
        TrieNode currentNode = rootNode;
        for (String event : trace) {
            if (currentNode.monitors.remove(monitor)) {
                size--;
            }
            if (!currentNode.hasChild(event)) {
                TrieNode newNode = new TrieNode(event);
                currentNode.addChild(newNode);
            }
            currentNode = currentNode.getChildNode(event);
        }
        if (!currentNode.monitors.contains(monitor)) {
            currentNode.monitors.add(monitor);
            size++;
        }
    }

    private void updateLengths(List<String> trace) {
        if (trace.size() < minLength) {
            minLength = trace.size();
        } else if(trace.size() > maxLength) {
            maxLength =  trace.size();
        }
    }

    public String print() {
        StringBuilder b = new StringBuilder();
        print(rootNode, b, 1);
        return b.toString();
    }

    private void print(TrieNode root, StringBuilder builder, int offset) {
        builder.append(String.format("%" + offset + "s", " ")).append(root.currentEvent)
                .append(" ").append(root.monitors).append("\n");
        if (root.children.isEmpty()) {
            return;
        }
        Stack<TrieNode> stack = new Stack<>();
        stack.addAll(root.children.values());
        while (!stack.isEmpty()) {
            TrieNode node = stack.pop();
            print(node, builder, offset + 2);
        }
    }

    public int getUniqueTraceCount() {
        uniqueComputed = true;
        return computeUnique(rootNode, 0, builder, new ArrayList<>());
    }

    public double averageTraceLength() {
        if (!uniqueComputed) {
            computeUnique(rootNode, 0, builder, new ArrayList<>());
        }
        return average(uniqueDepths);
    }

    public double averageTraceFrequencies() {
        if (!uniqueComputed) {
            computeUnique(rootNode, 0, builder, new ArrayList<>());
        }
        return average(uniqueFrequencies);
    }

    public int maxTraceFrequency() {
        if (!uniqueComputed) {
            computeUnique(rootNode, 0, builder, new ArrayList<>());
        }
        Collections.sort(uniqueFrequencies);
        return uniqueFrequencies.get(uniqueFrequencies.size() - 1);
    }

    private double average(List<Integer> uniqueDepths) {
        double sum = 0.0;
        for (int depth : uniqueDepths) {
            sum += depth;
        }
        return sum / uniqueDepths.size();
    }

    private int computeUnique(TrieNode root, int depth, StringBuilder builder, ArrayList<String> trace) {
        if (!root.monitors.isEmpty()) {
            uniqueTraces++;
            uniqueDepths.add(depth);
            uniqueFrequencies.add(root.monitors.size());
            builder.append(root.monitors.size()).append(" ").append(trace).append("\n");
        }
        if (root.children.isEmpty()) {
            return uniqueTraces;
        }
        Stack<TrieNode> stack = new Stack<>();
        stack.addAll(root.children.values());
        while (!stack.isEmpty()) {
            TrieNode node = stack.pop();
            ArrayList<String> subtrace = new ArrayList<>(trace);
            subtrace.add(node.currentEvent);
            computeUnique(node, depth + 1, builder, subtrace);
        }
        return uniqueTraces;
    }

    public String printWords() {
        if (!uniqueComputed) {
            computeUnique(rootNode, 0, builder, new ArrayList<>());
        }
        return builder.toString();
    }
}
