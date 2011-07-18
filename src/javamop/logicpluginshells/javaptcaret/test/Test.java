package javamop.logicpluginshells.javaptcaret.test;

import javamop.logicpluginshells.javaptcaret.ast.PseudoCode;
import javamop.logicpluginshells.javaptcaret.parser.PTCARET_PseudoCode_Parser;
import javamop.logicpluginshells.javaptcaret.parser.ParseException;

public class Test {

	public static void main(String[] args) {
		String input = "";

		input += "$beta$[0] := ($alpha$[0] || b && $beta$[0]);\n";
		input += "$alpha$[1] := ($beta$[0] || a && $alpha$[1]);\n";
		input += "output($alpha$[1])\n";
		input += "$alpha$[0] := a;\n";

		PseudoCode code;

		try {
			code = PTCARET_PseudoCode_Parser.parse(input);

			System.out.println(code);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
}
