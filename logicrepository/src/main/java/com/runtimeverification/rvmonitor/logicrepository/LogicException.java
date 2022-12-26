package com.runtimeverification.rvmonitor.logicrepository;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;

public class LogicException extends Exception {
	private static final long serialVersionUID = -1L;

	public LogicException(Exception e) {
		super("Logic Repository Exception:" + e.getMessage());
	}

	public LogicException(String str) {
		super(str);
	}

	public String toString() {
		LogicRepositoryType LogicRepositoryError = new LogicRepositoryType();
		List<String> errMsgs = LogicRepositoryError.getMessage();

		if (this.getMessage() == null)
			errMsgs.add("Unknown");
		else
			errMsgs.add(this.getMessage().trim());

		JAXBContext logicRepositoryParser;
		try {
			logicRepositoryParser = JAXBContext.newInstance("LogicRepository.parser.logicrepositorysyntax");
			Unmarshaller unmarshaller = logicRepositoryParser.createUnmarshaller();

			ByteArrayOutputStream logicPluginOutput;

			Marshaller marshaller = logicRepositoryParser.createMarshaller();
			com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.ObjectFactory factory = new com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.ObjectFactory();
			JAXBElement<LogicRepositoryType> logicResponse = factory.createMop(LogicRepositoryError);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshaller.marshal(logicResponse, os);
			return os.toString();
		} catch (Exception e) {
			return toString2();
		}
	}

	public String toString2() {
		String ret = "";
		ret += "<mop>\n";
		ret += "\t<Message>\n";
		if(this.getMessage() != null){
			if (this.getMessage().endsWith("\n"))
				ret += this.getMessage();
			else
				ret += this.getMessage() + "\n";
		}
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		PrintStream p = new PrintStream(os);;
//		this.printStackTrace(p);
//		ret += os;
		ret += "\t</Message>\n";
		ret += "</mop>\n";

		return ret;
	}
}
