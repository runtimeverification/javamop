package javamop;

import java.io.ByteArrayInputStream;

import javamop.parser.JavaMOPExtender;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.main_parser.JavaMOPParser;

/**
 * The class handling annotated Java file
 * @author Feng Chen, Dongyun Jin
 */

public class AnnotationProcessor extends MOPProcessor {
	String javaContent;
	
	public AnnotationProcessor(String name) {
		super(name);
	}

	public String process(String input) throws MOPException {
		String content = getAnnotations(input);
		
		//parse a specification file
		MOPSpecFile mopSpecFile;
		try {
			MOPSpecFileExt mopSpecFileExt = JavaMOPParser.parse(new ByteArrayInputStream(content.getBytes()));
			mopSpecFile = JavaMOPExtender.translateMopSpecFile(mopSpecFileExt);
		} catch (Exception e) {
			throw new MOPException("Error when parsing a specification file:\n" + e.getMessage());
		}
		
		//use the parent class to process mop specification
		return super.process(mopSpecFile);
	}

	protected String getAnnotations(String input) throws MOPException {
		String content = "";
		
		int start = input.indexOf("/*@", 0), end;
		
		while (start > -1) {
			end = input.indexOf("@*/", start);

			if(end > -1)
				content += input.substring(start + 3, end); // 4 means /*@ + a space
			else
				throw new MOPException("annotation block didn't end");
			
			start = input.indexOf("/*@", start + 1);
		}
		return content;
	}

	
	public String getJavaContent(){
		return javaContent;
	}

}
