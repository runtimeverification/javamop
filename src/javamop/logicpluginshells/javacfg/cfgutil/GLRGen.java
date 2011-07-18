package javamop.logicpluginshells.javacfg.cfgutil;

import java.util.*;

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
		return "package javamop.LogicPluginShells.JavaCFG.CFGUtil;\nimport java.util.*;\n"
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
		return "if ($cat$ != 2) {\n" + "$event$--;\n" + "$cat$ = 1;\n" + "for (int $i$ = $stacks$.size()-1; $i$ >=0; $i$--) {\n"
				+ "IntStack stack = $stacks$.get($i$);\n" + "$stacks$.set($i$,null);\n" + "while (stack != null) {\n" + "int s = stack.peek();\n"
				+ "if (s >= 0 && $at$[s][$event$].length >= 0) {\n" + "/* not in an error state and something to do? */\n"
				+ "for (int j = 0; j < $at$[s][$event$].length; j++) {\n" + "IntStack tstack;\n" + "if ($at$[s][$event$].length > 1){\n"
				+ "tstack = stack.fclone();\n" + "} else{\n" + "tstack = stack;\n" + "}\n" + "switch ($at$[s][$event$][j].length) {\n" + "case 1:/* Shift */\n"
				+ "tstack.push($at$[s][$event$][j][0]);\n" + "$stacks$.add(tstack);\n" + "if ($acc$[$at$[s][$event$][j][0]]) $cat$ = 0;\n" + "break;\n"
				+ "case 2: /* Reduce */\n" + "tstack.pop($at$[s][$event$][j][1]);\n" + "int $old$ = tstack.peek();\n"
				+ "tstack.push($gt$[$old$][$at$[s][$event$][j][0]]);\n" + "$stacks$.add($i$,tstack);\n" + "break;\n" + "}\n" + "}\n" + "}\n"
				+ "stack = $stacks$.get($i$);\n" + "$stacks$.remove($i$);\n" + "}\n" + "}\n" + "if ($stacks$.isEmpty())\n" + "$cat$ = 2;\n" + "}\n";
	}

	public static String init(LR lr) {
		return "IntStack stack = new IntStack();\nstack.push(-2);\nstack.push(" + Integer.toString(lr.q0) + ");\n$stacks$.add(stack);";
	}

	public static String reset(LR lr) {
		return "$stacks$.clear();\n" + init(lr);
	}

	public static String state(LR lr) {
		return "/* %%_%_CFG_%_%% */" + "ArrayList<IntStack> $stacks$ = new ArrayList<IntStack>();\nstatic int[][] $gt$ = " + lr.gtString()
				+ ";\nstatic int[][][][] $at$ = " + lr.atString() + ";\n" + "static boolean[] $acc$ = " + lr.accString() + ";;\n"
				+ "int $cat$; // ACCEPT = 0, UNKNOWN = 1, FAIL = 2\nint $event$ = -1;";
	}

	public static String match() {
		return "$cat$ == 0";
	}

	public static String fail() {
		return "$cat$ == 2";
	}

	public static String intstack = "class IntStack implements java.io.Serializable {\n" + "int[] data;\n" + "int curr_index = 0;\n" + "public IntStack(){\n"
			+ "data = new int[32];\n" + "}\n" + "public String toString(){\n" + "String ret = \"[\";\n" + "for (int i = curr_index; i>=0; i--){\n"
			+ "ret += Integer.toString(data[i])+\",\";\n" + "}\n" + "return ret+\"]\";\n" + "}\n" + "public int hashCode() {\n" + "return curr_index^peek();\n"
			+ "}\n" + "public boolean equals(Object o) {\n" + "if (o == null) return false;\n" + "if (!(o instanceof IntStack)) return false;\n"
			+ "IntStack s = (IntStack)o;\n" + "if(curr_index != s.curr_index) return false;\n" + "for(int i = 0; i < curr_index; i++){\n"
			+ "if(data[i] != s.data[i]) return false;\n" + "}\n" + "return true;\n" + "}\n" + "public IntStack(int size){\n" + "data = new int[size];\n" + "}\n"
			+ "public int peek(){\n" + "return data[curr_index - 1];\n" + "}\n" + "public int pop(){\n" + "return data[--curr_index];\n" + "}\n"
			+ "public void pop(int num){\n" + "curr_index -= num;\n" + "}\n" + "public void push(int datum){\n" + "if(curr_index < data.length){\n"
			+ "data[curr_index++] = datum;\n" + "} else{\n" + "int len = data.length;\n" + "int[] old = data;\n" + "data = new int[len * 2];\n"
			+ "for(int i = 0; i < len; ++i){\n" + "data[i] = old[i];\n" + "}\n" + "data[curr_index++] = datum;\n" + "}\n" + "}\n" + "public IntStack fclone(){\n"
			+ "IntStack ret = new IntStack(data.length);\n" + "ret.curr_index = curr_index;\n" + "for(int i = 0; i < curr_index; ++i){\n"
			+ "ret.data[i] = data[i];\n" + "}\n" + "return ret;\n" + "}\n" + "public void clear(){\n" + "curr_index = 0;\n" + "}\n" + "}\n";
}
