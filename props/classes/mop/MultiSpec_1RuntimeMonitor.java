package mop;
import java.util.*;
import java.io.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.lang.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.lang.ref.*;
import com.runtimeverification.rvmonitor.java.rt.*;
import com.runtimeverification.rvmonitor.java.rt.ref.*;
import com.runtimeverification.rvmonitor.java.rt.table.*;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.SetEventDelegator;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple2;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple3;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.DisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TerminatedMonitorCleaner;
import com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver;
import com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver.LookupPurpose;
import com.runtimeverification.rvmonitor.java.rt.observable.IObservable;
import com.runtimeverification.rvmonitor.java.rt.observable.IObservableObject;
import com.runtimeverification.rvmonitor.java.rt.observable.InternalBehaviorMultiplexer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

final class Iterator_HasNextMonitor_Set extends com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet<Iterator_HasNextMonitor> {
        boolean violationIteratorHasNextMonitorProp1;

        Iterator_HasNextMonitor_Set(){
                this.size = 0;
                this.elements = new Iterator_HasNextMonitor[4];
        }
        final synchronized void event_hasnexttrue(Iterator i, boolean b) {
                this.violationIteratorHasNextMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Iterator_HasNextMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Iterator_HasNextMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_hasnexttrue(i, b);
                                violationIteratorHasNextMonitorProp1 |= monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                                if(monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                                        monitorfinalMonitor.Prop_1_handler_violation();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_hasnextfalse(Iterator i, boolean b) {
                this.violationIteratorHasNextMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Iterator_HasNextMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Iterator_HasNextMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_hasnextfalse(i, b);
                                violationIteratorHasNextMonitorProp1 |= monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                                if(monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                                        monitorfinalMonitor.Prop_1_handler_violation();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_next(Iterator i) {
                this.violationIteratorHasNextMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Iterator_HasNextMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Iterator_HasNextMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_next(i);
                                violationIteratorHasNextMonitorProp1 |= monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                                if(monitorfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                                        monitorfinalMonitor.Prop_1_handler_violation();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
}
final class Map_UnsafeIteratorMonitor_Set extends com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet<Map_UnsafeIteratorMonitor> {
        boolean matchMapUnsafeIteratorMonitorProp1;

        Map_UnsafeIteratorMonitor_Set(){
                this.size = 0;
                this.elements = new Map_UnsafeIteratorMonitor[4];
        }
        final synchronized void event_getset(Map m, Collection c) {
                this.matchMapUnsafeIteratorMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Map_UnsafeIteratorMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Map_UnsafeIteratorMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_getset(m, c);
                                matchMapUnsafeIteratorMonitorProp1 |= monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match;
                                if(monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                                        monitorfinalMonitor.Prop_1_handler_match();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_getiter(Collection c, Iterator i) {
                this.matchMapUnsafeIteratorMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Map_UnsafeIteratorMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Map_UnsafeIteratorMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_getiter(c, i);
                                matchMapUnsafeIteratorMonitorProp1 |= monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match;
                                if(monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                                        monitorfinalMonitor.Prop_1_handler_match();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_modifyMap(Map m) {
                this.matchMapUnsafeIteratorMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Map_UnsafeIteratorMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Map_UnsafeIteratorMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_modifyMap(m);
                                matchMapUnsafeIteratorMonitorProp1 |= monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match;
                                if(monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                                        monitorfinalMonitor.Prop_1_handler_match();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_modifyCol(Collection c) {
                this.matchMapUnsafeIteratorMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Map_UnsafeIteratorMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Map_UnsafeIteratorMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_modifyCol(c);
                                matchMapUnsafeIteratorMonitorProp1 |= monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match;
                                if(monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                                        monitorfinalMonitor.Prop_1_handler_match();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
        final synchronized void event_useiter(Iterator i) {
                this.matchMapUnsafeIteratorMonitorProp1 = false;
                int numAlive = 0 ;
                for(int i_1 = 0; i_1 < this.size; i_1++){
                        Map_UnsafeIteratorMonitor monitor = this.elements[i_1];
                        if(!monitor.isTerminated()){
                                elements[numAlive] = monitor;
                                numAlive++;

                                final Map_UnsafeIteratorMonitor monitorfinalMonitor = monitor;
                                monitor.Prop_1_event_useiter(i);
                                matchMapUnsafeIteratorMonitorProp1 |= monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match;
                                if(monitorfinalMonitor.Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                                        monitorfinalMonitor.Prop_1_handler_match();
                                }
                        }
                }
                for(int i_1 = numAlive; i_1 < this.size; i_1++){
                        this.elements[i_1] = null;
                }
                size = numAlive;
        }
}

class Iterator_HasNextMonitor extends com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractAtomicMonitor implements Cloneable, com.runtimeverification.rvmonitor.java.rt.RVMObject, IObservableObject {
        protected Object clone() {
                Iterator_HasNext_Monitor_num++;
                try {
                        Iterator_HasNextMonitor ret = (Iterator_HasNextMonitor) super.clone();
                        ret.monitorid = ++nextid;
                        ret.trace = new ArrayList<String>();
                        ret.trace.addAll(this.trace);
                        ret.pairValue = new AtomicInteger(pairValue.get());
                        return ret;
                }
                catch (CloneNotSupportedException e) {
                        throw new InternalError(e.toString());
                }
        }

        protected static long Iterator_HasNext_Monitor_num = 0;
        protected static long Iterator_HasNext_CollectedMonitor_num = 0;
        protected static long Iterator_HasNext_TerminatedMonitor_num = 0;
        protected static long Iterator_HasNext_next_num = 0;
        protected static long Iterator_HasNext_hasnexttrue_num = 0;
        protected static long Iterator_HasNext_hasnextfalse_num = 0;
        protected static long Iterator_HasNext_1_violation_num = 0;

        static final int Prop_1_transition_hasnexttrue[] = {2, 3, 2, 3};;
        static final int Prop_1_transition_hasnextfalse[] = {0, 3, 0, 3};;
        static final int Prop_1_transition_next[] = {1, 3, 0, 3};;

        volatile boolean Iterator_HasNextMonitor_Prop_1_Category_violation = false;

        private AtomicInteger pairValue;

        Iterator_HasNextMonitor() {
                this.pairValue = new AtomicInteger(this.calculatePairValue(-1, 0) ) ;

                Iterator_HasNext_Monitor_num++;
                this.trace = new ArrayList<String>();
                this.monitorid = ++nextid;
        }

        public static long getTotalMonitorCount() {
                return Iterator_HasNext_Monitor_num;
        }
        public static long getCollectedMonitorCount() {
                return Iterator_HasNext_CollectedMonitor_num;
        }
        public static long getTerminatedMonitorCount() {
                return Iterator_HasNext_TerminatedMonitor_num;
        }
        public static Map<String, Long> getEventCounters() {
                HashMap<String, Long> eventCounters = new HashMap<String, Long>();
                eventCounters.put("next", Iterator_HasNext_next_num);
                eventCounters.put("hasnexttrue", Iterator_HasNext_hasnexttrue_num);
                eventCounters.put("hasnextfalse", Iterator_HasNext_hasnextfalse_num);
                return eventCounters;
        }
        public static Map<String, Long> getCategoryCounters() {
                HashMap<String, Long> categoryCounters = new HashMap<String, Long>();
                categoryCounters.put("violation", Iterator_HasNext_1_violation_num);
                return categoryCounters;
        }

        @Override public final int getState() {
                return this.getState(this.pairValue.get() ) ;
        }
        @Override public final int getLastEvent() {
                return this.getLastEvent(this.pairValue.get() ) ;
        }
        private final int getState(int pairValue) {
                return (pairValue & 3) ;
        }
        private final int getLastEvent(int pairValue) {
                return (pairValue >> 2) ;
        }
        private final int calculatePairValue(int lastEvent, int state) {
                return (((lastEvent + 1) << 2) | state) ;
        }

        private final int handleEvent(int eventId, int[] table) {
                int nextstate;
                while (true) {
                        int oldpairvalue = this.pairValue.get() ;
                        int oldstate = this.getState(oldpairvalue) ;
                        nextstate = table [ oldstate ];
                        int nextpairvalue = this.calculatePairValue(eventId, nextstate) ;
                        if (this.pairValue.compareAndSet(oldpairvalue, nextpairvalue) ) {
                                break;
                        }
                }
                return nextstate;
        }

        final boolean Prop_1_event_hasnexttrue(Iterator i, boolean b) {
                {
                        if ( ! (b) ) {
                                return false;
                        }
                        {
                        }
                }
                this.trace.add("hasnexttrue:" + com.runtimeverification.rvmonitor.java.rt.ViolationRecorder.getLineOfCode());

                int nextstate = this.handleEvent(0, Prop_1_transition_hasnexttrue) ;
                this.Iterator_HasNextMonitor_Prop_1_Category_violation = nextstate == 1;

                return true;
        }

        final boolean Prop_1_event_hasnextfalse(Iterator i, boolean b) {
                {
                        if ( ! (!b) ) {
                                return false;
                        }
                        {
                        }
                }
                this.trace.add("hasnextfalse");

                int nextstate = this.handleEvent(1, Prop_1_transition_hasnextfalse) ;
                this.Iterator_HasNextMonitor_Prop_1_Category_violation = nextstate == 1;

                return true;
        }

        final boolean Prop_1_event_next(Iterator i) {
                {
                }
                this.trace.add("next");

                int nextstate = this.handleEvent(2, Prop_1_transition_next) ;
                this.Iterator_HasNextMonitor_Prop_1_Category_violation = nextstate == 1;

                return true;
        }

        final void Prop_1_handler_violation (){
                if(Iterator_HasNextMonitor_Prop_1_Category_violation) {
                        Iterator_HasNext_1_violation_num++;
                }
                {
                        RVMLogging.out.println(Level.WARNING, "Specification Iterator_HasNext has been violated on line " + com.runtimeverification.rvmonitor.java.rt.ViolationRecorder.getLineOfCode() + ". Documentation for this property can be found at http://runtimeverification.com/monitor/annotated-java/__properties/html/mop/Iterator_HasNext.html");
                        RVMLogging.out.println(Level.WARNING, "Iterator.hasNext() was not called before calling next().");
                }

        }

        final void reset() {
                this.pairValue.set(this.calculatePairValue(-1, 0) ) ;

                Iterator_HasNextMonitor_Prop_1_Category_violation = false;
        }

        // RVMRef_i was suppressed to reduce memory overhead

        //alive_parameters_0 = [Iterator i]
        boolean alive_parameters_0 = true;

        @Override
        protected final void terminateInternal(int idnum) {
                int lastEvent = this.getLastEvent();

                switch(idnum){
                        case 0:
                        alive_parameters_0 = false;
                        break;
                }
                switch(lastEvent) {
                        case -1:
                        return;
                        case 0:
                        //hasnexttrue
                        //alive_i
                        if(!(alive_parameters_0)){
                                RVM_terminated = true;
                                Iterator_HasNext_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 1:
                        //hasnextfalse
                        //alive_i
                        if(!(alive_parameters_0)){
                                RVM_terminated = true;
                                Iterator_HasNext_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 2:
                        //next
                        //alive_i
                        if(!(alive_parameters_0)){
                                RVM_terminated = true;
                                Iterator_HasNext_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                }
                return;
        }

        protected void finalize() throws Throwable {
                try {
                        Iterator_HasNext_CollectedMonitor_num++;
                } finally {
                        super.finalize();
                }
        }
        public static int getNumberOfEvents() {
                return 3;
        }

        public static int getNumberOfStates() {
                return 4;
        }

        private List<String> trace;
        private int monitorid;
        private static int nextid;

        @Override
        public final String getObservableObjectDescription() {
                StringBuilder s = new StringBuilder();
                s.append('#');
                s.append(this.monitorid);
                s.append('[');
                for (int i = 0; i < this.trace.size(); ++i) {
                        if (i > 0)
                        s.append(',');
                        s.append(this.trace.get(i));
                }
                s.append(']');
                return s.toString();
        }
}
interface IMap_UnsafeIteratorMonitor extends IMonitor, IDisableHolder {
}

class Map_UnsafeIteratorDisableHolder extends DisableHolder implements IMap_UnsafeIteratorMonitor, IObservableObject {
        Map_UnsafeIteratorDisableHolder(long tau) {
                super(tau);
                this.holderid = ++nextid;
        }

        @Override
        public final boolean isTerminated() {
                return false;
        }

        @Override
        public int getLastEvent() {
                return -1;
        }

        @Override
        public int getState() {
                return -1;
        }

        private int holderid;
        private static int nextid;

        @Override
        public final String getObservableObjectDescription() {
                StringBuilder s = new StringBuilder();
                s.append('#');
                s.append(this.holderid);
                s.append("{t:");
                s.append(this.getTau());
                s.append(",dis:");
                s.append(this.getDisable());
                s.append('}');
                return s.toString();
        }
}

class Map_UnsafeIteratorMonitor extends com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractSynchronizedMonitor implements Cloneable, com.runtimeverification.rvmonitor.java.rt.RVMObject, IMap_UnsafeIteratorMonitor, IObservableObject {
        protected Object clone() {
                Map_UnsafeIterator_Monitor_num++;
                try {
                        Map_UnsafeIteratorMonitor ret = (Map_UnsafeIteratorMonitor) super.clone();
                        ret.monitorid = ++nextid;
                        ret.trace = new ArrayList<String>();
                        ret.trace.addAll(this.trace);
                        return ret;
                }
                catch (CloneNotSupportedException e) {
                        throw new InternalError(e.toString());
                }
        }

        protected static long Map_UnsafeIterator_Monitor_num = 0;
        protected static long Map_UnsafeIterator_CollectedMonitor_num = 0;
        protected static long Map_UnsafeIterator_TerminatedMonitor_num = 0;
        protected static long Map_UnsafeIterator_getiter_num = 0;
        protected static long Map_UnsafeIterator_modifyCol_num = 0;
        protected static long Map_UnsafeIterator_getset_num = 0;
        protected static long Map_UnsafeIterator_modifyMap_num = 0;
        protected static long Map_UnsafeIterator_useiter_num = 0;
        protected static long Map_UnsafeIterator_1_match_num = 0;

        WeakReference Ref_c = null;
        WeakReference Ref_i = null;
        WeakReference Ref_m = null;
        int Prop_1_state;
        static final int Prop_1_transition_getset[] = {3, 5, 5, 5, 5, 5};;
        static final int Prop_1_transition_getiter[] = {5, 5, 5, 1, 5, 5};;
        static final int Prop_1_transition_modifyMap[] = {5, 4, 5, 3, 4, 5};;
        static final int Prop_1_transition_modifyCol[] = {5, 4, 5, 3, 4, 5};;
        static final int Prop_1_transition_useiter[] = {5, 1, 5, 5, 2, 5};;

        boolean Map_UnsafeIteratorMonitor_Prop_1_Category_match = false;

        Map_UnsafeIteratorMonitor(long tau, CachedWeakReference RVMRef_m) {
                this.tau = tau;
                Prop_1_state = 0;

                this.RVMRef_m = RVMRef_m;
                Map_UnsafeIterator_Monitor_num++;
                this.trace = new ArrayList<String>();
                this.monitorid = ++nextid;
        }

        public static long getTotalMonitorCount() {
                return Map_UnsafeIterator_Monitor_num;
        }
        public static long getCollectedMonitorCount() {
                return Map_UnsafeIterator_CollectedMonitor_num;
        }
        public static long getTerminatedMonitorCount() {
                return Map_UnsafeIterator_TerminatedMonitor_num;
        }
        public static Map<String, Long> getEventCounters() {
                HashMap<String, Long> eventCounters = new HashMap<String, Long>();
                eventCounters.put("getiter", Map_UnsafeIterator_getiter_num);
                eventCounters.put("modifyCol", Map_UnsafeIterator_modifyCol_num);
                eventCounters.put("getset", Map_UnsafeIterator_getset_num);
                eventCounters.put("modifyMap", Map_UnsafeIterator_modifyMap_num);
                eventCounters.put("useiter", Map_UnsafeIterator_useiter_num);
                return eventCounters;
        }
        public static Map<String, Long> getCategoryCounters() {
                HashMap<String, Long> categoryCounters = new HashMap<String, Long>();
                categoryCounters.put("match", Map_UnsafeIterator_1_match_num);
                return categoryCounters;
        }

        @Override
        public final int getState() {
                return Prop_1_state;
        }

        private final long tau;
        private long disable = -1;

        @Override
        public final long getTau() {
                return this.tau;
        }

        @Override
        public final long getDisable() {
                return this.disable;
        }

        @Override
        public final void setDisable(long value) {
                this.disable = value;
        }

        final synchronized boolean Prop_1_event_getset(Map m, Collection c) {
                Iterator i = null;
                if(Ref_i != null){
                        i = (Iterator)Ref_i.get();
                }
                {
                }
                this.trace.add("getset");
                if(Ref_c == null){
                        Ref_c = new WeakReference(c);
                }
                if(Ref_m == null){
                        Ref_m = new WeakReference(m);
                }
                RVM_lastevent = 0;

                Prop_1_state = Prop_1_transition_getset[Prop_1_state];
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = Prop_1_state == 2;
                return true;
        }

        final synchronized boolean Prop_1_event_getiter(Collection c, Iterator i) {
                Map m = null;
                if(Ref_m != null){
                        m = (Map)Ref_m.get();
                }
                {
                }
                this.trace.add("getiter");
                if(Ref_c == null){
                        Ref_c = new WeakReference(c);
                }
                if(Ref_i == null){
                        Ref_i = new WeakReference(i);
                }
                RVM_lastevent = 1;

                Prop_1_state = Prop_1_transition_getiter[Prop_1_state];
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = Prop_1_state == 2;
                return true;
        }

        final synchronized boolean Prop_1_event_modifyMap(Map m) {
                Collection c = null;
                if(Ref_c != null){
                        c = (Collection)Ref_c.get();
                }
                Iterator i = null;
                if(Ref_i != null){
                        i = (Iterator)Ref_i.get();
                }
                {
                }
                this.trace.add("modifyMap");
                if(Ref_m == null){
                        Ref_m = new WeakReference(m);
                }
                RVM_lastevent = 2;

                Prop_1_state = Prop_1_transition_modifyMap[Prop_1_state];
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = Prop_1_state == 2;
                return true;
        }

        final synchronized boolean Prop_1_event_modifyCol(Collection c) {
                Map m = null;
                if(Ref_m != null){
                        m = (Map)Ref_m.get();
                }
                Iterator i = null;
                if(Ref_i != null){
                        i = (Iterator)Ref_i.get();
                }
                {
                }
                this.trace.add("modifyCol");
                if(Ref_c == null){
                        Ref_c = new WeakReference(c);
                }
                RVM_lastevent = 3;

                Prop_1_state = Prop_1_transition_modifyCol[Prop_1_state];
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = Prop_1_state == 2;
                return true;
        }

        final synchronized boolean Prop_1_event_useiter(Iterator i) {
                Map m = null;
                if(Ref_m != null){
                        m = (Map)Ref_m.get();
                }
                Collection c = null;
                if(Ref_c != null){
                        c = (Collection)Ref_c.get();
                }
                {
                }
                this.trace.add("useiter");
                if(Ref_i == null){
                        Ref_i = new WeakReference(i);
                }
                RVM_lastevent = 4;

                Prop_1_state = Prop_1_transition_useiter[Prop_1_state];
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = Prop_1_state == 2;
                return true;
        }

        final void Prop_1_handler_match (){
                if(Map_UnsafeIteratorMonitor_Prop_1_Category_match) {
                        Map_UnsafeIterator_1_match_num++;
                }
                {
                        RVMLogging.out.println(Level.CRITICAL, "Specification Map_UnsafeIterator has been violated on line " + com.runtimeverification.rvmonitor.java.rt.ViolationRecorder.getLineOfCode() + ". Documentation for this property can be found at http://runtimeverification.com/monitor/annotated-java/__properties/html/mop/Map_UnsafeIterator.html");
                        RVMLogging.out.println(Level.CRITICAL, "The map was modified while an iteration over the set is in progress.");
                }

        }

        final synchronized void reset() {
                RVM_lastevent = -1;
                Prop_1_state = 0;
                Map_UnsafeIteratorMonitor_Prop_1_Category_match = false;
        }

        final CachedWeakReference RVMRef_m;
        // RVMRef_c was suppressed to reduce memory overhead
        // RVMRef_i was suppressed to reduce memory overhead

        //alive_parameters_0 = [Collection c, Iterator i]
        boolean alive_parameters_0 = true;
        //alive_parameters_1 = [Map m, Iterator i]
        boolean alive_parameters_1 = true;
        //alive_parameters_2 = [Iterator i]
        boolean alive_parameters_2 = true;

        @Override
        protected synchronized final void terminateInternal(int idnum) {
                switch(idnum){
                        case 0:
                        alive_parameters_1 = false;
                        break;
                        case 1:
                        alive_parameters_0 = false;
                        break;
                        case 2:
                        alive_parameters_0 = false;
                        alive_parameters_1 = false;
                        alive_parameters_2 = false;
                        break;
                }
                switch(RVM_lastevent) {
                        case -1:
                        return;
                        case 0:
                        //getset
                        //alive_c && alive_i
                        if(!(alive_parameters_0)){
                                RVM_terminated = true;
                                Map_UnsafeIterator_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 1:
                        //getiter
                        //alive_m && alive_i || alive_c && alive_i
                        if(!(alive_parameters_1 || alive_parameters_0)){
                                RVM_terminated = true;
                                Map_UnsafeIterator_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 2:
                        //modifyMap
                        //alive_i
                        if(!(alive_parameters_2)){
                                RVM_terminated = true;
                                Map_UnsafeIterator_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 3:
                        //modifyCol
                        //alive_i
                        if(!(alive_parameters_2)){
                                RVM_terminated = true;
                                Map_UnsafeIterator_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                        case 4:
                        //useiter
                        //alive_m && alive_i || alive_c && alive_i
                        if(!(alive_parameters_1 || alive_parameters_0)){
                                RVM_terminated = true;
                                Map_UnsafeIterator_TerminatedMonitor_num++;
                                return;
                        }
                        break;

                }
                return;
        }

        protected void finalize() throws Throwable {
                try {
                        Map_UnsafeIterator_CollectedMonitor_num++;
                } finally {
                        super.finalize();
                }
        }
        public static int getNumberOfEvents() {
                return 5;
        }

        public static int getNumberOfStates() {
                return 6;
        }

        private List<String> trace;
        private int monitorid;
        private static int nextid;

        @Override
        public final String getObservableObjectDescription() {
                StringBuilder s = new StringBuilder();
                s.append('#');
                s.append(this.monitorid);
                s.append("{t:");
                s.append(this.tau);
                s.append(",dis:");
                s.append(this.disable);
                s.append('}');
                s.append('[');
                for (int i = 0; i < this.trace.size(); ++i) {
                        if (i > 0)
                        s.append(',');
                        s.append(this.trace.get(i));
                }
                s.append(']');
                return s.toString();
        }
}

class MultiSpec_1_Statistics extends Thread implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
        static public long numTotalEvents = 0;
        static public long numTotalMonitors = 0;
        public void run() {
                System.err.println("# of total events: " + MultiSpec_1_Statistics.numTotalEvents);
                System.err.println("# of total monitors: " + MultiSpec_1_Statistics.numTotalMonitors);
        }
}
public final class MultiSpec_1RuntimeMonitor implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
        private static boolean violationIteratorHasNextMonitorProp1 = false;
        private static boolean matchMapUnsafeIteratorMonitorProp1 = false;
        private static com.runtimeverification.rvmonitor.java.rt.map.RVMMapManager MultiSpec_1MapManager;
        private static MultiSpec_1_Statistics MultiSpec_1_StatisticsInstance;
        static {
                MultiSpec_1MapManager = new com.runtimeverification.rvmonitor.java.rt.map.RVMMapManager();
                MultiSpec_1MapManager.start();
                MultiSpec_1_StatisticsInstance = new MultiSpec_1_Statistics();
                Runtime.getRuntime().addShutdownHook(MultiSpec_1_StatisticsInstance);
        }

        // Declarations for the Lock

        // Declarations for Timestamps
        private static final AtomicLong Map_UnsafeIterator_timestamp = new AtomicLong(1);

        private static final AtomicBoolean Iterator_HasNext_activated = new AtomicBoolean();
        private static final AtomicBoolean Map_UnsafeIterator_activated = new AtomicBoolean();

        // Declarations for Indexing Trees
        private static ThreadLocal<Object> Iterator_HasNext_i_Map_cachekey_i = new ThreadLocal<Object>() ;
        private static ThreadLocal<Iterator_HasNextMonitor> Iterator_HasNext_i_Map_cachevalue = new ThreadLocal<Iterator_HasNextMonitor>() ;
        private static final MapOfMonitor<Iterator_HasNextMonitor> Iterator_HasNext_i_Map = new MapOfMonitor<Iterator_HasNextMonitor>(0) ;

        private static ThreadLocal<Object> Map_UnsafeIterator_c_Map_cachekey_c = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>> Map_UnsafeIterator_c_Map_cachevalue = new ThreadLocal<Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_c_i_Map_cachekey_c = new ThreadLocal<Object>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_c_i_Map_cachekey_i = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>> Map_UnsafeIterator_c_i_Map_cachevalue = new ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_i_Map_cachekey_i = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>> Map_UnsafeIterator_i_Map_cachevalue = new ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_Map_cachekey_m = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>> Map_UnsafeIterator_m_Map_cachevalue = new ThreadLocal<Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_c_Map_cachekey_c = new ThreadLocal<Object>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_c_Map_cachekey_m = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>> Map_UnsafeIterator_m_c_Map_cachevalue = new ThreadLocal<Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_c_i_Map_cachekey_c = new ThreadLocal<Object>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_c_i_Map_cachekey_i = new ThreadLocal<Object>() ;
        private static ThreadLocal<Object> Map_UnsafeIterator_m_c_i_Map_cachekey_m = new ThreadLocal<Object>() ;
        private static ThreadLocal<IMap_UnsafeIteratorMonitor> Map_UnsafeIterator_m_c_i_Map_cachevalue = new ThreadLocal<IMap_UnsafeIteratorMonitor>() ;
        private static final MapOfAll<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> Map_UnsafeIterator_c_i_Map = new MapOfAll<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ;
        private static final MapOfAll<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> Map_UnsafeIterator_m_c_i_Map = new MapOfAll<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(0) ;
        private static final MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> Map_UnsafeIterator_i_Map = new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(2) ;
        private static ThreadLocal<Object> Map_UnsafeIterator_c__To__m_c_Map_cachekey_c = new ThreadLocal<Object>() ;
        private static ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>> Map_UnsafeIterator_c__To__m_c_Map_cachevalue = new ThreadLocal<Tuple2<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>>() ;
        private static final MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> Map_UnsafeIterator_c__To__m_c_Map = new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ;

        // Trees for References
        private static final com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap MultiSpec_1_Collection_RefMap = new com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap();
        private static final com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap MultiSpec_1_Iterator_RefMap = new com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap();
        private static final com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap MultiSpec_1_Map_RefMap = new com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap();

        static {
                Iterator_HasNext_i_Map.setObservableObjectDescription("<i>");
                Map_UnsafeIterator_c_i_Map.setObservableObjectDescription("<c,i>");
                Map_UnsafeIterator_m_c_i_Map.setObservableObjectDescription("<m,c,i>");
                Map_UnsafeIterator_i_Map.setObservableObjectDescription("<i>");
                Map_UnsafeIterator_c__To__m_c_Map.setObservableObjectDescription("<{c}:{m,c}>");
        }

        public static int cleanUp() {
                int collected = 0;
                // indexing trees
                collected += Iterator_HasNext_i_Map.cleanUpUnnecessaryMappings();
                collected += Map_UnsafeIterator_c_i_Map.cleanUpUnnecessaryMappings();
                collected += Map_UnsafeIterator_m_c_i_Map.cleanUpUnnecessaryMappings();
                collected += Map_UnsafeIterator_i_Map.cleanUpUnnecessaryMappings();
                collected += Map_UnsafeIterator_c__To__m_c_Map.cleanUpUnnecessaryMappings();
                // ref trees
                collected += MultiSpec_1_Collection_RefMap.cleanUpUnnecessaryMappings();
                collected += MultiSpec_1_Iterator_RefMap.cleanUpUnnecessaryMappings();
                collected += MultiSpec_1_Map_RefMap.cleanUpUnnecessaryMappings();
                return collected;
        }

        // Removing terminated monitors from partitioned sets
        static {
                TerminatedMonitorCleaner.start() ;
        }
        // Setting the behavior of the runtime library according to the compile-time option
        static {
                RuntimeOption.enableFineGrainedLock(true) ;
        }
        // Observing internal behaviors
        private static final InternalBehaviorMultiplexer observer = new InternalBehaviorMultiplexer() ;
        public static final IObservable<IInternalBehaviorObserver> getObservable() {
                return observer;
        }
        public static final void subscribe(IInternalBehaviorObserver o) {
                observer.subscribe(o) ;
        }
        public static final void unsubscribe(IInternalBehaviorObserver o) {
                observer.unsubscribe(o) ;
        }
        static {
            try {
                        PrintWriter w =  new PrintWriter(new File("/tmp/internal.txt"));
                        com.runtimeverification.rvmonitor.java.rt.observable.InternalBehaviorDumper dumper = new com.runtimeverification.rvmonitor.java.rt.observable.InternalBehaviorDumper(w);
                        subscribe(dumper);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        List<com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver> obs = observer.getObservers();
                        for (com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver o : obs)
                            o.onCompleted();
                    }
                });
        }

        public static final void Iterator_HasNext_hasnexttrueEvent(Iterator i, boolean b) {
                Iterator_HasNext_activated.set(true);
                observer.onEventMethodEnter("hasnext", i) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                Iterator_HasNextMonitor.Iterator_HasNext_hasnexttrue_num++;

                CachedWeakReference wr_i = null;
                MapOfMonitor<Iterator_HasNextMonitor> matchedLastMap = null;
                Iterator_HasNextMonitor matchedEntry = null;
                boolean cachehit = false;
                if ((i == Iterator_HasNext_i_Map_cachekey_i.get() ) ) {
                        matchedEntry = Iterator_HasNext_i_Map_cachevalue.get() ;
                        observer.onIndexingTreeCacheHit("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                        cachehit = true;
                }
                else {
                        observer.onIndexingTreeCacheMissed("Iterator_HasNext_i_Map_cachevalue") ;
                        wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        {
                                // FindOrCreateEntry
                                MapOfMonitor<Iterator_HasNextMonitor> itmdMap = Iterator_HasNext_i_Map;
                                matchedLastMap = itmdMap;
                                Iterator_HasNextMonitor node_i = Iterator_HasNext_i_Map.getNode(wr_i) ;
                                matchedEntry = node_i;
                        }
                        observer.onIndexingTreeLookup(Iterator_HasNext_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_i) ;
                }
                // D(X) main:1
                if ((matchedEntry == null) ) {
                        if ((wr_i == null) ) {
                                wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        }
                        // D(X) main:4
                        Iterator_HasNextMonitor created = new Iterator_HasNextMonitor() ;
                        observer.onNewMonitorCreated(created) ;
                        matchedEntry = created;
                        matchedLastMap.putNode(wr_i, created) ;
                }
                // D(X) main:8--9
                final Iterator_HasNextMonitor matchedEntryfinalMonitor = matchedEntry;
                matchedEntry.Prop_1_event_hasnexttrue(i, b);
                violationIteratorHasNextMonitorProp1 |= matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                if(matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                        matchedEntryfinalMonitor.Prop_1_handler_violation();
                }

                observer.onMonitorTransitioned(matchedEntry) ;
                if ((cachehit == false) ) {
                        Iterator_HasNext_i_Map_cachekey_i.set(i) ;
                        Iterator_HasNext_i_Map_cachevalue.set(matchedEntry) ;
                        observer.onIndexingTreeCacheUpdated("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                }

                observer.onEventMethodLeave() ;

        }

        public static final void Iterator_HasNext_hasnextfalseEvent(Iterator i, boolean b) {
                Iterator_HasNext_activated.set(true);
                observer.onEventMethodEnter("hasnextfalse", i) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                Iterator_HasNextMonitor.Iterator_HasNext_hasnextfalse_num++;

                CachedWeakReference wr_i = null;
                MapOfMonitor<Iterator_HasNextMonitor> matchedLastMap = null;
                Iterator_HasNextMonitor matchedEntry = null;
                boolean cachehit = false;
                if ((i == Iterator_HasNext_i_Map_cachekey_i.get() ) ) {
                        matchedEntry = Iterator_HasNext_i_Map_cachevalue.get() ;
                        observer.onIndexingTreeCacheHit("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                        cachehit = true;
                }
                else {
                        observer.onIndexingTreeCacheMissed("Iterator_HasNext_i_Map_cachevalue") ;
                        wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        {
                                // FindOrCreateEntry
                                MapOfMonitor<Iterator_HasNextMonitor> itmdMap = Iterator_HasNext_i_Map;
                                matchedLastMap = itmdMap;
                                Iterator_HasNextMonitor node_i = Iterator_HasNext_i_Map.getNode(wr_i) ;
                                matchedEntry = node_i;
                        }
                        observer.onIndexingTreeLookup(Iterator_HasNext_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_i) ;
                }
                // D(X) main:1
                if ((matchedEntry == null) ) {
                        if ((wr_i == null) ) {
                                wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        }
                        // D(X) main:4
                        Iterator_HasNextMonitor created = new Iterator_HasNextMonitor() ;
                        observer.onNewMonitorCreated(created) ;
                        matchedEntry = created;
                        matchedLastMap.putNode(wr_i, created) ;
                }
                // D(X) main:8--9
                final Iterator_HasNextMonitor matchedEntryfinalMonitor = matchedEntry;
                matchedEntry.Prop_1_event_hasnextfalse(i, b);
                violationIteratorHasNextMonitorProp1 |= matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                if(matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                        matchedEntryfinalMonitor.Prop_1_handler_violation();
                }

                observer.onMonitorTransitioned(matchedEntry) ;
                if ((cachehit == false) ) {
                        Iterator_HasNext_i_Map_cachekey_i.set(i) ;
                        Iterator_HasNext_i_Map_cachevalue.set(matchedEntry) ;
                        observer.onIndexingTreeCacheUpdated("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                }

                observer.onEventMethodLeave() ;

        }

        public static final void Iterator_HasNext_nextEvent(Iterator i) {
                Iterator_HasNext_activated.set(true);
                observer.onEventMethodEnter("next", i) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                Iterator_HasNextMonitor.Iterator_HasNext_next_num++;

                CachedWeakReference wr_i = null;
                MapOfMonitor<Iterator_HasNextMonitor> matchedLastMap = null;
                Iterator_HasNextMonitor matchedEntry = null;
                boolean cachehit = false;
                if ((i == Iterator_HasNext_i_Map_cachekey_i.get() ) ) {
                        matchedEntry = Iterator_HasNext_i_Map_cachevalue.get() ;
                        observer.onIndexingTreeCacheHit("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                        cachehit = true;
                }
                else {
                        observer.onIndexingTreeCacheMissed("Iterator_HasNext_i_Map_cachevalue") ;
                        wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        {
                                // FindOrCreateEntry
                                MapOfMonitor<Iterator_HasNextMonitor> itmdMap = Iterator_HasNext_i_Map;
                                matchedLastMap = itmdMap;
                                Iterator_HasNextMonitor node_i = Iterator_HasNext_i_Map.getNode(wr_i) ;
                                matchedEntry = node_i;
                        }
                        observer.onIndexingTreeLookup(Iterator_HasNext_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_i) ;
                }
                // D(X) main:1
                if ((matchedEntry == null) ) {
                        if ((wr_i == null) ) {
                                wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                        }
                        // D(X) main:4
                        Iterator_HasNextMonitor created = new Iterator_HasNextMonitor() ;
                        observer.onNewMonitorCreated(created) ;
                        matchedEntry = created;
                        matchedLastMap.putNode(wr_i, created) ;
                }
                // D(X) main:8--9
                final Iterator_HasNextMonitor matchedEntryfinalMonitor = matchedEntry;
                matchedEntry.Prop_1_event_next(i);
                violationIteratorHasNextMonitorProp1 |= matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation;
                if(matchedEntryfinalMonitor.Iterator_HasNextMonitor_Prop_1_Category_violation) {
                        matchedEntryfinalMonitor.Prop_1_handler_violation();
                }

                observer.onMonitorTransitioned(matchedEntry) ;
                if ((cachehit == false) ) {
                        Iterator_HasNext_i_Map_cachekey_i.set(i) ;
                        Iterator_HasNext_i_Map_cachevalue.set(matchedEntry) ;
                        observer.onIndexingTreeCacheUpdated("Iterator_HasNext_i_Map_cachevalue", matchedEntry) ;
                }

                observer.onEventMethodLeave() ;

        }

        public static final void Map_UnsafeIterator_getsetEvent(Map m, Collection c) {
                Map_UnsafeIterator_activated.set(true);
                observer.onEventMethodEnter("getset", m, c) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                Map_UnsafeIteratorMonitor.Map_UnsafeIterator_getset_num++;

                CachedWeakReference wr_c = null;
                CachedWeakReference wr_m = null;
                Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> matchedEntry = null;
                boolean cachehit = false;
                if (((c == Map_UnsafeIterator_m_c_Map_cachekey_c.get() ) && (m == Map_UnsafeIterator_m_c_Map_cachekey_m.get() ) ) ) {
                        matchedEntry = Map_UnsafeIterator_m_c_Map_cachevalue.get() ;
                        observer.onIndexingTreeCacheHit("Map_UnsafeIterator_m_c_Map_cachevalue", matchedEntry) ;
                        cachehit = true;
                }
                else {
                        observer.onIndexingTreeCacheMissed("Map_UnsafeIterator_m_c_Map_cachevalue") ;
                        wr_m = MultiSpec_1_Map_RefMap.findOrCreateWeakRef(m);
                        wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                        {
                                // FindOrCreateEntry
                                Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                if ((node_m == null) ) {
                                        node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                        Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                        node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                        node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                }
                                Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_m_c = node_m.getValue1() .getNode(wr_c) ;
                                if ((node_m_c == null) ) {
                                        node_m_c = new Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>() ;
                                        node_m.getValue1() .putNode(wr_c, node_m_c) ;
                                        node_m_c.setValue1(new MapOfMonitor<IMap_UnsafeIteratorMonitor>(2) ) ;
                                        node_m_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                }
                                matchedEntry = node_m_c;
                        }
                        observer.onIndexingTreeLookup(Map_UnsafeIterator_m_c_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_m, wr_c) ;
                }
                // D(X) main:1
                Map_UnsafeIteratorMonitor matchedLeaf = matchedEntry.getValue3() ;
                if ((matchedLeaf == null) ) {
                        if ((wr_m == null) ) {
                                wr_m = MultiSpec_1_Map_RefMap.findOrCreateWeakRef(m);
                        }
                        if ((wr_c == null) ) {
                                wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                        }
                        if ((matchedLeaf == null) ) {
                                // D(X) main:4
                                Map_UnsafeIteratorMonitor created = new Map_UnsafeIteratorMonitor(Map_UnsafeIterator_timestamp.getAndIncrement() , wr_m) ;
                                observer.onNewMonitorCreated(created) ;
                                matchedEntry.setValue3(created) ;
                                Map_UnsafeIteratorMonitor_Set enclosingSet = matchedEntry.getValue2() ;
                                enclosingSet.add(created) ;
                                // D(X) defineNew:5 for <c>
                                {
                                        // InsertMonitor
                                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                        if ((node_c == null) ) {
                                                node_c = new Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_c_i_Map.putNode(wr_c, node_c) ;
                                                node_c.setValue1(new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ) ;
                                                node_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        Map_UnsafeIteratorMonitor_Set targetSet = node_c.getValue2() ;
                                        targetSet.add(created) ;
                                }
                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_c_i_Map, created, wr_c) ;
                                // D(X) defineNew:5 for <m>
                                {
                                        // InsertMonitor
                                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                        if ((node_m == null) ) {
                                                node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                                node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                                node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        Map_UnsafeIteratorMonitor_Set targetSet = node_m.getValue2() ;
                                        targetSet.add(created) ;
                                }
                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_m_c_i_Map, created, wr_m) ;
                                // D(X) defineNew:5 for <c-m, c>
                                {
                                        // InsertMonitor
                                        Tuple2<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c__To__m_c_Map.getNode(wr_c) ;
                                        if ((node_c == null) ) {
                                                node_c = new Tuple2<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_c__To__m_c_Map.putNode(wr_c, node_c) ;
                                                node_c.setValue1(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        Map_UnsafeIteratorMonitor_Set targetSet = node_c.getValue1() ;
                                        targetSet.add(created) ;
                                }
                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_c__To__m_c_Map, created, wr_c) ;
                        }
                        // D(X) main:6
                        Map_UnsafeIteratorMonitor disableUpdatedLeaf = matchedEntry.getValue3() ;
                        disableUpdatedLeaf.setDisable(Map_UnsafeIterator_timestamp.getAndIncrement() ) ;
                        observer.onDisableFieldUpdated(disableUpdatedLeaf) ;
                }
                // D(X) main:8--9
                Map_UnsafeIteratorMonitor_Set stateTransitionedSet = matchedEntry.getValue2() ;
                stateTransitionedSet.event_getset(m, c);
                matchMapUnsafeIteratorMonitorProp1 = stateTransitionedSet.matchMapUnsafeIteratorMonitorProp1;

                observer.onMonitorTransitioned(stateTransitionedSet) ;
                if ((cachehit == false) ) {
                        Map_UnsafeIterator_m_c_Map_cachekey_c.set(c) ;
                        Map_UnsafeIterator_m_c_Map_cachekey_m.set(m) ;
                        Map_UnsafeIterator_m_c_Map_cachevalue.set(matchedEntry) ;
                        observer.onIndexingTreeCacheUpdated("Map_UnsafeIterator_m_c_Map_cachevalue", matchedEntry) ;
                }

                observer.onEventMethodLeave() ;

        }

        public static final void Map_UnsafeIterator_getiterEvent(Collection c, Iterator i) {
                observer.onEventMethodEnter("getiter", c, i) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                if (Map_UnsafeIterator_activated.get()) {
                        Map_UnsafeIteratorMonitor.Map_UnsafeIterator_getiter_num++;

                        CachedWeakReference wr_c = null;
                        CachedWeakReference wr_i = null;
                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> matchedEntry = null;
                        boolean cachehit = false;
                        if (((c == Map_UnsafeIterator_c_i_Map_cachekey_c.get() ) && (i == Map_UnsafeIterator_c_i_Map_cachekey_i.get() ) ) ) {
                                matchedEntry = Map_UnsafeIterator_c_i_Map_cachevalue.get() ;
                                observer.onIndexingTreeCacheHit("Map_UnsafeIterator_c_i_Map_cachevalue", matchedEntry) ;
                                cachehit = true;
                        }
                        else {
                                observer.onIndexingTreeCacheMissed("Map_UnsafeIterator_c_i_Map_cachevalue") ;
                                wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                                wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                                {
                                        // FindOrCreateEntry
                                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                        if ((node_c == null) ) {
                                                node_c = new Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_c_i_Map.putNode(wr_c, node_c) ;
                                                node_c.setValue1(new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ) ;
                                                node_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c_i = node_c.getValue1() .getNode(wr_i) ;
                                        if ((node_c_i == null) ) {
                                                node_c_i = new Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                node_c.getValue1() .putNode(wr_i, node_c_i) ;
                                                node_c_i.setValue1(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        matchedEntry = node_c_i;
                                }
                                observer.onIndexingTreeLookup(Map_UnsafeIterator_c_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_c, wr_i) ;
                        }
                        // D(X) main:1
                        IMap_UnsafeIteratorMonitor matchedLeaf = matchedEntry.getValue2() ;
                        if ((matchedLeaf == null) ) {
                                if ((wr_c == null) ) {
                                        wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                                }
                                if ((wr_i == null) ) {
                                        wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                                }
                                {
                                        // D(X) createNewMonitorStates:4 when Dom(theta'') = <c>
                                        Map_UnsafeIteratorMonitor_Set sourceSet = null;
                                        {
                                                // FindCode
                                                Tuple2<Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c__To__m_c_Map.getNode(wr_c) ;
                                                if ((node_c != null) ) {
                                                        Map_UnsafeIteratorMonitor_Set itmdSet = node_c.getValue1() ;
                                                        sourceSet = itmdSet;
                                                }
                                        }
                                        observer.onIndexingTreeLookup(Map_UnsafeIterator_c__To__m_c_Map, LookupPurpose.ClonedMonitor, sourceSet, wr_c) ;
                                        if ((sourceSet != null) ) {
                                                int numalive = 0;
                                                int setlen = sourceSet.getSize() ;
                                                for (int ielem = 0; (ielem < setlen) ;++ielem) {
                                                        Map_UnsafeIteratorMonitor sourceMonitor = sourceSet.get(ielem) ;
                                                        if ((!sourceMonitor.isTerminated() && (sourceMonitor.RVMRef_m.get() != null) ) ) {
                                                                sourceSet.set(numalive++, sourceMonitor) ;
                                                                CachedWeakReference wr_m = sourceMonitor.RVMRef_m;
                                                                MapOfMonitor<IMap_UnsafeIteratorMonitor> destLastMap = null;
                                                                IMap_UnsafeIteratorMonitor destLeaf = null;
                                                                {
                                                                        // FindOrCreate
                                                                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                                                        if ((node_m == null) ) {
                                                                                node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                                                                node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                                                                node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                        }
                                                                        Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_m_c = node_m.getValue1() .getNode(wr_c) ;
                                                                        if ((node_m_c == null) ) {
                                                                                node_m_c = new Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>() ;
                                                                                node_m.getValue1() .putNode(wr_c, node_m_c) ;
                                                                                node_m_c.setValue1(new MapOfMonitor<IMap_UnsafeIteratorMonitor>(2) ) ;
                                                                                node_m_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                        }
                                                                        MapOfMonitor<IMap_UnsafeIteratorMonitor> itmdMap = node_m_c.getValue1() ;
                                                                        destLastMap = itmdMap;
                                                                        IMap_UnsafeIteratorMonitor node_m_c_i = node_m_c.getValue1() .getNode(wr_i) ;
                                                                        destLeaf = node_m_c_i;
                                                                }
                                                                observer.onIndexingTreeLookup(Map_UnsafeIterator_m_c_i_Map, LookupPurpose.CombinedMonitor, destLeaf, wr_m, wr_c, wr_i) ;
                                                                if (((destLeaf == null) || destLeaf instanceof Map_UnsafeIteratorDisableHolder) ) {
                                                                        boolean definable = true;
                                                                        // D(X) defineTo:1--5 for <c, i>
                                                                        if (definable) {
                                                                                // FindCode
                                                                                Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                                                                if ((node_c != null) ) {
                                                                                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c_i = node_c.getValue1() .getNode(wr_i) ;
                                                                                        if ((node_c_i != null) ) {
                                                                                                IMap_UnsafeIteratorMonitor itmdLeaf = node_c_i.getValue2() ;
                                                                                                if ((itmdLeaf != null) ) {
                                                                                                        if (((itmdLeaf.getDisable() > sourceMonitor.getTau() ) || ((itmdLeaf.getTau() > 0) && (itmdLeaf.getTau() < sourceMonitor.getTau() ) ) ) ) {
                                                                                                                definable = false;
                                                                                                        }
                                                                                                        observer.onTimeCheck(Map_UnsafeIterator_c_i_Map, sourceMonitor, itmdLeaf, definable, wr_c, wr_i) ;
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }
                                                                        // D(X) defineTo:1--5 for <i>
                                                                        if (definable) {
                                                                                // FindCode
                                                                                Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_i = Map_UnsafeIterator_i_Map.getNode(wr_i) ;
                                                                                if ((node_i != null) ) {
                                                                                        IMap_UnsafeIteratorMonitor itmdLeaf = node_i.getValue2() ;
                                                                                        if ((itmdLeaf != null) ) {
                                                                                                if (((itmdLeaf.getDisable() > sourceMonitor.getTau() ) || ((itmdLeaf.getTau() > 0) && (itmdLeaf.getTau() < sourceMonitor.getTau() ) ) ) ) {
                                                                                                        definable = false;
                                                                                                }
                                                                                                observer.onTimeCheck(Map_UnsafeIterator_i_Map, sourceMonitor, itmdLeaf, definable, wr_i) ;
                                                                                        }
                                                                                }
                                                                        }
                                                                        // D(X) defineTo:1--5 for <m, c, i>
                                                                        if (definable) {
                                                                                // FindCode
                                                                                Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                                                                if ((node_m != null) ) {
                                                                                        Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_m_c = node_m.getValue1() .getNode(wr_c) ;
                                                                                        if ((node_m_c != null) ) {
                                                                                                IMap_UnsafeIteratorMonitor node_m_c_i = node_m_c.getValue1() .getNode(wr_i) ;
                                                                                                if ((node_m_c_i != null) ) {
                                                                                                        if (((node_m_c_i.getDisable() > sourceMonitor.getTau() ) || ((node_m_c_i.getTau() > 0) && (node_m_c_i.getTau() < sourceMonitor.getTau() ) ) ) ) {
                                                                                                                definable = false;
                                                                                                        }
                                                                                                        observer.onTimeCheck(Map_UnsafeIterator_m_c_i_Map, sourceMonitor, node_m_c_i, definable, wr_m, wr_c, wr_i) ;
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }
                                                                        if (definable) {
                                                                                // D(X) defineTo:6
                                                                                Map_UnsafeIteratorMonitor created = (Map_UnsafeIteratorMonitor)sourceMonitor.clone() ;
                                                                                observer.onMonitorCloned(sourceMonitor, created) ;
                                                                                destLastMap.putNode(wr_i, created) ;
                                                                                // D(X) defineTo:7 for <c>
                                                                                {
                                                                                        // InsertMonitor
                                                                                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                                                                        if ((node_c == null) ) {
                                                                                                node_c = new Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                Map_UnsafeIterator_c_i_Map.putNode(wr_c, node_c) ;
                                                                                                node_c.setValue1(new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ) ;
                                                                                                node_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Map_UnsafeIteratorMonitor_Set targetSet = node_c.getValue2() ;
                                                                                        targetSet.add(created) ;
                                                                                }
                                                                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_c_i_Map, created, wr_c) ;
                                                                                // D(X) defineTo:7 for <c, i>
                                                                                {
                                                                                        // InsertMonitor
                                                                                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                                                                        if ((node_c == null) ) {
                                                                                                node_c = new Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                Map_UnsafeIterator_c_i_Map.putNode(wr_c, node_c) ;
                                                                                                node_c.setValue1(new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ) ;
                                                                                                node_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c_i = node_c.getValue1() .getNode(wr_i) ;
                                                                                        if ((node_c_i == null) ) {
                                                                                                node_c_i = new Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                node_c.getValue1() .putNode(wr_i, node_c_i) ;
                                                                                                node_c_i.setValue1(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Map_UnsafeIteratorMonitor_Set targetSet = node_c_i.getValue1() ;
                                                                                        targetSet.add(created) ;
                                                                                }
                                                                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_c_i_Map, created, wr_c, wr_i) ;
                                                                                // D(X) defineTo:7 for <i>
                                                                                {
                                                                                        // InsertMonitor
                                                                                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_i = Map_UnsafeIterator_i_Map.getNode(wr_i) ;
                                                                                        if ((node_i == null) ) {
                                                                                                node_i = new Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                Map_UnsafeIterator_i_Map.putNode(wr_i, node_i) ;
                                                                                                node_i.setValue1(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Map_UnsafeIteratorMonitor_Set targetSet = node_i.getValue1() ;
                                                                                        targetSet.add(created) ;
                                                                                }
                                                                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_i_Map, created, wr_i) ;
                                                                                // D(X) defineTo:7 for <m>
                                                                                {
                                                                                        // InsertMonitor
                                                                                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                                                                        if ((node_m == null) ) {
                                                                                                node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                                                                                node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                                                                                node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Map_UnsafeIteratorMonitor_Set targetSet = node_m.getValue2() ;
                                                                                        targetSet.add(created) ;
                                                                                }
                                                                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_m_c_i_Map, created, wr_m) ;
                                                                                // D(X) defineTo:7 for <m, c>
                                                                                {
                                                                                        // InsertMonitor
                                                                                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                                                                        if ((node_m == null) ) {
                                                                                                node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                                                                Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                                                                                node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                                                                                node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor> node_m_c = node_m.getValue1() .getNode(wr_c) ;
                                                                                        if ((node_m_c == null) ) {
                                                                                                node_m_c = new Tuple3<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>() ;
                                                                                                node_m.getValue1() .putNode(wr_c, node_m_c) ;
                                                                                                node_m_c.setValue1(new MapOfMonitor<IMap_UnsafeIteratorMonitor>(2) ) ;
                                                                                                node_m_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                                                                        }
                                                                                        Map_UnsafeIteratorMonitor_Set targetSet = node_m_c.getValue2() ;
                                                                                        targetSet.add(created) ;
                                                                                }
                                                                                observer.onIndexingTreeNodeInserted(Map_UnsafeIterator_m_c_i_Map, created, wr_m, wr_c) ;
                                                                        }
                                                                }
                                                        }
                                                }
                                                sourceSet.eraseRange(numalive) ;
                                        }
                                }
                                // D(X) main:6
                                IMap_UnsafeIteratorMonitor disableUpdatedLeaf = matchedEntry.getValue2() ;
                                if ((disableUpdatedLeaf == null) ) {
                                        Map_UnsafeIteratorDisableHolder holder = new Map_UnsafeIteratorDisableHolder(-1) ;
                                        matchedEntry.setValue2(holder) ;
                                        disableUpdatedLeaf = holder;
                                }
                                disableUpdatedLeaf.setDisable(Map_UnsafeIterator_timestamp.getAndIncrement() ) ;
                                observer.onDisableFieldUpdated(disableUpdatedLeaf) ;
                        }
                        // D(X) main:8--9
                        Map_UnsafeIteratorMonitor_Set stateTransitionedSet = matchedEntry.getValue1() ;
                        stateTransitionedSet.event_getiter(c, i);
                        matchMapUnsafeIteratorMonitorProp1 = stateTransitionedSet.matchMapUnsafeIteratorMonitorProp1;

                        observer.onMonitorTransitioned(stateTransitionedSet) ;
                        if ((cachehit == false) ) {
                                Map_UnsafeIterator_c_i_Map_cachekey_c.set(c) ;
                                Map_UnsafeIterator_c_i_Map_cachekey_i.set(i) ;
                                Map_UnsafeIterator_c_i_Map_cachevalue.set(matchedEntry) ;
                                observer.onIndexingTreeCacheUpdated("Map_UnsafeIterator_c_i_Map_cachevalue", matchedEntry) ;
                        }

                }
                observer.onEventMethodLeave() ;

        }

        public static final void Map_UnsafeIterator_modifyMapEvent(Map m) {
                observer.onEventMethodEnter("modifyMap", m) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                if (Map_UnsafeIterator_activated.get()) {
                        Map_UnsafeIteratorMonitor.Map_UnsafeIterator_modifyMap_num++;

                        CachedWeakReference wr_m = null;
                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> matchedEntry = null;
                        boolean cachehit = false;
                        if ((m == Map_UnsafeIterator_m_Map_cachekey_m.get() ) ) {
                                matchedEntry = Map_UnsafeIterator_m_Map_cachevalue.get() ;
                                observer.onIndexingTreeCacheHit("Map_UnsafeIterator_m_Map_cachevalue", matchedEntry) ;
                                cachehit = true;
                        }
                        else {
                                observer.onIndexingTreeCacheMissed("Map_UnsafeIterator_m_Map_cachevalue") ;
                                wr_m = MultiSpec_1_Map_RefMap.findOrCreateWeakRef(m);
                                {
                                        // FindOrCreateEntry
                                        Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_m = Map_UnsafeIterator_m_c_i_Map.getNode(wr_m) ;
                                        if ((node_m == null) ) {
                                                node_m = new Tuple3<MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_m_c_i_Map.putNode(wr_m, node_m) ;
                                                node_m.setValue1(new MapOfAll<MapOfMonitor<IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, Map_UnsafeIteratorMonitor>(1) ) ;
                                                node_m.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        matchedEntry = node_m;
                                }
                                observer.onIndexingTreeLookup(Map_UnsafeIterator_m_c_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_m) ;
                        }
                        // D(X) main:1
                        IMap_UnsafeIteratorMonitor matchedLeaf = matchedEntry.getValue3() ;
                        if ((matchedLeaf == null) ) {
                                if ((wr_m == null) ) {
                                        wr_m = MultiSpec_1_Map_RefMap.findOrCreateWeakRef(m);
                                }
                                // D(X) main:6
                                IMap_UnsafeIteratorMonitor disableUpdatedLeaf = matchedEntry.getValue3() ;
                                if ((disableUpdatedLeaf == null) ) {
                                        Map_UnsafeIteratorDisableHolder holder = new Map_UnsafeIteratorDisableHolder(-1) ;
                                        matchedEntry.setValue3(holder) ;
                                        disableUpdatedLeaf = holder;
                                }
                                disableUpdatedLeaf.setDisable(Map_UnsafeIterator_timestamp.getAndIncrement() ) ;
                                observer.onDisableFieldUpdated(disableUpdatedLeaf) ;
                        }
                        // D(X) main:8--9
                        Map_UnsafeIteratorMonitor_Set stateTransitionedSet = matchedEntry.getValue2() ;
                        stateTransitionedSet.event_modifyMap(m);
                        matchMapUnsafeIteratorMonitorProp1 = stateTransitionedSet.matchMapUnsafeIteratorMonitorProp1;

                        observer.onMonitorTransitioned(stateTransitionedSet) ;
                        if ((cachehit == false) ) {
                                Map_UnsafeIterator_m_Map_cachekey_m.set(m) ;
                                Map_UnsafeIterator_m_Map_cachevalue.set(matchedEntry) ;
                                observer.onIndexingTreeCacheUpdated("Map_UnsafeIterator_m_Map_cachevalue", matchedEntry) ;
                        }

                }
                observer.onEventMethodLeave() ;

        }

        public static final void Map_UnsafeIterator_modifyColEvent(Collection c) {
                observer.onEventMethodEnter("modifyCol", c) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                if (Map_UnsafeIterator_activated.get()) {
                        Map_UnsafeIteratorMonitor.Map_UnsafeIterator_modifyCol_num++;

                        CachedWeakReference wr_c = null;
                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> matchedEntry = null;
                        boolean cachehit = false;
                        if ((c == Map_UnsafeIterator_c_Map_cachekey_c.get() ) ) {
                                matchedEntry = Map_UnsafeIterator_c_Map_cachevalue.get() ;
                                observer.onIndexingTreeCacheHit("Map_UnsafeIterator_c_Map_cachevalue", matchedEntry) ;
                                cachehit = true;
                        }
                        else {
                                observer.onIndexingTreeCacheMissed("Map_UnsafeIterator_c_Map_cachevalue") ;
                                wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                                {
                                        // FindOrCreateEntry
                                        Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_c = Map_UnsafeIterator_c_i_Map.getNode(wr_c) ;
                                        if ((node_c == null) ) {
                                                node_c = new Tuple3<MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>, Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_c_i_Map.putNode(wr_c, node_c) ;
                                                node_c.setValue1(new MapOfSetMonitor<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>(1) ) ;
                                                node_c.setValue2(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        matchedEntry = node_c;
                                }
                                observer.onIndexingTreeLookup(Map_UnsafeIterator_c_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_c) ;
                        }
                        // D(X) main:1
                        IMap_UnsafeIteratorMonitor matchedLeaf = matchedEntry.getValue3() ;
                        if ((matchedLeaf == null) ) {
                                if ((wr_c == null) ) {
                                        wr_c = MultiSpec_1_Collection_RefMap.findOrCreateWeakRef(c);
                                }
                                // D(X) main:6
                                IMap_UnsafeIteratorMonitor disableUpdatedLeaf = matchedEntry.getValue3() ;
                                if ((disableUpdatedLeaf == null) ) {
                                        Map_UnsafeIteratorDisableHolder holder = new Map_UnsafeIteratorDisableHolder(-1) ;
                                        matchedEntry.setValue3(holder) ;
                                        disableUpdatedLeaf = holder;
                                }
                                disableUpdatedLeaf.setDisable(Map_UnsafeIterator_timestamp.getAndIncrement() ) ;
                                observer.onDisableFieldUpdated(disableUpdatedLeaf) ;
                        }
                        // D(X) main:8--9
                        Map_UnsafeIteratorMonitor_Set stateTransitionedSet = matchedEntry.getValue2() ;
                        stateTransitionedSet.event_modifyCol(c);
                        matchMapUnsafeIteratorMonitorProp1 = stateTransitionedSet.matchMapUnsafeIteratorMonitorProp1;

                        observer.onMonitorTransitioned(stateTransitionedSet) ;
                        if ((cachehit == false) ) {
                                Map_UnsafeIterator_c_Map_cachekey_c.set(c) ;
                                Map_UnsafeIterator_c_Map_cachevalue.set(matchedEntry) ;
                                observer.onIndexingTreeCacheUpdated("Map_UnsafeIterator_c_Map_cachevalue", matchedEntry) ;
                        }

                }
                observer.onEventMethodLeave() ;

        }

        public static final void Map_UnsafeIterator_useiterEvent(Iterator i) {
                observer.onEventMethodEnter("useiter", i) ;

                MultiSpec_1_Statistics.numTotalEvents++;
                if (Map_UnsafeIterator_activated.get()) {
                        Map_UnsafeIteratorMonitor.Map_UnsafeIterator_useiter_num++;

                        CachedWeakReference wr_i = null;
                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> matchedEntry = null;
                        boolean cachehit = false;
                        if ((i == Map_UnsafeIterator_i_Map_cachekey_i.get() ) ) {
                                matchedEntry = Map_UnsafeIterator_i_Map_cachevalue.get() ;
                                observer.onIndexingTreeCacheHit("Map_UnsafeIterator_i_Map_cachevalue", matchedEntry) ;
                                cachehit = true;
                        }
                        else {
                                observer.onIndexingTreeCacheMissed("Map_UnsafeIterator_i_Map_cachevalue") ;
                                wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                                {
                                        // FindOrCreateEntry
                                        Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor> node_i = Map_UnsafeIterator_i_Map.getNode(wr_i) ;
                                        if ((node_i == null) ) {
                                                node_i = new Tuple2<Map_UnsafeIteratorMonitor_Set, IMap_UnsafeIteratorMonitor>() ;
                                                Map_UnsafeIterator_i_Map.putNode(wr_i, node_i) ;
                                                node_i.setValue1(new Map_UnsafeIteratorMonitor_Set() ) ;
                                        }
                                        matchedEntry = node_i;
                                }
                                observer.onIndexingTreeLookup(Map_UnsafeIterator_i_Map, LookupPurpose.TransitionedMonitor, matchedEntry, wr_i) ;
                        }
                        // D(X) main:1
                        IMap_UnsafeIteratorMonitor matchedLeaf = matchedEntry.getValue2() ;
                        if ((matchedLeaf == null) ) {
                                if ((wr_i == null) ) {
                                        wr_i = MultiSpec_1_Iterator_RefMap.findOrCreateWeakRef(i);
                                }
                                // D(X) main:6
                                IMap_UnsafeIteratorMonitor disableUpdatedLeaf = matchedEntry.getValue2() ;
                                if ((disableUpdatedLeaf == null) ) {
                                        Map_UnsafeIteratorDisableHolder holder = new Map_UnsafeIteratorDisableHolder(-1) ;
                                        matchedEntry.setValue2(holder) ;
                                        disableUpdatedLeaf = holder;
                                }
                                disableUpdatedLeaf.setDisable(Map_UnsafeIterator_timestamp.getAndIncrement() ) ;
                                observer.onDisableFieldUpdated(disableUpdatedLeaf) ;
                        }
                        // D(X) main:8--9
                        Map_UnsafeIteratorMonitor_Set stateTransitionedSet = matchedEntry.getValue1() ;
                        stateTransitionedSet.event_useiter(i);
                        matchMapUnsafeIteratorMonitorProp1 = stateTransitionedSet.matchMapUnsafeIteratorMonitorProp1;

                        observer.onMonitorTransitioned(stateTransitionedSet) ;
                        if ((cachehit == false) ) {
                                Map_UnsafeIterator_i_Map_cachekey_i.set(i) ;
                                Map_UnsafeIterator_i_Map_cachevalue.set(matchedEntry) ;
                                observer.onIndexingTreeCacheUpdated("Map_UnsafeIterator_i_Map_cachevalue", matchedEntry) ;
                        }

                }
                observer.onEventMethodLeave() ;

        }

}
