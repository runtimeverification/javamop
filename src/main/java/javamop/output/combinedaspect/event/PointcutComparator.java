// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event;

import javamop.parser.ast.aspectj.*;

import java.util.ArrayList;

/**
 * Compares the various types of pointcuts with themselves. Unless otherwise specified, each
 * comparison function just compares the string representations of the two pointcuts.
 */
public class PointcutComparator {

    public boolean compare(PointCut p1, PointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(ArgsPointCut p1, ArgsPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    /**
     * Compare two composite pointcuts for equality by comparing their members.
     * @param p1 The first pointcut.
     * @param p2 The second pointcut.
     * @return If the two pointcuts are equal.
     */
    public boolean compare(CombinedPointCut p1, CombinedPointCut p2){
        ArrayList<PointCut> list2 = new ArrayList<PointCut>();
        list2.addAll(p2.getPointcuts());

        for(PointCut p3 : p1.getPointcuts()){
            boolean found = false;
            for(PointCut p4 : p2.getPointcuts()){
                if(compare(p3, p4)){
                    found = true;
                    list2.remove(p4);
                    break;
                }
            }

            if(!found)
                return false;
        }

        for(PointCut p3 : list2){
            boolean found = false;
            for(PointCut p4 : p1.getPointcuts()){
                if(compare(p3, p4)){
                    found = true;
                    break;
                }
            }

            if(!found)
                return false;
        }

        return true;
    }

    public boolean compare(NotPointCut p1, NotPointCut p2){
        return compare(p1.getPointCut(), p2.getPointCut());
    }

    public boolean compare(ConditionPointCut p1, ConditionPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(FieldPointCut p1, FieldPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(MethodPointCut p1, MethodPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(TargetPointCut p1, TargetPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(ThisPointCut p1, ThisPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(CFlowPointCut p1, CFlowPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(IFPointCut p1, IFPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(IDPointCut p1, IDPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(WithinPointCut p1, WithinPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    /**
     * ThreadNamePointCuts are always equal.
     * @param p1 The first pointcut.
     * @param p2 The second pointcut.
     * @return {@code true}.
     */
    public boolean compare(ThreadNamePointCut p1, ThreadNamePointCut p2){
        return true;
    }

    /**
     * ThreadBlockedPointCuts are always equal.
     * @param p1 The first pointcut.
     * @param p2 The second pointcut.
     * @return {@code true}.
     */
    public boolean compare(ThreadBlockedPointCut p1, ThreadBlockedPointCut p2){
        return true;
    }

    public boolean compare(ThreadPointCut p1, ThreadPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(EndProgramPointCut p1, EndProgramPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(EndThreadPointCut p1, EndThreadPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(EndObjectPointCut p1, EndObjectPointCut p2){
        return p1.toString().equals(p2.toString());
    }

    public boolean compare(StartThreadPointCut p1, StartThreadPointCut p2){
        return p1.toString().equals(p2.toString());
    }

}
