package com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Level;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class holds helpers for reducing hard-coding.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeHelper {
    public static class VariableName {
        public static CodeVariable getWeakRef(CodeType type, RVMParameter param) {
            String name = "wr_" + param.getName();
            return new CodeVariable(type, name);
        }

        public static CodeMemberField getWeakRefInMonitor(RVMParameter param,
                CodeType weakreftype) {
            // The following convention had been hard-coded in the
            // monitor-generating class; so,
            // I followed it.
            String fieldname = "RVMRef_" + param.getName();
            return new CodeMemberField(fieldname, true, false, false,
                    weakreftype);
        }

        /**
         * This method creates a variable used to store all the intermediate
         * nodes while reaching the needed entry.
         *
         * @param type
         *            the type of the resulting variable
         * @param query
         *            all the candidate parameters used to name the variable
         * @param until
         *            specifies parameters that have been read (inclusive)
         * @return a variable based on the given type and parameter name
         */
        public static CodeVariable getInternalNode(CodeType type,
                RVMParameters query, int until) {
            StringBuilder s = new StringBuilder();
            s.append("node");
            for (int i = 0; i <= until; ++i) {
                RVMParameter prm = query.get(i);
                s.append('_');
                s.append(prm.getName());
            }
            return new CodeVariable(type, s.toString());
        }

        public static String getIndexingTreeCacheKeyName(String treename,
                RVMParameter param) {
            String name = treename;
            name += "_cachekey_";
            name += param.getName();
            return name;
        }

        public static String getIndexingTreeCacheValueName(String treename) {
            String name = treename;
            name += "_cachevalue";
            return name;
        }
    }

    public static class RuntimeType {
        public static CodeRVType.Tuple getIndexingTreeTuple(
                List<CodeRVType> fields) {
            if (fields.size() < 2 || fields.size() > 3)
                throw new NotImplementedException();

            String pkgname = "com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter";
            String clsname = "Tuple" + fields.size();
            CodeType type;
            {
                List<CodeType> generics = new ArrayList<CodeType>();
                for (CodeRVType field : fields)
                    generics.add(field);
                type = new CodeType(pkgname, clsname, generics);
            }
            return CodeRVType.forTuple(type, fields);
        }

        public static CodeRVType getIndexingTree(Level map, CodeType set,
                CodeType leaf, boolean hasGWRT) {
            boolean m = map != null;
            boolean s = set != null;
            boolean l = leaf != null;

            String clsname = "";

            if (hasGWRT)
                clsname += "BasicRef";

            clsname += "MapOf";
            if (m && s && l)
                clsname += "All";
            else if (m && s)
                clsname += "MapSet";
            else if (s && l)
                clsname += "SetMonitor";
            else if (m)
                clsname += "Map";
            else if (s)
                clsname += "Set";
            else if (l)
                clsname += "Monitor";
            else
                throw new IllegalArgumentException();

            List<CodeType> generics = new ArrayList<CodeType>(3);
            if (m)
                generics.add(map.getCodeType());
            if (s)
                generics.add(set);
            if (l)
                generics.add(leaf);

            String pkgname = "com.runtimeverification.rvmonitor.java.rt.table";
            CodeType treetype = new CodeType(pkgname, clsname, generics);
            return CodeRVType.forIndexingTree(treetype);
        }

        public static CodeType getAbstractIndexingTree() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rt.tablebase";
            String clsname = "AbstractIndexingTree";
            return new CodeType(pkgname, clsname, null, null);
        }

        public static CodeType getWeakReference() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rt.ref";
            String clsname = "CachedWeakReference";
            return new CodeType(pkgname, clsname);
        }

        public static CodeType getSetEventDelegator(CodeType monitor,
                CodeType pairmap) {
            // SetEventDelegator<TMonitor>
            String pkgname = "com.runtimeverification.rvmonitor.java.rt.tablebase";
            String clsname = "SetEventDelegator";
            return new CodeType(pkgname, clsname, monitor, pairmap);
        }

        public static CodeType getDisableHolderFromMonitor(CodeType monitor) {
            // *Monitor -> *DisableHolder
            String clsname = monitor.getClassName();
            if (!clsname.endsWith("Monitor"))
                throw new NotImplementedException();
            int i = clsname.lastIndexOf("Monitor");
            clsname = clsname.substring(0, i);
            clsname += "DisableHolder";
            return new CodeType(monitor.getPackageName(), clsname);
        }

        public static CodeType getMonitorInterfaceFromMonitor(CodeType monitor) {
            // *Monitor -> I*Monitor
            String clsname = monitor.getClassName();
            if (!clsname.endsWith("Monitor"))
                throw new NotImplementedException();
            clsname = "I" + clsname;
            return new CodeType(monitor.getPackageName(), clsname);
        }

        public static CodeType getInternalBehaviorMultiplexer() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.observable";
            String clsname = "InternalBehaviorMultiplexer";
            return new CodeType(pkgname, clsname);
        }

        public static CodeType getObserverable(CodeType observer) {
            String pkgname = "com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.observable";
            String clsname = "IObservable";
            return new CodeType(pkgname, clsname, observer);
        }

        public static CodeType getInternalBehaviorObserver() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.observable";
            String clsname = "IInternalBehaviorObserver";
            return new CodeType(pkgname, clsname);
        }

        public static CodeMemberField getSetWiseLockField(String name) {
            String pkgname = "java.util.concurrent.locks";
            String clsname = "ReentrantLock";
            CodeType type = new CodeType(pkgname, clsname);

            CodeExpr init = new CodeNewExpr(type);
            return new CodeMemberField(name, false, true, true, type, init);
        }

        public static CodeType getTerminatedMonitorCleaner() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rt.tablebase";
            String clsname = "TerminatedMonitorCleaner";
            return new CodeType(pkgname, clsname);
        }

        public static CodeType getRuntimeOption() {
            String pkgname = "com.runtimeverification.rvmonitor.java.rt";
            String clsname = "RuntimeOption";
            return new CodeType(pkgname, clsname);
        }
    }
}
