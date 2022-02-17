package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import java.io.ByteArrayInputStream;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.*;
import com.runtimeverification.rvmonitor.logicrepository.plugins.*;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.parser.CFGParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.*;

public class CFGPlugin extends LogicPlugin {

   public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
      String logicStr = logicInputXML.getProperty().getFormula();
      String eventsStr = logicInputXML.getEvents();
      eventsStr.replaceAll("\\s+", " ");

      CFGParser cfgParser = CFGParser.parse(new ByteArrayInputStream(logicStr.getBytes()));
      CFG g = cfgParser.getCFG();
      g.simplify();

      LogicRepositoryType logicOutputXML = logicInputXML;
      logicOutputXML.getMessage().add("done");
      if (logicInputXML.getCategories().matches(".*match.*") && !(logicInputXML.getCategories().matches(".*fail.*"))) {
         logicOutputXML.setCreationEvents(g.creationEvents());
         logicOutputXML.setEnableSets(g.enablesString());
      } else { // Use an approximation for fail
         logicOutputXML.setCreationEvents(logicInputXML.getEvents());
         logicOutputXML.setEnableSets(g.failenables());
      }

      return logicOutputXML;
   }
}
