package javamop.parser;

import java.io.*;
import javamop.parser.ast.*;
import javamop.parser.main_parser.*;

public class Main {

	public static void main(String[] args) {
		try {
			MOPSpecFile f = JavaMOPParser.parse(new File(args[0]));
			System.out.print(f.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String reformat(String spec) {
		
		// first, handle the within clause
		int i = spec.indexOf(')');
		int j = spec.indexOf('{');
		int k = spec.lastIndexOf("within ", j);
		
		if (k > i) { // found the within clause
			int n = spec.indexOf("within", i) + "within".length();
			spec = spec.substring(0, n);
			spec += " \"" + spec.substring(n, j).trim() + "\" ";
			spec += spec.substring(j);
		}
		
		// second, handle the event definition
		
		// third, handle the property
		
		
		return spec;
	}
}
