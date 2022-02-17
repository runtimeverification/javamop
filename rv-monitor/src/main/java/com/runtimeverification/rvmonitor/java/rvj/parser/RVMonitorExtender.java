package com.runtimeverification.rvmonitor.java.rvj.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.JavaParserAdapter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.Formula;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.Property;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.RVMSpecFileExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.EventDefinitionExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ExtendedSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.FormulaExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.HandlerExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyAndHandlersExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.RVMonitorSpecExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ReferenceSpec;
import com.runtimeverification.rvmonitor.util.Pair;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.Tool;

class SpecContext {
    RVMonitorSpecExt spec;
    RVMSpecFileExt currentFile;
    HashMap<String, RVMSpecFileExt> depFiles;

    public SpecContext(RVMonitorSpecExt spec, RVMSpecFileExt currentFile,
            HashMap<String, RVMSpecFileExt> depFiles) {
        this.spec = spec;
        this.currentFile = currentFile;
        this.depFiles = depFiles;
    }
}

public class RVMonitorExtender {
    public static RVMSpecFile translateExtendedSpecFile(
            RVMSpecFileExt currentFile) throws RVMException {
        HashMap<String, RVMSpecFileExt> depFiles = new HashMap<String, RVMSpecFileExt>();

        // retrieve all extended parent specification files
        for (RVMonitorSpecExt spec : currentFile.getSpecs()) {
            HashMap<String, RVMSpecFileExt> temp = retrieveParentFiles(spec,
                    currentFile);
            depFiles.putAll(temp);
        }

        // collect all imports
        for (RVMSpecFileExt specFile : depFiles.values()) {
            currentFile.getImports().addAll(specFile.getImports());
        }

        // extend all specifications that the given file contains
        List<RVMonitorSpec> specList = new ArrayList<RVMonitorSpec>();
        for (RVMonitorSpecExt spec : currentFile.getSpecs()) {
            RVMonitorSpec spec2 = translateExtendedSpec(spec, currentFile,
                    depFiles);
            specList.add(spec2);
        }

        // return as an original AST
        return new RVMSpecFile(currentFile.getBeginLine(),
                currentFile.getBeginColumn(), currentFile.getPakage(),
                currentFile.getImports(), specList);
    }

    protected static RVMonitorSpec translateExtendedSpec(RVMonitorSpecExt spec,
            RVMSpecFileExt currentFile, HashMap<String, RVMSpecFileExt> depFiles)
                    throws RVMException {
        String declarations;
        List<EventDefinition> events;
        List<PropertyAndHandlers> props = new ArrayList<PropertyAndHandlers>();

        // collect all monitor variable declarations
        declarations = collectDeclarations(spec, currentFile, depFiles);

        // Check if all abstract events are implemented.
        List<EventDefinitionExt> nonImpAbsEvents = collectNonImpAbstractEvents(
                spec, currentFile, depFiles);
        if (nonImpAbsEvents.size() != 0) {
            String nonImpAbsEventsStr = "";

            for (EventDefinitionExt event : nonImpAbsEvents) {
                if (nonImpAbsEventsStr.length() != 0)
                    nonImpAbsEventsStr += ", ";
                nonImpAbsEventsStr += event.getId();
            }

            throw new RVMException(
                    "The following abstract events are not implemented: "
                            + nonImpAbsEventsStr);
        }

        // collect and translate event definitions
        events = collectAndTranslateEvents(new SpecContext(spec, currentFile,
                depFiles));

        // collect and translate properties and handlers
        props = collectAndTranslateProps(new SpecContext(spec, currentFile,
                depFiles));

        RVMonitorSpec ret;
        try {
            ret = new RVMonitorSpec(currentFile.getPakage(),
                    spec.getBeginLine(), spec.getBeginColumn(),
                    spec.getModifiers(), spec.getName(), spec.getParameters()
                    .toList(), spec.getInMethod(), declarations,
                    events, props);
        } catch (Exception e) {
            throw new RVMException(e.getMessage());
        }

        return ret;
    }

    private static List<EventDefinition> collectAndTranslateEvents(
            SpecContext context) throws RVMException {
        List<EventDefinition> ret = new ArrayList<EventDefinition>();

        if (context.spec.hasExtend()) {
            for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
                Pair<RVMonitorSpecExt, RVMSpecFileExt> parentSpecPair = findRVMonitorSpec(
                        parentSpecName.getName(), context.currentFile,
                        context.depFiles);

                RVMonitorSpecExt parentSpec = parentSpecPair.getLeft();
                RVMSpecFileExt parentSpecFile = parentSpecPair.getRight();

                List<EventDefinition> eventsFromParents = collectAndTranslateEvents(new SpecContext(
                        parentSpec, parentSpecFile, context.depFiles));

                ret.addAll(eventsFromParents);
            }
        }

        for (EventDefinitionExt event : context.spec.getEvents()) {

            EventDefinition translatedEvent = translateEvent(event, context);
            ret.add(translatedEvent);
        }

        return ret;
    }

    private static List<PropertyAndHandlers> collectAndTranslateProps(
            SpecContext context) throws RVMException {
        List<PropertyAndHandlers> ret = new ArrayList<PropertyAndHandlers>();

        // collect props
        HashMap<PropertyExt, HashMap<String, HandlerExt>> propAndHandlers = collectProps(context);

        // translate props
        for (PropertyExt prop : propAndHandlers.keySet()) {
            FormulaExt f = (FormulaExt) prop;
            HashMap<String, HandlerExt> handlers = propAndHandlers.get(prop);

            Property translatedProp = new Formula(prop.getBeginLine(),
                    prop.getBeginColumn(), prop.getType(), f.getFormula());
            HashMap<String, String> translatedHandlers = new HashMap<String, String>();
            for (String state : handlers.keySet()) {
                HandlerExt handler = handlers.get(state);

                translatedHandlers.put(state, handler.getBlockStmt());
            }

            PropertyAndHandlers translatedPropAndHandlers = new PropertyAndHandlers(
                    prop.getBeginLine(), prop.getBeginColumn(), translatedProp,
                    translatedHandlers);

            ret.add(translatedPropAndHandlers);
        }

        return ret;
    }

    private static HashMap<PropertyExt, HashMap<String, HandlerExt>> collectProps(
            SpecContext context) throws RVMException {
        HashMap<PropertyExt, HashMap<String, HandlerExt>> ret = new HashMap<PropertyExt, HashMap<String, HandlerExt>>();

        // collect properties and handlers from parents first
        if (context.spec.hasExtend()) {
            for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
                Pair<RVMonitorSpecExt, RVMSpecFileExt> parentSpecPair = findRVMonitorSpec(
                        parentSpecName.getName(), context.currentFile,
                        context.depFiles);

                RVMonitorSpecExt parentSpec = parentSpecPair.getLeft();
                RVMSpecFileExt parentSpecFile = parentSpecPair.getRight();

                HashMap<PropertyExt, HashMap<String, HandlerExt>> propsFromParents = collectProps(new SpecContext(
                        parentSpec, parentSpecFile, context.depFiles));

                checkDuplicatePropNames(ret.keySet(), propsFromParents.keySet());
                ret.putAll(propsFromParents);
            }
        }

        Set<String> propNames = collectAllPropNames(ret.keySet());

        // add all properties of the given spec into the output map.
        for (PropertyAndHandlersExt pnh : context.spec
                .getPropertiesAndHandlers()) {
            PropertyExt prop = pnh.getProperty();
            if (prop != null) {
                if (propNames.contains(prop.getName())) {
                    throw new RVMException("Duplicated Property Name");
                }

                propNames.add(prop.getName());
                ret.put(prop, new HashMap<String, HandlerExt>());
            }
        }

        // add all handlers into the output map.
        for (PropertyAndHandlersExt pnh : context.spec
                .getPropertiesAndHandlers()) {
            for (HandlerExt handler : pnh.getHandlerList()) {
                ReferenceSpec r = handler.getReferenceSpec();

                if (r.getSpecName() == null && r.getReferenceElement() == null
                        && pnh.getProperty() != null) {
                    HashMap<String, HandlerExt> handlers = ret.get(pnh
                            .getProperty());
                    handlers.put(handler.getState(), handler);
                    continue;
                }

                Pair<PropertyAndHandlersExt, SpecContext> propExtPair = getReferencedProp(
                        r, context);
                if (propExtPair == null)
                    throw new RVMException(
                            "cannot find the associated property for a handler.");

                PropertyAndHandlersExt pnh2 = propExtPair.getLeft();

                HashMap<String, HandlerExt> handlers = ret.get(pnh2
                        .getProperty());
                handlers.put(handler.getState(), handler);
            }
        }

        return ret;
    }

    protected static EventDefinition translateEvent(EventDefinitionExt event,
            SpecContext context) throws RVMException {
        EventDefinition ret;
        try {
            ret = new EventDefinition(event.getBeginLine(),
                    event.getBeginColumn(), event.getId(), event
                    .getParameters().toList(), event.getBlock(),
                    event.isStartEvent(), event.isBlockingEvent());
        } catch (Exception e) {
            throw new RVMException(e.getMessage());
        }
        return ret;
    }

    private static int numProperties(RVMonitorSpecExt spec) {
        int numProps = 0;

        for (PropertyAndHandlersExt pnh : spec.getPropertiesAndHandlers()) {
            if (pnh.getProperty() != null)
                numProps++;
        }
        return numProps;
    }

    private static HashMap<String, RVMSpecFileExt> retrieveParentFiles(
            RVMonitorSpecExt spec, RVMSpecFileExt currentFile)
                    throws RVMException {
        HashMap<String, RVMSpecFileExt> ret = new HashMap<String, RVMSpecFileExt>();

        // if the spec has no parent, nothing to do.
        if (!spec.hasExtend())
            return ret;

        for (ExtendedSpec extSpec : spec.getExtendedSpec()) {
            // if the spec is in the same file, skip
            if (findRVMonitorSpec(extSpec.getName(), currentFile) != null)
                continue;

            File parentFile = new File(extSpec.getName()
                    + Tool.getSpecFileDotExt());
            RVMSpecFileExt parentSpecFile;
            if (!parentFile.exists())
                throw new RVMException("cannot find the specification: "
                        + extSpec.getName() + ".");
            try {
                parentSpecFile = JavaParserAdapter.parse(parentFile);
            } catch (Exception e) {
                throw new RVMException(
                        "Error when parsing a specification file:\n"
                                + e.getMessage());
            }
            if (parentSpecFile.getSpec(extSpec.getName()) == null)
                throw new RVMException("cannot find the specification: "
                        + extSpec.getName() + ".");
            if (!parentSpecFile.getSpec(extSpec.getName()).isPublic())
                throw new RVMException("the specification " + extSpec.getName()
                        + " is not public.");

            ret.put(extSpec.getName(), parentSpecFile);

            for (RVMonitorSpecExt spec2 : parentSpecFile.getSpecs()) {
                HashMap<String, RVMSpecFileExt> temp = retrieveParentFiles(
                        spec2, parentSpecFile);
                ret.putAll(temp);
            }
        }

        return ret;
    }

    private static RVMonitorSpecExt findRVMonitorSpec(String name,
            RVMSpecFileExt currentFile) {
        for (RVMonitorSpecExt spec : currentFile.getSpecs()) {
            if (spec.getName().equals(name))
                return spec;
        }
        return null;
    }

    private static Pair<RVMonitorSpecExt, RVMSpecFileExt> findRVMonitorSpec(
            String name, RVMSpecFileExt currentFile,
            HashMap<String, RVMSpecFileExt> depFiles) throws RVMException {
        RVMonitorSpecExt parentSpec = findRVMonitorSpec(name, currentFile);

        if (parentSpec != null)
            return new Pair<RVMonitorSpecExt, RVMSpecFileExt>(parentSpec,
                    currentFile);

        RVMSpecFileExt specFile = depFiles.get(name);
        if (specFile != null) {
            parentSpec = findRVMonitorSpec(name, specFile);

            if (parentSpec != null)
                return new Pair<RVMonitorSpecExt, RVMSpecFileExt>(parentSpec,
                        specFile);
        }

        throw new RVMException("cannot find a parent specification: " + name);
    }

    private static String collectDeclarations(RVMonitorSpecExt spec,
            RVMSpecFileExt currentFile, HashMap<String, RVMSpecFileExt> depFiles)
                    throws RVMException {
        String ret;

        if (!spec.hasExtend())
            return spec.getDeclarationsStr();

        ret = "";

        for (ExtendedSpec parentSpecName : spec.getExtendedSpec()) {
            Pair<RVMonitorSpecExt, RVMSpecFileExt> parentSpecPair = findRVMonitorSpec(
                    parentSpecName.getName(), currentFile, depFiles);

            RVMonitorSpecExt parentSpec = parentSpecPair.getLeft();
            RVMSpecFileExt parentSpecFile = parentSpecPair.getRight();

            String declOfParents = collectDeclarations(parentSpec,
                    parentSpecFile, depFiles);

            ret += declOfParents + System.lineSeparator();
        }

        ret += spec.getDeclarationsStr();

        return ret;
    }

    private static List<EventDefinitionExt> collectNonImpAbstractEvents(
            RVMonitorSpecExt spec, RVMSpecFileExt currentFile,
            HashMap<String, RVMSpecFileExt> depFiles) throws RVMException {
        List<EventDefinitionExt> ret = new ArrayList<EventDefinitionExt>();

        if (!spec.hasExtend())
            return ret;

        for (ExtendedSpec parentSpecName : spec.getExtendedSpec()) {
            Pair<RVMonitorSpecExt, RVMSpecFileExt> parentSpecPair = findRVMonitorSpec(
                    parentSpecName.getName(), currentFile, depFiles);

            RVMonitorSpecExt parentSpec = parentSpecPair.getLeft();
            RVMSpecFileExt parentSpecFile = parentSpecPair.getRight();

            List<EventDefinitionExt> nonImpAbsEventsFromParents = collectNonImpAbstractEvents(
                    parentSpec, parentSpecFile, depFiles);

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

    private static Pair<PropertyAndHandlersExt, SpecContext> getReferencedProp(
            ReferenceSpec ref, SpecContext context) throws RVMException {
        // search in the same spec
        if (ref.getSpecName() == null
                || ref.getSpecName().equals(context.spec.getName())) {
            if (ref.getReferenceElement() == null) {
                // when there are more than one property in the file, throw an
                // exception
                if (numProperties(context.spec) != 1)
                    throw new RVMException("Cannot find a referenced property");

                // the first non null property is the one
                for (PropertyAndHandlersExt pnh : context.spec
                        .getPropertiesAndHandlers()) {
                    if (pnh.getProperty() != null) {
                        return new Pair<PropertyAndHandlersExt, SpecContext>(
                                pnh, context);
                    }
                }
            } else {
                for (PropertyAndHandlersExt pnh : context.spec
                        .getPropertiesAndHandlers()) {
                    if (pnh.getProperty() != null
                            && pnh.getProperty().getName()
                            .equals(ref.getReferenceElement())) {
                        return new Pair<PropertyAndHandlersExt, SpecContext>(
                                pnh, context);
                    }
                }
            }
        }

        if (!context.spec.hasExtend())
            return null;

        // search in the parent specs
        for (ExtendedSpec parentSpecName : context.spec.getExtendedSpec()) {
            Pair<PropertyAndHandlersExt, SpecContext> ret;

            Pair<RVMonitorSpecExt, RVMSpecFileExt> parentSpecPair = findRVMonitorSpec(
                    parentSpecName.getName(), context.currentFile,
                    context.depFiles);

            RVMonitorSpecExt parentSpec = parentSpecPair.getLeft();
            RVMSpecFileExt parentSpecFile = parentSpecPair.getRight();

            ret = getReferencedProp(ref, new SpecContext(parentSpec,
                    parentSpecFile, context.depFiles));

            if (ret != null)
                return ret;
        }

        return null;
    }

    private static Set<String> collectAllPropNames(Set<PropertyExt> props)
            throws RVMException {
        Set<String> ret = new HashSet<String>();

        for (PropertyExt prop : props) {
            if (ret.contains(prop.getName()))
                throw new RVMException("Duplicated Property Name");

            ret.add(prop.getName());
        }

        return ret;
    }

    private static void checkDuplicatePropNames(Set<PropertyExt> props1,
            Set<PropertyExt> props2) throws RVMException {
        Set<String> names1 = collectAllPropNames(props1);
        Set<String> names2 = collectAllPropNames(props2);

        for (String s : names2) {
            if (names1.contains(s)) {
                throw new RVMException("Duplicate Property Names");
            }
        }
    }
}
