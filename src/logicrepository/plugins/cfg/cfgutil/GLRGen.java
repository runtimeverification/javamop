package logicrepository.plugins.cfg.cfgutil;

import java.util.*;

public class GLRGen {
   static String gen(CFG g, HashMap<Terminal,Integer> tmap, String name) {
      return gen(new LR(g,tmap),name);
   }
   static String gen(CFG g) { return gen(g,"Foo"); }
   static String gen(CFG g, String name) {
      HashMap<Terminal,Integer> tmap = new HashMap<Terminal,Integer>();
      int tint = 1;
      for (Terminal t : g.terminals())
         tmap.put(t,tint++);
      return gen(g,tmap,name);
   }
   static String gen(LR lr, String name) {
      return "package javamop.LogicPluginShells.JavaCFG.CFGUtil;\nimport java.util.*;"
         + "class "+name+" {\n" + state(lr) +"\n"
         + name +"() {"+init(lr)+"\n}\n"
         + "public void process(int it) {\n" + body() + "\n}\n"
         + "public Category cat() { switch ($cat) { case 0: return Category.ACCEPT; case 1: return Category.UNKNOWN; case 2: return Category.FAIL; } return Category.FAIL;}}";
   }

   // See body.txt
   public static String body() { return
      " if ($cat != 2) { $cat = 1; for (int i = $stacks.size()-1; i >=0; i--) { IntStack stack = $stacks.get(i); $stacks.set(i,null); while (stack != null) { int s = stack.peek(); if (s >= 0 && $at[s][it].length >= 0) { /* not in an error state and something to do? */ for (int j = 0; j < $at[s][it].length; j++) { IntStack tstack; if ($at[s][it].length > 1) tstack = stack.fclone(); else tstack = stack; switch ($at[s][it][j].length) { case 1: /* Shift */ tstack.push(it); tstack.push($at[s][it][j][0]); $stacks.add(tstack); if ($at[$at[s][it][j][0]][0].length == 1) $cat = 0; break; case 2: /* Reduce */ tstack.pop($at[s][it][j][1]); int t = tstack.peek(); tstack.push($at[s][it][j][0]); tstack.push($gt[t][$at[s][it][j][0]]); $stacks.add(i,tstack); break; } } } stack = $stacks.get(i); $stacks.remove(i); } } if ($stacks.isEmpty()) $cat = 2; }"
         ;}

   public static String init(LR lr) { return "IntStack stack = new IntStack();\nstack.push(-2);\nstack.push("+Integer.toString(lr.start)+");\nstack.push("+Integer.toString(lr.q0)+");\n$stacks.add(stack);"; }

   public static String reset(LR lr) { return "$stacks.clear()\n"+init(lr); }

   public static String state(LR lr) { return "/* %%_%_CFG_%_%% */"
     + "ArrayList<IntStack> $stacks = new ArrayList<IntStack>();\nstatic int[][] $gt = "+lr.gtString()+";\nstatic int[][][][] $at = "+lr.atString()+";\n"
     + "int $cat; // ACCEPT = 0, UNKNOWN = 1, FAIL = 2\n";
     }

   public static String match() { return "$cat == 0"; }
   public static String fail() { return "$cat == 2"; }
   public static String intstack = "package javamop.LogicPluginShells.JavaCFG.CFGUtil; public class IntStack implements java.io.Serializable { int[] data; int curr_index = 0; public IntStack(){ data = new int[32]; } public String toString(){ String ret = \"[\"; for (int i = curr_index; i>=0; i--) ret += Integer.toString(data[i])+\",\"; return ret+\"]\"; } public int hashCode() { return curr_index^peek(); } public boolean equals(Object o) { if (o == null) return false; if (!(o instanceof IntStack)) return false; IntStack s = (IntStack)o; if(curr_index != s.curr_index) return false; for(int i = 0; i < curr_index; i++) if(data[i] != s.data[i]) return false; return true; } public IntStack(int size){ data = new int[size]; } public int peek(){ return data[curr_index - 1]; } public int pop(){ return data[--curr_index]; } public void pop(int num){ curr_index -= num; } public void push(int datum){ if(curr_index < data.length) data[curr_index++] = datum; else{ int len = data.length; int[] old = data; data = new int[len * 2]; for(int i = 0; i < len; ++i){ data[i] = old[i]; } data[curr_index++] = datum; } } public IntStack fclone(){ IntStack ret = new IntStack(data.length); ret.curr_index = curr_index; for(int i = 0; i < curr_index; ++i){ ret.data[i] = data[i]; } return ret; } public void clear(){ curr_index = 0; } }";
}
