package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.*;

public class PTLTLPlugin extends LogicPlugin {

  public LogicRepositoryType process(LogicRepositoryType logicInputXML) {
    String logicStr = logicInputXML.getProperty().getFormula().trim();

    LogicRepositoryType ret = logicInputXML;
    ret.getProperty().setLogic("fsm");
	//System.out.println(PTLTL.mkFSM(logicStr));
    ret.getProperty().setFormula(com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.PTLTL.mkFSM(logicStr));

    return ret;
  }

}

