/**
 * @author Feng Chen, Dongyun Jin
 * The class handling the mop specification tree
 */

package javamop;

import java.util.List;

import javamop.logicclient.LogicRepositoryConnector;
import javamop.logicpluginshells.LogicPluginShellFactory;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.output.AspectJCode;
import javamop.output.JavaLibCode;
import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.visitor.CollectUserVarVisitor;
import javamop.parser.logicrepositorysyntax.LogicRepositoryType;
import javamop.util.Tool;
import javamop.Main;

public class MOPProcessor {
	public static boolean verbose = false;

	public String name;

	public MOPProcessor(String name) {
		this.name = name;
	}

	private void registerUserVar(JavaMOPSpec mopSpec) throws MOPException {
		for (EventDefinition event : mopSpec.getEvents()) {
			MOPNameSpace.addUserVariable(event.getId());
			for(MOPParameter param : event.getMOPParameters()){
				MOPNameSpace.addUserVariable(param.getName());
			}
		}
		for (MOPParameter param : mopSpec.getParameters()) {
			MOPNameSpace.addUserVariable(param.getName());
		}
		MOPNameSpace.addUserVariable(mopSpec.getName());
		for (BodyDeclaration bd : mopSpec.getDeclarations()) {
			List<String> vars = bd.accept(new CollectUserVarVisitor(), null);

			if (vars != null)
				MOPNameSpace.addUserVariables(vars);
		}
	}
	
	public String translate2RV(MOPSpecFile mopSpecFile) throws MOPException {
		String rvresult = "";
		if (mopSpecFile.getPakage() != null) {
			rvresult += mopSpecFile.getPakage().toString();
		}
		if (mopSpecFile.getImports() != null) {
			for (ImportDeclaration id : mopSpecFile.getImports()) {
				rvresult += id.toString();
			}
		}
		for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs()){
			if(Main.translate2RV) {
				rvresult += mopSpec.getRVSpec().toString();
			}
		}
		rvresult = Tool.changeIndentation(rvresult, "", "\t");
		return rvresult;
	}

	public String process(MOPSpecFile mopSpecFile) throws MOPException {
		String result;
		
		// register all user variables to MOPNameSpace to avoid conflicts
		for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs())
			registerUserVar(mopSpec);

		// Connect to Logic Repository
		for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs()){
			
			for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
				// connect to the logic repository and get the logic output
				LogicRepositoryType logicOutput = LogicRepositoryConnector.process(mopSpec, prop);
				// get the monitor from the logic shell
				LogicPluginShellResult logicShellOutput = LogicPluginShellFactory.process(logicOutput, mopSpec.getEventStr());
				prop.setLogicShellOutput(logicShellOutput);
				
				if(logicOutput.getMessage().contains("versioned stack")){
					prop.setVersionedStack();
				}

				if (verbose) {
					System.out.println("== result from logic shell ==");
					System.out.print(logicShellOutput);
					System.out.println("");
				}
			}
		}

		// Error Checker
		for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs()){
			MOPErrorChecker.verify(mopSpec);
		}

		// Generate output code
		
		if (Main.translate2RV) {
			result = (new AspectJCode(name, mopSpecFile)).toRVString();
		} else {
			if (Main.toJavaLib)
				result = (new JavaLibCode(name, mopSpecFile)).toString();
			else
				result = (new AspectJCode(name, mopSpecFile)).toString();
		}

		// Do indentation
		result = Tool.changeIndentation(result, "", "\t");

		return result;
	}


}
