package com.runtimeverification.rvmonitor.java.rvj.logicclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.ObjectFactory;
import com.runtimeverification.rvmonitor.util.RVMException;

public class LogicRepositoryData {

    private LogicRepositoryType xmlData = null;
    private ByteArrayOutputStream outputStreamData = null;
    private ByteArrayInputStream inputStreamData = null;

    public LogicRepositoryData(LogicRepositoryType xmlData) throws RVMException {
        this.xmlData = xmlData;
    }

    public LogicRepositoryData(ByteArrayOutputStream outputStreamData)
            throws RVMException {
        this.outputStreamData = outputStreamData;
    }

    public LogicRepositoryData(ByteArrayInputStream inputStreamData)
            throws RVMException {
        this.inputStreamData = inputStreamData;
    }

    public LogicRepositoryData(InputStream inputStreamData) throws RVMException {
        this.xmlData = transInputStreamToXML(inputStreamData);
    }

    public LogicRepositoryType getXML() throws RVMException {
        if (xmlData != null)
            return xmlData;
        if (outputStreamData != null) {
            xmlData = transOutputStreamToXML(outputStreamData);
            return xmlData;
        }
        if (inputStreamData != null) {
            xmlData = transInputStreamToXML(inputStreamData);
            return xmlData;
        }
        throw new RVMException("Logic Repository Data Error");
    }

    public ByteArrayOutputStream getOutputStream() throws RVMException {
        if (outputStreamData != null) {
            return outputStreamData;
        }
        if (xmlData != null) {
            outputStreamData = transXMLToOutputStream(xmlData);
            return outputStreamData;
        }
        if (inputStreamData != null) {
            xmlData = transInputStreamToXML(inputStreamData);
            outputStreamData = transXMLToOutputStream(xmlData);
            return outputStreamData;
        }
        throw new RVMException("Logic Repository Data Error");
    }

    public ByteArrayInputStream getInputStream() throws RVMException {
        if (inputStreamData != null) {
            inputStreamData.reset();
            return inputStreamData;
        }
        if (xmlData != null) {
            inputStreamData = transXMLToInputStream(xmlData);
            inputStreamData.reset();
            return inputStreamData;
        }
        if (outputStreamData != null) {
            xmlData = transOutputStreamToXML(outputStreamData);
            inputStreamData = transXMLToInputStream(xmlData);
            inputStreamData.reset();
            return inputStreamData;
        }
        throw new RVMException("Logic Repository Data Error");
    }

    public void updateXML() {
        outputStreamData = null;
        inputStreamData = null;
    }

    public ByteArrayOutputStream transXMLToOutputStream(
            LogicRepositoryType xmlData) throws RVMException {
        JAXBContext logicData;
        ByteArrayOutputStream outputStream;
        try {
            logicData = JAXBContext
                    .newInstance("com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax");
            Marshaller marshaller = logicData.createMarshaller();
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<LogicRepositoryType> logicData2 = factory
                    .createMop(xmlData);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.marshal(logicData2, os);
            outputStream = os;
        } catch (Exception e) {
            throw new RVMException(e.getMessage());
        }

        return outputStream;
    }

    public ByteArrayInputStream transXMLToInputStream(
            LogicRepositoryType xmlData) throws RVMException {
        ByteArrayOutputStream os = transXMLToOutputStream(xmlData);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public LogicRepositoryType transOutputStreamToXML(
            ByteArrayOutputStream outputStream) throws RVMException {
        ByteArrayInputStream parserInput = new ByteArrayInputStream(
                outputStream.toByteArray());

        return transInputStreamToXML(parserInput);
    }

    @SuppressWarnings("unchecked")
    public LogicRepositoryType transInputStreamToXML(InputStream inputStream)
            throws RVMException {
        LogicRepositoryType xmlData;
        InputStream parserInput = inputStream;

        JAXBContext logicRequest;
        try {
            logicRequest = JAXBContext
                    .newInstance("com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax");
            Unmarshaller unmarshaller = logicRequest.createUnmarshaller();
            xmlData = ((JAXBElement<LogicRepositoryType>) unmarshaller
                    .unmarshal(parserInput)).getValue();
        } catch (Exception e) {
            throw new RVMException(e.getMessage(), e);
        }

        return xmlData;
    }

}
