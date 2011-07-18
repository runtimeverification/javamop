package logicrepository.plugins.ptltl;

import logicrepository.plugins.ptltl.*;
import logicrepository.*; // ~/FSL/projects/MOP/trunk/lib/logicrepository.jar;
import logicrepository.LogicException.*;
import logicrepository.Main.*;
import logicrepository.parser.logicrepositorysyntax.*;
import logicrepository.plugins.*;

public class PTLTLPlugin extends LogicPlugin {

  public LogicRepositoryType process(LogicRepositoryType logicInputXML) {
    String logicStr = logicInputXML.getProperty().getFormula().trim();

    LogicRepositoryType ret = logicInputXML;
    ret.getProperty().setLogic("fsm");
	//System.out.println(PTLTL.mkFSM(logicStr));
    ret.getProperty().setFormula(logicrepository.plugins.ptltl.PTLTL.mkFSM(logicStr));

    return ret;
  }

}

