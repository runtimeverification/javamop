// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser;

import java.io.File;

import javamop.parser.ast.MOPSpecFile;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.main_parser.JavaMOPParser;

public class Main {

	public static void main(String[] args) {
		try {
			MOPSpecFileExt f = JavaMOPParser.parse(new File(args[0]));
			MOPSpecFile o = JavaMOPExtender.translateMopSpecFile(f);
			
			System.out.print(o.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
