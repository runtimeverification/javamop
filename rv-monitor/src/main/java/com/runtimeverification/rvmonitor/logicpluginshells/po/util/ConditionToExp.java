package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

public class ConditionToExp {

    public static String convert(Condition c) {
        if (c instanceof ANDCondition) {
            return "(" + convert(((ANDCondition) c).con1) + ") && ("
                    + convert(((ANDCondition) c).con2) + ")";
        }

        if (c instanceof ORCondition) {
            return "(" + convert(((ORCondition) c).con1) + ") || ("
                    + convert(((ORCondition) c).con2) + ")";
        }

        if (c instanceof BlockCondition) {
            String ret = "";
            ret += "$" + ((BlockCondition) c).getBeforeEvent() + "_on$";
            ret += " && " + "$" + ((BlockCondition) c).getBeforeEvent()
                    + "_Thread$ != null";

            if (((BlockCondition) c).getBlockEvent() != null)
                ret += " && " + "!$" + ((BlockCondition) c).getBlockEvent()
                        + "_on$";

            ret += " && (" + "$" + ((BlockCondition) c).getBeforeEvent()
                    + "_Thread$.getState() == Thread.State.BLOCKED";
            ret += " || " + "$" + ((BlockCondition) c).getBeforeEvent()
                    + "_Thread$.getState() == Thread.State.WAITING";
            ret += " || " + "$" + ((BlockCondition) c).getBeforeEvent()
                    + "_Thread$.getState() == Thread.State.TIMED_WAITING";
            ret += ")";

            return ret;
        }

        if (c instanceof SimpleCondition) {
            return "$" + ((SimpleCondition) c).getBeforeEvent() + "_on$";
        }

        if (c instanceof NotCondition) {
            return "!(" + convert(((NotCondition) c).getCondition()) + ")";
        }

        return "";
    }
}
