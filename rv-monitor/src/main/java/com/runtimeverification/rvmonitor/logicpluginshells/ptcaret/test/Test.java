package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.test;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.parser.PTCARETParser;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.parser.ParseException;

public class Test {

    public static void main(String[] args) {
        String input = "";

        input += "$beta$[0] := ($alpha$[0] || b && $beta$[0]);\n";
        input += "$alpha$[1] := ($beta$[0] || a && $alpha$[1]);\n";
        input += "output($alpha$[1])\n";
        input += "$alpha$[0] := a;\n";

        PseudoCode code;

        try {
            code = PTCARETParser.parse(input);

            System.out.println(code);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
