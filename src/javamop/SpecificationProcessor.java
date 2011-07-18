/**
 * @author Feng Chen, Dongyun Jin
 * The class handling the specification file 
 */

package javamop;

import java.io.ByteArrayInputStream;

import javamop.parser.ast.MOPSpecFile;
import javamop.parser.main_parser.JavaMOPParser;

public class SpecificationProcessor extends MOPProcessor{

	public SpecificationProcessor(String name) {
		super(name);
	}

	public String process(String content) throws MOPException {

		//parse a specification file
		MOPSpecFile mop_spec_file;
		try {
			mop_spec_file = JavaMOPParser.parse(new ByteArrayInputStream(content.getBytes()));
		} catch (Exception e) {
			throw new MOPException("Error when parsing a specification file:\n" + e.getMessage());
		}
		
		//use the parent class to process mop specification
		return super.process(mop_spec_file);
	}
}
