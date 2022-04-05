package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.util.FileUtils;

public class GLRGen {
    static String gen(CFG g, HashMap<Terminal, Integer> tmap, String name) {
        return gen(new LR(g, tmap), name);
    }

    static String gen(CFG g) {
        return gen(g, "Foo");
    }

    static String gen(CFG g, String name) {
        HashMap<Terminal, Integer> tmap = new HashMap<Terminal, Integer>();
        int tint = 1;
        for (Terminal t : g.terminals())
            tmap.put(t, tint++);
        return gen(g, tmap, name);
    }

    static String gen(LR lr, String name) {
        return "package com.runtimeverification.rvmonitor.java.rvj.LogicPluginShells.JavaCFG.CFGUtil;\nimport java.util.*;\n"
                + "class "
                + name
                + " {\n"
                + state(lr)
                + "\n"
                + name
                + "() {"
                + init(lr)
                + "\n}\n"
                + "public void process(int $event$) {\n"
                + body()
                + "\n}\n"
                + "public Category cat() { switch ($cat$) { case 0: return Category.ACCEPT; case 1: return Category.UNKNOWN; case 2: return Category.FAIL; } return Category.FAIL;}}";
    }

    // See body.txt
    public static String body() {
        return FileUtils.extractFileFromJar(GLRGen.class, "body.txt");
    }

    public static String init(LR lr) {
        return "IntStack stack = new IntStack();\n" + "stack.push(-2);\n"
                + "stack.push(" + Integer.toString(lr.q0) + ");\n"
                + "$stacks$.add(stack);";
    }

    public static String cinit(LR lr) {
        return "__RV_stack  *stack = __RV_new_RV_stack(10);\n"
                + "__RV_push(stack, -2);\n" + "__RV_push(stack, " + lr.q0
                + ");\n" + "__RV_add(__RV_stacks_inst, stack);\n";
    }

    public static String reset(LR lr) {
        return "$stacks$.clear();\n" + init(lr);
    }

    public static String creset(LR lr) {
        return "__RV_clear(__RV_stacks_inst);\n" + cinit(lr);
    }

    public static String state(LR lr) {
        return "/* %%_%_CFG_%_%% */"
                + "ArrayList<IntStack> $stacks$ = new ArrayList<IntStack>();\nstatic int[][] $gt$ = "
                + lr.gtString()
                + ";\nstatic int[][][][] $at$ = "
                + lr.atString()
                + ";\n"
                + "static boolean[] $acc$ = "
                + lr.accString()
                + ";\n"
                + "int $cat$; // ACCEPT = 0, UNKNOWN = 1, FAIL = 2\nint $event$ = -1;";
    }

    public static String cstate(LR lr) {
        return "__RV_stacks *__RV_stacks_inst = NULL;\n" + lr.cgtString()
                + lr.catString() + lr.caccString()
                + "static int __RV_cat; //ACCEPT = 0, UNKNOWN = 1, FAIL = 2\n"
                + "static int __RV_event$ = -1;\n";
    }

    public static String match() {
        return "$cat$ == 0";
    }

    public static String fail() {
        return "$cat$ == 2";
    }
}
