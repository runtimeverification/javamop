package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.LocalVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

public class IndexingCache {
    final RVMParameters param;
    final boolean perthread;

    public final boolean hasSet;
    public final boolean hasNode;
    final RVMVariable setType;
    final RVMVariable set;

    final RVMVariable nodeType;
    final RVMVariable node;

    final HashMap<String, RVMVariable> keys = new HashMap<String, RVMVariable>();
    final HashMap<String, RefTree> refTrees;

    public IndexingCache(RVMVariable name, RVMParameters param,
            RVMParameters fullParam, SuffixMonitor monitor,
            MonitorSet monitorSet, HashMap<String, RefTree> refTrees,
            boolean perthread, boolean isGeneral) {
        this.param = param;
        this.perthread = perthread;

        for (int i = 0; i < param.size(); i++) {
            this.keys.put(param.get(i).getName(), new RVMVariable(name
                    + "_cachekey_" + fullParam.getIdnum(param.get(i))));
        }

        this.hasSet = !fullParam.equals(param);
        this.hasNode = !hasSet || isGeneral;

        this.setType = monitorSet.getName();
        this.set = new RVMVariable(name + "_cacheset");
        this.nodeType = monitor.getOutermostName();
        this.node = new RVMVariable(name + "_cachenode");
        this.refTrees = refTrees;
    }

    public String getKeyType(RVMParameter p) {
        return refTrees.get(p.getType().toString()).getResultType();
    }

    public String getTreeType(RVMParameter p) {
        return refTrees.get(p.getType().toString()).getType();
    }

    public RVMVariable getKey(RVMParameter p) {
        return keys.get(p.getName());
    }

    public RVMVariable getKey(int i) {
        return keys.get(param.get(i).getName());
    }

    public String getKeyComparison() {
        String ret = "";

        for (int i = 0; i < param.size(); i++) {
            if (i > 0) {
                ret += " && ";
            }
            if (perthread) {
                ret += getKey(i) + ".get() != null && ";
                ret += param.get(i).getName() + " == " + getKey(i)
                        + ".get().get()";
            } else {
                if (Main.useFineGrainedLock) {
                    ret += getKey(i) + ".get() != null && ";
                    ret += param.get(i).getName() + " == " + getKey(i)
                            + ".get().get()";
                } else {
                    ret += getKey(i) + " != null && ";
                    ret += param.get(i).getName() + " == " + getKey(i)
                            + ".get()";
                }
            }
        }

        return ret;
    }

    public String getCacheKeys(LocalVariables localVars) {
        String ret = "";

        for (RVMParameter p : param) {
            RVMVariable tempRef = localVars.getTempRef(p);

            ret += tempRef + " = ";

            if (perthread) {
                ret += getKey(p) + ".get();\n";
            } else {
                if (Main.useFineGrainedLock)
                    ret += getKey(p) + ".get();\n";
                else
                    ret += getKey(p) + ";\n";
            }
        }

        return ret;
    }

    public String getCacheSet(RVMVariable obj) {
        String ret = "";

        if (!hasSet)
            return ret;

        if (perthread) {
            ret += obj + " = " + set + ".get();\n";
        } else {
            if (Main.useFineGrainedLock)
                ret += obj + " = " + set + ".get();\n";
            else
                ret += obj + " = " + set + ";\n";
        }

        return ret;
    }

    public String getCacheNode(RVMVariable obj) {
        String ret = "";

        if (perthread) {
            ret += obj + " = " + node + ".get();\n";
        } else {
            if (Main.useFineGrainedLock)
                ret += obj + " = " + node + ".get();\n";
            else
                ret += obj + " = " + node + ";\n";
        }

        return ret;
    }

    public String setCacheKeys(LocalVariables localVars) {
        String ret = "";

        for (RVMParameter p : param) {
            RVMVariable tempRef = localVars.getTempRef(p);

            if (perthread) {
                ret += getKey(p) + ".set(" + tempRef + ");\n";
            } else {
                if (Main.useFineGrainedLock)
                    ret += getKey(p) + ".set(" + tempRef + ");\n";
                else
                    ret += getKey(p) + " = " + tempRef + ";\n";
            }
        }

        return ret;
    }

    public String setCacheSet(RVMVariable obj) {
        String ret = "";

        if (!hasSet)
            return ret;

        if (perthread) {
            ret += set + ".set(" + obj + ");\n";
        } else {
            if (Main.useFineGrainedLock)
                ret += set + ".set(" + obj + ");\n";
            else
                ret += set + " = " + obj + ";\n";
        }

        return ret;
    }

    public String setCacheNode(RVMVariable obj) {
        String ret = "";

        if (!hasNode)
            return ret;

        if (perthread) {
            ret += node + ".set(" + obj + ");\n";
        } else {
            if (Main.useFineGrainedLock)
                ret += node + ".set(" + obj + ");\n";
            else
                ret += node + " = " + obj + ";\n";
        }

        return ret;
    }

    public String init() {
        return "";
    }

    @Override
    public String toString() {
        String ret = "";

        if (perthread) {
            for (RVMParameter p : param) {
                RVMVariable key = keys.get(p.getName());

                ret += "static final ThreadLocal<" + getKeyType(p) + "> " + key
                        + " = new ThreadLocal<" + getKeyType(p) + ">() {\n";
                ret += "protected " + getKeyType(p) + " initialValue(){\n";
                ret += "return null;\n";
                ret += "}\n";
                ret += "};\n";
            }

            if (hasSet) {
                ret += "static final ThreadLocal<" + setType + "> " + set
                        + " = new ThreadLocal<" + setType + ">() {\n";
                ret += "protected " + setType + " initialValue(){\n";
                ret += "return null;\n";
                ret += "}\n";
                ret += "};\n";
            }

            if (hasNode) {
                ret += "static final ThreadLocal<" + nodeType + "> " + node
                        + " = new ThreadLocal<" + nodeType + ">() {\n";
                ret += "protected " + nodeType + " initialValue(){\n";
                ret += "return null;\n";
                ret += "}\n";
                ret += "};\n";
            }
        } else {
            // ---
            if (Main.useFineGrainedLock) {
                for (RVMParameter p : param) {
                    String type = getKeyType(p);
                    RVMVariable name = keys.get(p.getName());
                    ret += this.createStaticThreadLocal(type, name, "null");
                }
                if (hasSet) {
                    ret += this.createStaticThreadLocal(setType.toString(),
                            set, "null");
                }
                if (hasNode) {
                    ret += this.createStaticThreadLocal(nodeType.toString(),
                            node, "null");
                }
            } else {
                for (RVMParameter p : param) {
                    RVMVariable key = keys.get(p.getName());
                    ret += "static " + getKeyType(p) + " " + key + " = null;\n";
                }
                if (hasSet) {
                    ret += "static " + setType + " " + set + " = null;\n";
                }
                if (hasNode) {
                    ret += "static " + nodeType + " " + node + " = null;\n";
                }
            }
        }

        return ret;
    }

    private String createStaticThreadLocal(String type, RVMVariable name,
            String initvalue) {
        String ret = "";
        ret += "static final ThreadLocal<" + type + "> " + name
                + " = new ThreadLocal<" + type + ">() {\n";
        ret += "@Override protected " + type + " initialValue() {\n";
        ret += "return " + initvalue + ";\n";
        ret += "}\n";
        ret += "};\n";
        return ret;
    }

    public String reset() {
        String ret = "";

        if (perthread) {
            for (RVMParameter p : param) {
                RVMVariable key = keys.get(p.getName());

                ret += key + " = new ThreadLocal() {\n";
                ret += "protected " + getKeyType(p) + " initialValue(){\n";
                ret += "return " + getTreeType(p) + ".NULRef;\n";
                ret += "}\n";
                ret += "};\n";
            }

            if (hasSet) {
                ret += set + " = new ThreadLocal() {\n";
                ret += "protected " + setType + " initialValue(){\n";
                ret += "return null;\n";
                ret += "}\n";
                ret += "};\n";
            }

            if (hasNode) {
                ret += node + " = new ThreadLocal() {\n";
                ret += "protected " + nodeType + " initialValue(){\n";
                ret += "return null;\n";
                ret += "}\n";
                ret += "};\n";
            }
        } else {
            for (RVMParameter p : param) {
                RVMVariable key = keys.get(p.getName());
                ret += key + " = " + getTreeType(p) + ".NULRef;\n";
            }
            if (hasSet) {
                ret += set + " = null;\n";
            }
            if (hasNode) {
                ret += node + " = null;\n";
            }
        }

        return ret;
    }

}
