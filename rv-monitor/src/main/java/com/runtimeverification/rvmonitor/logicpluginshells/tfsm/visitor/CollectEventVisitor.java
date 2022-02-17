package com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.Node;

public class CollectEventVisitor implements
        GenericVisitor<List<String>, Object> {

    @Override
    public List<String> visit(Node n, Object arg) {
        return null;
    }

    @Override
    public List<String> visit(FSMInput f, Object arg) {
        List<String> events = new ArrayList<String>();

        if (f.getItems() != null) {
            for (FSMItem i : f.getItems()) {
                List<String> temp = i.accept(this, arg);

                if (temp != null) {
                    for (String event : temp) {
                        if (!events.contains(event))
                            events.add(event);
                    }
                }
            }
        }
        return events;
    }

    @Override
    public List<String> visit(FSMItem i, Object arg) {
        List<String> events = new ArrayList<String>();

        if (i.getTransitions() != null) {
            for (FSMTransition t : i.getTransitions()) {
                List<String> temp = t.accept(this, arg);

                if (temp != null) {
                    for (String event : temp) {
                        if (!events.contains(event))
                            events.add(event);
                    }
                }
            }
        }

        return events;
    }

    @Override
    public List<String> visit(FSMAlias a, Object arg) {
        return null;
    }

    @Override
    public List<String> visit(FSMTransition t, Object arg) {
        if (t.getEventName() != null) {
            List<String> events = new ArrayList<String>();
            events.add(t.getEventName());
            return events;
        }
        return null;
    }

}
