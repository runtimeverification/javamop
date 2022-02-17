package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.util.*;

public class GLRGen {
    
    private static String gen(CFG g, HashMap<Terminal,Integer> tmap, String name) {
        return gen(new LR(g,tmap),name);
    }
    
    private static String gen(CFG g) { 
        return gen(g,"Foo");
    }
    
    private static String gen(CFG g, String name) {
        HashMap<Terminal,Integer> tmap = new HashMap<Terminal,Integer>();
        int tint = 1;
        for (Terminal t : g.terminals())
            tmap.put(t,tint++);
        return gen(g,tmap,name);
    }
    
    public static String gen(LR lr, String name) {
        return "package com.runtimeverification.rvmonitor.java.rvj.LogicPluginShells.JavaCFG.CFGUtil;\nimport java.util.*;"
        + "class "+name+" {\n" + state(lr) +"\n"
        + name +"() {"+init(lr)+"\n}\n"
        + "public void process(int it) {\n" + body() + "\n}\n"
        + "public Category cat() { switch ($cat) { case 0: return Category.ACCEPT; case 1: return Category.UNKNOWN; case 2: return Category.FAIL; } return Category.FAIL;}}";
    }
    
    // See body.txt
    public static String body() { 
        return " if ($cat != 2) { $cat = 1; for (int i = $stacks.size()-1; i >=0; i--) { IntStack stack = $stacks.get(i); $stacks.set(i,null); while (stack != null) { int s = stack.peek(); if (s >= 0 && $at[s][it].length >= 0) { /* not in an error state and something to do? */ for (int j = 0; j < $at[s][it].length; j++) { IntStack tstack; if ($at[s][it].length > 1) tstack = stack.fclone(); else tstack = stack; switch ($at[s][it][j].length) { case 1: /* Shift */ tstack.push(it); tstack.push($at[s][it][j][0]); $stacks.add(tstack); if ($at[$at[s][it][j][0]][0].length == 1) $cat = 0; break; case 2: /* Reduce */ tstack.pop($at[s][it][j][1]); int t = tstack.peek(); tstack.push($at[s][it][j][0]); tstack.push($gt[t][$at[s][it][j][0]]); $stacks.add(i,tstack); break; } } } stack = $stacks.get(i); $stacks.remove(i); } } if ($stacks.isEmpty()) $cat = 2; }";
    }
        
    public static String init(LR lr) { 
        return "IntStack stack = new IntStack();\nstack.push(-2);\nstack.push("+Integer.toString(lr.getStart())+");\nstack.push("+Integer.toString(lr.getQ0())+");\n$stacks.add(stack);";
    }
    
    public static String reset(LR lr) { 
        return "$stacks.clear()\n"+init(lr);
    }
    
    public static String state(LR lr) { 
        return "/* %%_%_CFG_%_%% */"
            + "ArrayList<IntStack> $stacks = new ArrayList<IntStack>();\nstatic int[][] $gt = "+lr.gtString()+";\nstatic int[][][][] $at = "+lr.atString()+";\n"
            + "int $cat; // ACCEPT = 0, UNKNOWN = 1, FAIL = 2\n";
    }
    
    public static String match() { 
        return "$cat == 0";
    }
    
    public static String fail() { 
        return "$cat == 2";
    }
}
