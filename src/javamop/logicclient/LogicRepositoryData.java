package javamop.logicclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javamop.MOPException;
import javamop.parser.logicrepositorysyntax.*;

public class LogicRepositoryData {

	private LogicRepositoryType xmlData = null;
	private ByteArrayOutputStream outputStreamData = null;
	private ByteArrayInputStream inputStreamData = null;
	
	public LogicRepositoryData(LogicRepositoryType xmlData) throws MOPException{
		this.xmlData = xmlData;
	}
	
	public LogicRepositoryData(ByteArrayOutputStream outputStreamData) throws MOPException{
		this.outputStreamData = outputStreamData;
	}

	public LogicRepositoryData(ByteArrayInputStream inputStreamData) throws MOPException{
		this.inputStreamData = inputStreamData;
	}

	public LogicRepositoryData(InputStream inputStreamData) throws MOPException{
		this.xmlData = transInputStreamToXML(inputStreamData);
	}
	
	public LogicRepositoryType getXML() throws MOPException{
		if(xmlData != null)
			return xmlData;
		if(outputStreamData != null){
			xmlData = transOutputStreamToXML(outputStreamData);
			return xmlData;
		}
		if(inputStreamData != null){
			xmlData = transInputStreamToXML(inputStreamData);
			return xmlData;
		}
		throw new MOPException("Logic Repository Data Error");
	}
	
	public ByteArrayOutputStream getOutputStream() throws MOPException{
		if(outputStreamData != null){
			return outputStreamData;
		}
		if(xmlData != null){
			outputStreamData = transXMLToOutputStream(xmlData);
			return outputStreamData;
		}
		if(inputStreamData != null){
			xmlData = transInputStreamToXML(inputStreamData);
			outputStreamData = transXMLToOutputStream(xmlData);
			return outputStreamData;
		}
		throw new MOPException("Logic Repository Data Error");
	}
	
	public ByteArrayInputStream getInputStream() throws MOPException{
		if(inputStreamData != null){
			inputStreamData.reset();
			return inputStreamData;
		}
		if(xmlData != null){
			inputStreamData = transXMLToInputStream(xmlData);
			inputStreamData.reset();
			return inputStreamData;
		}
		if(outputStreamData != null){
			xmlData = transOutputStreamToXML(outputStreamData);
			inputStreamData = transXMLToInputStream(xmlData);
			inputStreamData.reset();
			return inputStreamData;
		}
		throw new MOPException("Logic Repository Data Error");
	}
	
	public void updateXML(){
		outputStreamData = null;
		inputStreamData = null;
	}
	
	public ByteArrayOutputStream transXMLToOutputStream(LogicRepositoryType xmlData) throws MOPException {
		JAXBContext logicData;
		ByteArrayOutputStream outputStream;
		try {
			logicData = JAXBContext.newInstance("javamop.parser.logicrepositorysyntax");
			Marshaller marshaller = logicData.createMarshaller();
			javamop.parser.logicrepositorysyntax.ObjectFactory factory = new javamop.parser.logicrepositorysyntax.ObjectFactory();
			JAXBElement<LogicRepositoryType> logicData2 = factory.createMop(xmlData);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(logicData2, os);
			outputStream = os;
		} catch (Exception e) {
			throw new MOPException(e.getMessage());
		}

		return outputStream;
	}

	public ByteArrayInputStream transXMLToInputStream(LogicRepositoryType xmlData) throws MOPException {
		ByteArrayOutputStream os = transXMLToOutputStream(xmlData); 
		return new ByteArrayInputStream(os.toByteArray());
	}

	public LogicRepositoryType transOutputStreamToXML(ByteArrayOutputStream outputStream) throws MOPException {
		LogicRepositoryType xmlData;
		ByteArrayInputStream parserInput = new ByteArrayInputStream(outputStream.toByteArray());

		return transInputStreamToXML(parserInput);
	}

	public LogicRepositoryType transInputStreamToXML(InputStream inputStream) throws MOPException {
		LogicRepositoryType xmlData;
		InputStream parserInput = inputStream;

		JAXBContext logicRequest;
		try {
			logicRequest = JAXBContext.newInstance("javamop.parser.logicrepositorysyntax");
			Unmarshaller unmarshaller = logicRequest.createUnmarshaller();			
			xmlData = ((JAXBElement<LogicRepositoryType>) unmarshaller.unmarshal(parserInput)).getValue();
		} catch (Exception e) {
			throw new MOPException(e.getMessage());
		}

		return xmlData;
	}

	
}
