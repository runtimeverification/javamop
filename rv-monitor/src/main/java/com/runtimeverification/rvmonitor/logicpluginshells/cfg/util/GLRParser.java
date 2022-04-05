package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.util.ArrayList;

class GLRParser implements java.io.Serializable {
    ArrayList<IntStack> stacks = new ArrayList<IntStack>();
    int[][] gt;
    int[][][][] at;
    Category cat = Category.UNKNOWN;

    @Override
    public int hashCode() {
        System.err.println("GLRParser hashCode fail");
        return 0;
    }

    GLRParser(LR in) {
        at = in.atArray();
        gt = in.gt;
        IntStack stack = new IntStack();
        stack.push(-2);
        stack.push(in.start);
        stack.push(in.q0);
        stacks.add(stack);
    }

    public Category cat() {
        return cat;
    }

    public boolean process(int it) {
        if (cat.equals(Category.FAIL))
            return true;

        // Initializaton stuff
        cat = Category.UNKNOWN;

        for (int i = stacks.size() - 1; i >= 0; i--) {
            IntStack stack = stacks.get(i);
            stacks.set(i, null);
            while (stack != null) {
                int s = stack.peek();
                if (s >= 0 && at[s][it].length >= 0) { // not in an error state
                                                       // and something to do?
                    for (int j = 0; j < at[s][it].length; j++) {
                        IntStack tstack;
                        if (at[s][it].length > 1)
                            tstack = stack.fclone();
                        else
                            tstack = stack;
                        switch (at[s][it][j].length) {
                        case 1: // Shift
                            tstack.push(it);
                            tstack.push(at[s][it][j][0]);
                            stacks.add(tstack);
                            if (at[at[s][it][j][0]][0].length == 1)
                                cat = Category.ACCEPT;
                            break;
                        case 2: // Reduce
                            tstack.pop(at[s][it][j][1]);
                            int t = tstack.peek();
                            tstack.push(at[s][it][j][0]);
                            tstack.push(gt[t][at[s][it][j][0]]);
                            stacks.add(i, tstack);
                            break;
                        }
                    }
                }
                stack = stacks.get(i);
                stacks.remove(i);
            }
        }
        if (stacks.isEmpty()) {
            cat = Category.FAIL;
            return true;
        }

        return false;
    }
}
