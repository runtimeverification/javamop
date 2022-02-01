// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event;

import javamop.JavaMOPMain;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.event.advice.AdviceAndPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.visitor.ConvertPointcutToCNFVisitor;
import javamop.util.MOPException;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all the different types of events for one or more specifications.
 */
public class EventManager {

    private ArrayList<AdviceAndPointCut> advices = new ArrayList<AdviceAndPointCut>();
    private final ArrayList<EndObject> endObjectEvents = new ArrayList<EndObject>();
    private final ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
    private final ArrayList<StartThread> startThreadEvents = new ArrayList<StartThread>();
    private final EndProgram endProgramEvent;


    private final MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");

    /**
     * Construct an event manager over multiple specifications.
     *
     * @param name           The name of the event manager.
     * @param specs          All the specifications that this manages events from.
     * @param combinedAspect The AspectJ output for this program.
     */
    public EventManager(final String name, final List<JavaMOPSpec> specs,
                        final CombinedAspect combinedAspect) throws MOPException {

        this.endProgramEvent = new EndProgram(name);

        for (JavaMOPSpec spec : specs) {
            if (spec.isEnforce()) {
                endThreadEvents.add(new ThreadStatusMonitor(spec, combinedAspect));
            }
            for (EventDefinition event : spec.getEvents()) {
                // normal event
                if (!event.isEndObject() && !event.isEndProgram() && !event.isEndThread() &&
                        !event.isStartThread()) {
                    boolean added = false;
                    AdviceAndPointCut cachedAdvice = null;

                    for (AdviceAndPointCut advice : advices) {
                        PointcutComparator comparator = new PointcutComparator();
                        PointCut p1 = event.getPointCut().accept(
                                new ConvertPointcutToCNFVisitor(), null);
                        PointCut p2 = advice.getPointCut().accept(
                                new ConvertPointcutToCNFVisitor(), null);

                        //if pointcut p1 and p2 are equal, then the program location they defined
                        //are the same, in order to reuse it later, cache it now.
                        //Based on other info attached to the event, different join points can be
                        //distinguished and new advice obj can be created if necessary.
                        if (comparator.compare(p1, p2)) {
                            //the advice needs to be cache for future reuse if the input parameter
                            //is the same as that of the event.
                            if (event.getParametersWithoutThreadVar().parameterDeclString().equals(
                                    advice.getParametersDeclStr()
                            ))
                                cachedAdvice = advice;
                        }
                        if (advice.isAround != event.getPos().equals("around"))
                            continue;
                        if (advice.isAround) {
                            if (!advice.retType.equals(event.getRetType().toString()))
                                continue;
                        }

                        if (!advice.pos.equals(event.getPos()))
                            continue;

                        if (!advice.retVal.equals(event.getRetVal()))
                            continue;
                        if (!advice.throwVal.equals(event.getThrowVal()))
                            continue;

                        if (comparator.compare(p1, p2)) {
                            added = advice.addEvent(spec, event, combinedAspect);
                            if (added)
                                break;
                        }
                    }

                    if (!added) {
                        AdviceAndPointCut newAdvice = new AdviceAndPointCut(spec, event,
                                combinedAspect);
                        //using the existing pointcut if there is any
                        if (cachedAdvice != null) {
                            newAdvice = newAdvice.clone(cachedAdvice);
                        }
                        advices.add(newAdvice);
                    }
                }

                // endObject
                if (event.isEndObject()) {
                    endObjectEvents.add(new EndObject(spec, event, combinedAspect));
                }

                // endThread
                if (event.isEndThread()) {
                    endThreadEvents.add(new EndThread(spec, event, combinedAspect));
                }

                // startThread
                if (event.isStartThread()) {
                    startThreadEvents.add(new StartThread(spec, event, combinedAspect));
                }

                // endProgram
                if (event.isEndProgram()) {
                    endProgramEvent.addEndProgramEvent(spec, event, combinedAspect);
                }

            } // end of for event

        } // end of for spec

        endProgramEvent.registerEndThreadEvents(endThreadEvents);

    }

    /**
     * The internals of the constructor code for the event manager.
     *
     * @return Java code for constructing the event manager.
     */
    public String printConstructor() {
        String ret = "";

        if (endProgramEvent != null) {
            ret += endProgramEvent.printAddStatement();
        }

        return ret;
    }

    /**
     * The aggregated AspectJ hooks/advice from the managed events.
     *
     * @return AspectJ/Java code for the managed events.
     */
    public String advices() {
        String ret = "";

        ret += "pointcut " + commonPointcut + "() : ";

        ret += "!within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) " +
                "&& !adviceexecution() && BaseAspect.notwithin();\n";

        int numAdvice = 1;
        advices = this.adjustAdviceOrder();
        for (AdviceAndPointCut advice : advices) {
            if (JavaMOPMain.empty_advicebody) {
                ret += "// " + numAdvice++ + "\n";
            }

            ret += advice.toString();

            ret += "\n";
            if (advice.beCounted) {
                ret += "\n";
                ret += "// Declaration of the count variable for above pointcut\n";
                ret += "static int " + advice.getPointCutName() + "_count = 0;";
                ret += "\n\n\n";
            }
        }

        for (EndObject endObject : endObjectEvents) {
            ret += endObject.printDecl();
            ret += "\n";
        }

        for (EndThread endThread : endThreadEvents) {
            ret += endThread.printAdvices();
            ret += "\n";
        }

        for (StartThread startThread : startThreadEvents) {
            ret += startThread.printAdvices();
            ret += "\n";
        }

        ret += endProgramEvent.printHookThread();

        return ret;
    }

    /**
     * Move "before" advice to the front.
     */
    private ArrayList<AdviceAndPointCut> adjustAdviceOrder() {
        ArrayList<AdviceAndPointCut> result = new ArrayList<AdviceAndPointCut>();
        for (AdviceAndPointCut advice : this.advices) {
            if (advice.pos.equals("before")) {
                result.add(0, advice);
            } else {
                result.add(advice);
            }
        }
        return result;
    }

    /**
     * A utility class with static methods for constructing unique method names.
     */
    public static class EventMethodHelper {

        /**
         * Construct a method name for a particular event.
         *
         * @param specName The name of the specification the event is a part of.
         * @param eventId  The id of the target event from which the method name will be generated.
         * @param aspectName    The aspect the method will be a part of.
         */
        protected static String methodName(String specName, String eventId,
                                           String aspectName) {
            boolean mangle = false;
            if (JavaMOPMain.options.merge && JavaMOPMain.options.aspectname != null &&
                    JavaMOPMain.options.aspectname.length() > 0) {
                mangle = true;
            }

            StringBuilder s = new StringBuilder();
            if (mangle && JavaMOPMain.specifiedAJName) {
                s.append(JavaMOPMain.options.aspectname);
            } else if (JavaMOPMain.options.emop){
                s.append(JavaMOPMain.options.aspectname);
            } else {
                s.append(aspectName);
            }
            s.append("RuntimeMonitor");
            s.append('.');
            if (mangle || JavaMOPMain.options.emop) {
                s.append(specName);
                s.append('_');
            }
            s.append(eventId);
            s.append("Event");
            return s.toString();
        }

        /**
         * Construct a method name for a particular event.
         *
         * @param enclosing  The specification the event is a part of.
         * @param evt        The event the method is being generated for.
         * @param aspectName The aspect the method will be a part of.
         */
        public static String methodName(JavaMOPSpec enclosing, EventDefinition evt,
                                        String aspectName) {
            return methodName(enclosing.getName(), evt.getId(), aspectName);
        }
    }
}
