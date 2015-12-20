// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javamop.util.MOPException;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.Property;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;
import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.ExtendedSpec;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.HandlerExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.astex.mopspec.PropertyExt;
import javamop.parser.astex.mopspec.ReferenceSpec;
import javamop.parser.astex.visitor.CollectEventPointCutVisitor;
import javamop.parser.astex.visitor.CollectHandlerPointCutVisitor;
import javamop.parser.astex.visitor.RenameVariableVisitor;
import javamop.parser.astex.visitor.ReplacePointCutVisitor;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.util.Pair;

class SpecContext {
	JavaMOPSpecExt spec;
	MOPSpecFileExt currentFile;
	HashMap<String, MOPSpecFileExt> depFiles;

	public SpecContext(JavaMOPSpecExt spec, MOPSpecFileExt currentFile, HashMap<String, MOPSpecFileExt> depFiles) {
		this.spec = spec;
		this.currentFile = currentFile;
		this.depFiles = depFiles;
	}
}

class JavaMOPExtender {
	public static MOPSpecFile translateMopSpecFile(MOPSpecFileExt currentFile) throws MOPException {
		HashMap<String, MOPSpecFileExt> depFiles = new HashMap<String, MOPSpecFileExt>();

		// retrieve all extended parent specification files
		for (JavaMOPSpecExt spec : currentFile.getSpecs()) {
			HashMap<String, MOPSpecFileExt> temp = retrieveParentFiles(spec, currentFile);
			depFiles.putAll(temp);
		}

		// collect all imports
		for (MOPSpecFileExt specFile : depFiles.values()) {
			currentFile.getImports().addAll(specFile.getImports());
		}

		// extend all specifications that the given file contains
		List<JavaMOPSpec> specList = new ArrayList<JavaMOPSpec>();
		for (JavaMOPSpecExt spec : currentFile.getSpecs()) {
			JavaMOPSpec spec2 = translateJavaMopSpec(spec, currentFile, depFiles);
			specList.add(spec2);
		}

		// return as an original AST
		return new MOPSpecFile(currentFile.getBeginLine(), currentFile.getBeginColumn(), currentFile.getPakage(),
				currentFile.getImports(), specList);
	}

	protected static JavaMOPSpec translateJavaMopSpec(JavaMOPSpecExt spec, MOPSpecFileExt currentFile, HashMap<String, MOPSpecFileExt> depFiles)
			throws MOPException {
		List<BodyDeclaration> declarations;
		List<EventDefinition> events;
		List<PropertyAndHandlers> props = new ArrayList<PropertyAndHandlers>();

		// collect all monitor variable declarations
		declarations = collectDeclarations(spec, currentFile, depFiles);

		// Check if all abstract events are implemented.
		List<EventDefinitionExt> nonImpAbsEvents = collectNonImpAbstractEvents(spec, currentFile, depFiles);
		if (nonImpAbsEvents.size() != 0) {
			String nonImpAbsEventsStr = "";

			for (EventDefinitionExt event : nonImpAbsEvents) {
				if (nonImpAbsEventsStr.length() != 0)
					nonImpAbsEventsStr += ", ";
				nonImpAbsEventsStr += event.getId();
			}

			throw new MOPException("The following abstract events are not implemented: " + nonImpAbsEventsStr);
		}

		// collect and translate event definitions
		events = collectAndTranslateEvents(new SpecContext(spec, currentFile, depFiles));

		// collect and translate properties and handlers
		props = collectAndTranslateProps(new SpecContext(spec, currentFile, depFiles));

		JavaMOPSpec ret;
		try {
			ret = new JavaMOPSpec(currentFile.getPakage(), spec.getBeginLine(), spec.getBeginColumn(), spec.getModifiers(), spec.getName(), spec.getParameters().toList(),
					spec.getInMethod(), declarations, events, props).setRawLogic(spec.getRawLogic());
		} catch (Exception e) {
			throw new MOPException(e.getMessage());
		}

		return ret;
	}

	private static List<EventDefinition> collectAndTranslateEvents(SpecContext context) throws MOPException {
		List<EventDefinition> ret = new ArrayList<EventDefinition>();

		if (context.spec.hasExtend()) {
			for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
				Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), context.currentFile, context.depFiles);

				JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
				MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

				List<EventDefinition> eventsFromParents = collectAndTranslateEvents(new SpecContext(parentSpec, parentSpecFile, context.depFiles));

				ret.addAll(eventsFromParents);
			}
		}

		for (EventDefinitionExt event : context.spec.getEvents()) {
			if (event.isAbstract())
				continue;

			EventDefinition translatedEvent = translateEvent(event, context);
			ret.add(translatedEvent);
		}

		return ret;
	}

	private static List<PropertyAndHandlers> collectAndTranslateProps(SpecContext context) throws MOPException {
		List<PropertyAndHandlers> ret = new ArrayList<PropertyAndHandlers>();

		// collect props
		HashMap<PropertyExt, HashMap<String, HandlerExt>> propAndHandlers = collectProps(context);

		// translate props
		for (PropertyExt prop : propAndHandlers.keySet()) {
			FormulaExt f = (FormulaExt) prop;
			HashMap<String, HandlerExt> handlers = propAndHandlers.get(prop);

			Property translatedProp = new Formula(prop.getBeginLine(), prop.getBeginColumn(), prop.getType(), f.getFormula());
			HashMap<String, BlockStmt> translatedHandlers = new HashMap<String, BlockStmt>();
			for (String state : handlers.keySet()) {
				HandlerExt handler = handlers.get(state);

				translatedHandlers.put(state, handler.getBlockStmt());
			}

			PropertyAndHandlers translatedPropAndHandlers = new PropertyAndHandlers(prop.getBeginLine(), prop.getBeginColumn(), translatedProp,
					translatedHandlers);

			ret.add(translatedPropAndHandlers);
		}

		return ret;
	}

	private static HashMap<PropertyExt, HashMap<String, HandlerExt>> collectProps(SpecContext context) throws MOPException {
		HashMap<PropertyExt, HashMap<String, HandlerExt>> ret = new HashMap<PropertyExt, HashMap<String, HandlerExt>>();

		// collect properties and handlers from parents first
		if (context.spec.hasExtend()) {
			for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
				Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), context.currentFile, context.depFiles);

				JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
				MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

				HashMap<PropertyExt, HashMap<String, HandlerExt>> propsFromParents = collectProps(new SpecContext(parentSpec, parentSpecFile,
						context.depFiles));

				checkDuplicatePropNames(ret.keySet(), propsFromParents.keySet());
				ret.putAll(propsFromParents);
			}
		}

		Set<String> propNames = collectAllPropNames(ret.keySet());

		// add all properties of the given spec into the output map.
		for (PropertyAndHandlersExt pnh : context.spec.getPropertiesAndHandlers()) {
			PropertyExt prop = pnh.getProperty();
			if (prop != null) {
				if (propNames.contains(prop.getName())){
					throw new MOPException("Duplicated Property Name");
				}

				propNames.add(prop.getName());
				ret.put(prop, new HashMap<String, HandlerExt>());
			}
		}

		// add all handlers into the output map.
		for (PropertyAndHandlersExt pnh : context.spec.getPropertiesAndHandlers()) {
			for (HandlerExt handler : pnh.getHandlerList()) {
				ReferenceSpec r = handler.getReferenceSpec();

				if (r.getSpecName() == null && r.getReferenceElement() == null && pnh.getProperty() != null) {
					HashMap<String, HandlerExt> handlers = ret.get(pnh.getProperty());
					handlers.put(handler.getState(), handler);
					continue;
				}

				Pair<PropertyAndHandlersExt, SpecContext> propExtPair = getReferencedProp(r, context);
				if (propExtPair == null)
					throw new MOPException("cannot find the associated property for a handler.");

				PropertyAndHandlersExt pnh2 = propExtPair.getLeft();

				HashMap<String, HandlerExt> handlers = ret.get(pnh2.getProperty());
				handlers.put(handler.getState(), handler);
			}
		}

		return ret;
	}

	protected static EventDefinition translateEvent(EventDefinitionExt event, SpecContext context) throws MOPException {
		EventDefinition ret;
		String pointCutStr;
		PointCut pointCut = event.getPointCut();

		pointCut = resolveEventPointCuts(pointCut, event, context);

		pointCut = resolveHandlerPointCuts(pointCut, event, context);

		pointCutStr = pointCut.toString();
		try {
			ret = new EventDefinition(event.getBeginLine(), event.getBeginColumn(), event.getId(), event.getRetType(), event.getPos(), event
					.getParameters().toList(), pointCutStr, event.getBlock(), event.getHasRetruning(), event.getRetVal().toList(),
					event.getHasThrowing(), event.getThrowVal().toList(), event.isStartEvent(), event.isCreationEvent(), event.isBlockingEvent()
			        , event.isStaticEvent());
		} catch (Exception e) {
			throw new MOPException(e.getMessage());
		}
		return ret;
	}

	private static PointCut resolveEventPointCuts(PointCut pointCut, EventDefinitionExt event, SpecContext context) throws MOPException {
		// collect all event pointcuts
		List<EventPointCut> eventPointCuts = pointCut.accept(new CollectEventPointCutVisitor(), null);

		// if there is no event pointcut, return the original pointcut
		if (eventPointCuts == null || eventPointCuts.size() == 0)
			return pointCut;

		HashMap<PointCut, PointCut> pointcutReplaceTable = new HashMap<PointCut, PointCut>();

		for (EventPointCut eventPointCut : eventPointCuts) {
			HashMap<String, MOPParameter> varRenameTable = new HashMap<String, MOPParameter>();

			MOPParameters params = getParametersFromStringList(eventPointCut.getParameters(), event.getMOPParameters());

			// get the referenced event and translate it
			Pair<EventDefinitionExt, SpecContext> targetEventExtPair = getReferencedEvent(eventPointCut.getReferenceSpec(), event.getPos(), params,
					context);
			if (targetEventExtPair == null)
				throw new MOPException("cannot find the event " + eventPointCut.getReferenceSpec().getReferenceElement()
						+ " referred by an event pointcut.");
			EventDefinition targetEvent = translateEvent(targetEventExtPair.getLeft(), targetEventExtPair.getRight());

			// prepare renaming table
			for (int i = 0; i < params.size(); i++) {
				MOPParameter origVar = targetEvent.getMOPParameters().get(i);
				MOPParameter newVar = params.get(i);

				varRenameTable.put(origVar.getName(), newVar);
			}

			// rename variables in the target pointcut
			PointCut targetPointCut = targetEvent.getPointCut();
			targetPointCut = (PointCut) targetPointCut.accept(new RenameVariableVisitor(), varRenameTable);

			// register the target pointcut to the pointcut replaceTable
			pointcutReplaceTable.put(eventPointCut, targetPointCut);
		}

		// replace event pointcuts with corresponding pointcuts.
		pointCut = pointCut.accept(new ReplacePointCutVisitor(), pointcutReplaceTable);

		return pointCut;
	}

	private static PointCut resolveHandlerPointCuts(PointCut pointCut, EventDefinitionExt event, SpecContext context) throws MOPException {
		// collect all handler pointcuts
		List<HandlerPointCut> handlerPointCuts = pointCut.accept(new CollectHandlerPointCutVisitor(), null);

		// if there is no handler pointcut, return the original pointcut
		if (handlerPointCuts == null || handlerPointCuts.size() == 0)
			return pointCut;

		HashMap<PointCut, PointCut> pointcutReplaceTable = new HashMap<PointCut, PointCut>();

		for (HandlerPointCut handlerPointCut : handlerPointCuts) {
			// get the referenced property
			Pair<PropertyAndHandlersExt, SpecContext> targetPropExtPair = getReferencedProp(handlerPointCut.getReferenceSpec(), context);
			if (targetPropExtPair == null) {
				throw new MOPException("cannot find the category " + handlerPointCut.getReferenceSpec().getReferenceElement()
						+ " referred by an handler pointcut.");
			}
			PropertyAndHandlersExt pnh = targetPropExtPair.getLeft();
			JavaMOPSpecExt spec = targetPropExtPair.getRight().spec;

			// parse the method pointcut for the handler method
			String methodPatternStr = "execution(void " + spec.getName() + "." + "Prop_" + pnh.getPropertyId() + "_handler_"
					+ handlerPointCut.getState() + "(..))";
			PointCut targetPointCut;
			try {
				targetPointCut = javamop.parser.aspectj_parser.AspectJParser.parse(new ByteArrayInputStream(methodPatternStr.getBytes()));
			} catch (Exception e) {
				throw new MOPException("A JavaMOP internal method pattern for a handler pointcut cannot be parsed.\n" + e.getMessage());
			}

			// register the target pointcut to the pointcut replaceTable
			pointcutReplaceTable.put(handlerPointCut, targetPointCut);
		}

		// replace event pointcuts with corresponding pointcuts.
		pointCut = pointCut.accept(new ReplacePointCutVisitor(), pointcutReplaceTable);

		return pointCut;
	}

	private static int numProperties(JavaMOPSpecExt spec) {
		int numProps = 0;

		for (PropertyAndHandlersExt pnh : spec.getPropertiesAndHandlers()) {
			if (pnh.getProperty() != null)
				numProps++;
		}
		return numProps;
	}

	private static MOPParameters getParametersFromStringList(List<String> list, MOPParameters parameters) throws MOPException {
		MOPParameters ret = new MOPParameters();

		for (String paramName : list) {
			MOPParameter param = parameters.getParam(paramName);
			if (param != null)
				ret.add(param);
			else
				throw new MOPException("An event pointcut is using unknown parameter.");
		}
		return ret;
	}

	private static HashMap<String, MOPSpecFileExt> retrieveParentFiles(JavaMOPSpecExt spec, MOPSpecFileExt currentFile) throws MOPException {
		HashMap<String, MOPSpecFileExt> ret = new HashMap<String, MOPSpecFileExt>();

		// if the spec has no parent, nothing to do.
		if (!spec.hasExtend())
			return ret;

		for (ExtendedSpec extSpec : spec.getExtendedSpec()) {
			// if the spec is in the same file, skip
			if (findJavaMOPSpec(extSpec.getName(), currentFile) != null)
				continue;

			File parentFile = new File(extSpec.getName() + ".mop");
			MOPSpecFileExt parentSpecFile;
			if (!parentFile.exists())
				throw new MOPException("cannot find the specification: " + extSpec.getName() + ".");
			try {
				parentSpecFile = JavaMOPParser.parse(parentFile);
			} catch (Exception e) {
				throw new MOPException("Error when parsing a specification file:\n" + e.getMessage());
			}
			if (parentSpecFile.getSpec(extSpec.getName()) == null)
				throw new MOPException("cannot find the specification: " + extSpec.getName() + ".");
			if (!parentSpecFile.getSpec(extSpec.getName()).isPublic())
				throw new MOPException("the specification " + extSpec.getName() + " is not public.");

			ret.put(extSpec.getName(), parentSpecFile);

			for (JavaMOPSpecExt spec2 : parentSpecFile.getSpecs()) {
				HashMap<String, MOPSpecFileExt> temp = retrieveParentFiles(spec2, parentSpecFile);
				ret.putAll(temp);
			}
		}

		return ret;
	}

	private static JavaMOPSpecExt findJavaMOPSpec(String name, MOPSpecFileExt currentFile) {
		for (JavaMOPSpecExt spec : currentFile.getSpecs()) {
			if (spec.getName().equals(name))
				return spec;
		}
		return null;
	}

	private static Pair<JavaMOPSpecExt, MOPSpecFileExt> findJavaMOPSpec(String name, MOPSpecFileExt currentFile,
			HashMap<String, MOPSpecFileExt> depFiles) throws MOPException {
		JavaMOPSpecExt parentSpec = findJavaMOPSpec(name, currentFile);

		if (parentSpec != null)
			return new Pair<JavaMOPSpecExt, MOPSpecFileExt>(parentSpec, currentFile);

		MOPSpecFileExt specFile = depFiles.get(name);
		if (specFile != null) {
			parentSpec = findJavaMOPSpec(name, specFile);

			if (parentSpec != null)
				return new Pair<JavaMOPSpecExt, MOPSpecFileExt>(parentSpec, specFile);
		}

		throw new MOPException("cannot find a parent specification: " + name);
	}

	private static List<BodyDeclaration> collectDeclarations(JavaMOPSpecExt spec, MOPSpecFileExt currentFile, HashMap<String, MOPSpecFileExt> depFiles)
			throws MOPException {
		List<BodyDeclaration> ret;

		if (!spec.hasExtend())
			return spec.getDeclarations();

		ret = new ArrayList<BodyDeclaration>();

		for (ExtendedSpec parentSpecName : spec.getExtendedSpec()) {
			Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), currentFile, depFiles);

			JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
			MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

			List<BodyDeclaration> declOfParents = collectDeclarations(parentSpec, parentSpecFile, depFiles);

			ret.addAll(declOfParents);
		}

		ret.addAll(spec.getDeclarations());

		return ret;
	}

	private static List<EventDefinitionExt> collectNonImpAbstractEvents(JavaMOPSpecExt spec, MOPSpecFileExt currentFile,
			HashMap<String, MOPSpecFileExt> depFiles) throws MOPException {
		List<EventDefinitionExt> ret = new ArrayList<EventDefinitionExt>();

		for (EventDefinitionExt event : spec.getEvents()) {
			if (event.isAbstract()) {
				ret.add(event);
			}
		}

		if (!spec.hasExtend())
			return ret;

		for (ExtendedSpec parentSpecName : spec.getExtendedSpec()) {
			Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), currentFile, depFiles);

			JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
			MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

			List<EventDefinitionExt> nonImpAbsEventsFromParents = collectNonImpAbstractEvents(parentSpec, parentSpecFile, depFiles);

			for (EventDefinitionExt absEvent : nonImpAbsEventsFromParents) {
				boolean isImplemented = false;

				for (EventDefinitionExt event : spec.getEvents()) {
					if (event.isImplementing(absEvent)) {
						isImplemented = true;
						break;
					}
				}

				if (!isImplemented) {
					ret.add(absEvent);
				}
			}
		}

		return ret;
	}

	private static Pair<EventDefinitionExt, SpecContext> getReferencedEvent(ReferenceSpec ref, String pos, MOPParameters params, SpecContext context)
			throws MOPException {
		// search in the same spec
		if (ref.getSpecName() == null || ref.getSpecName().equals(context.spec.getName())) {
			for (EventDefinitionExt specEvent : context.spec.getEvents()) {
				if (specEvent.getId().equals(ref.getReferenceElement()) && specEvent.getPos().equals(pos)
						&& params.matchTypes(specEvent.getMOPParameters())) {
					return new Pair<EventDefinitionExt, SpecContext>(specEvent, context);
				}
			}
		}

		if (!context.spec.hasExtend())
			return null;

		// search in the parent specs
		for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
			Pair<EventDefinitionExt, SpecContext> ret;

			Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), context.currentFile, context.depFiles);

			JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
			MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

			ret = getReferencedEvent(ref, pos, params, new SpecContext(parentSpec, parentSpecFile, context.depFiles));

			if (ret != null)
				return ret;
		}

		return null;
	}

	private static Pair<PropertyAndHandlersExt, SpecContext> getReferencedProp(ReferenceSpec ref, SpecContext context) throws MOPException {
		// search in the same spec
		if (ref.getSpecName() == null || ref.getSpecName().equals(context.spec.getName())) {
			if (ref.getReferenceElement() == null) {
				// when there are more than one property in the file, throw an exception
				if (numProperties(context.spec) != 1)
					throw new MOPException("Cannot find a referenced property");

				// the first non null property is the one
				for (PropertyAndHandlersExt pnh : context.spec.getPropertiesAndHandlers()) {
					if (pnh.getProperty() != null) {
						return new Pair<PropertyAndHandlersExt, SpecContext>(pnh, context);
					}
				}
			} else {
				for (PropertyAndHandlersExt pnh : context.spec.getPropertiesAndHandlers()) {
					if (pnh.getProperty() != null && pnh.getProperty().getName().equals(ref.getReferenceElement())) {
						return new Pair<PropertyAndHandlersExt, SpecContext>(pnh, context);
					}
				}
			}
		}

		if (!context.spec.hasExtend())
			return null;

		// search in the parent specs
		for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
			Pair<PropertyAndHandlersExt, SpecContext> ret;

			Pair<JavaMOPSpecExt, MOPSpecFileExt> parentSpecPair = findJavaMOPSpec(parentSpecName.getName(), context.currentFile, context.depFiles);

			JavaMOPSpecExt parentSpec = parentSpecPair.getLeft();
			MOPSpecFileExt parentSpecFile = parentSpecPair.getRight();

			ret = getReferencedProp(ref, new SpecContext(parentSpec, parentSpecFile, context.depFiles));

			if (ret != null)
				return ret;
		}

		return null;
	}

	private static Set<String> collectAllPropNames(Set<PropertyExt> props) throws MOPException {
		Set<String> ret = new HashSet<String>();

		for (PropertyExt prop : props) {
			if (ret.contains(prop.getName()))
				throw new MOPException("Duplicated Property Name");

			ret.add(prop.getName());
		}

		return ret;
	}

	private static void checkDuplicatePropNames(Set<PropertyExt> props1, Set<PropertyExt> props2) throws MOPException {
		Set<String> names1 = collectAllPropNames(props1);
		Set<String> names2 = collectAllPropNames(props2);

		for (String s : names2) {
			if (names1.contains(s)) {
				throw new MOPException("Duplicate Property Names");
			}
		}
	}
}
