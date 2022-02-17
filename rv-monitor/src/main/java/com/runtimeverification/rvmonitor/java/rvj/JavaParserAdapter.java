package com.runtimeverification.rvmonitor.java.rvj;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.runtimeverification.rvmonitor.core.ast.Event;
import com.runtimeverification.rvmonitor.core.ast.MonitorFile;
import com.runtimeverification.rvmonitor.core.ast.Property;
import com.runtimeverification.rvmonitor.core.ast.PropertyHandler;
import com.runtimeverification.rvmonitor.core.ast.Specification;
import com.runtimeverification.rvmonitor.core.parser.RVParser;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.ImportDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.PackageDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.SpecModifierSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.RVMSpecFileExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.EventDefinitionExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ExtendedSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.FormulaExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.HandlerExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyAndHandlersExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.RVMonitorSpecExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.ParseException;
import com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.RVMonitorParser;
import com.runtimeverification.rvmonitor.util.RVMException;

/**
 * A class with static methods to convert the language-independent syntax into
 * Java-specific RVMSpecFileExt objects.
 *
 * @author A. Cody Schuffelen
 */
public final class JavaParserAdapter {

    /**
     * Private constructor to prevent instantiation.
     */
    private JavaParserAdapter() {

    }

    /**
     * Produce a RVMSpecFileExt by reading a file through the
     * language-independent RVM parser.
     *
     * @param file
     *            The file to read from.
     * @return A Java-specific RVM specification object.
     */
    public static RVMSpecFileExt parse(File file) throws RVMException {
        try {
            final Reader source = new InputStreamReader(new FileInputStream(
                    file));
            final MonitorFile spec = RVParser.parse(source);
            return convert(spec);
        } catch (Exception e) {
            throw new RVMException(e);
        }
    }

    /**
     * Produce a RVMSpecFileExt by reading a string through the
     * language-independent RVM parser.
     *
     * @param str
     *            The string to read from.
     * @return A Java-specific RVM specification object.
     */
    public static RVMSpecFileExt parse(String str) throws RVMException {
        try {
            final Reader source = new StringReader(str);
            final MonitorFile spec = RVParser.parse(source);
            return convert(spec);
        } catch (Exception e) {
            throw new RVMException(e);
        }
    }

    /**
     * Convert a language-independent specification into one with Java-specific
     * information.
     *
     * @param spec
     *            The specification to convert.
     * @return The Java-specific specification.
     */
    private static RVMSpecFileExt convert(MonitorFile file)
            throws ParseException {
        final PackageDeclaration filePackage = getPackage(file.getPreamble());
        final List<ImportDeclaration> imports = getImports(file.getPreamble());
        final ArrayList<RVMonitorSpecExt> specs = new ArrayList<RVMonitorSpecExt>();
        for (Specification spec : file.getSpecifications()) {
            specs.add(convert(filePackage, spec));
        }
        return new RVMSpecFileExt(0, 0, filePackage, imports, specs);
    }

    private static RVMonitorParser parseJavaBubble(String bubble) {
        return new RVMonitorParser(new StringReader(bubble));
    }

    /**
     * Extract the package from the package statement in the preamble.
     *
     * @param preamble
     *            The beginning of the specification file.
     * @return The package the class should be in.
     */
    private static PackageDeclaration getPackage(String preamble) {
        try {
            return parseJavaBubble(preamble).PackageDeclaration();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    /**
     * Extract the imports from the import statements in the preamble.
     *
     * @param preamble
     *            The beginning of the specification file.
     * @return The package the class should be in.
     */
    private static List<ImportDeclaration> getImports(final String preamble) {
        final ArrayList<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        RVMonitorParser parser = parseJavaBubble(preamble);
        try {
            // Parse the package declaration if it is there.
            parser.PackageDeclaration();
        } catch (Exception e) {
            // Reset if it isn't.
            parser = parseJavaBubble(preamble);
        }
        // Parse any import declarations present.
        while (true) {
            try {
                imports.add(parser.ImportDeclaration());
            } catch (Exception e) {
                // e.printStackTrace();
                break;
            }
        }
        return imports;
    }

    /**
     * Convert a {@link Specification} into a {@link RVMonitorSpecExt}.
     *
     * @param pack
     *            The package declaration of the file the specification is in.
     * @param spec
     *            The specification to convert.
     * @return The Java-specific specification.
     */
    private static RVMonitorSpecExt convert(final PackageDeclaration pack,
            final Specification spec) throws ParseException {
        final List<String> modifierList = spec.getLanguageModifiers();
        final boolean isPublic = modifierList.contains("public");
        final int modifierBitfield = extractModifierBitfield(modifierList);
        final String name = spec.getName();
        final List<RVMParameter> parameters = convertParameters(spec
                .getLanguageParameters());
        final String inMethod = null;
        final List<ExtendedSpec> extensions = null;
        final String declarations = spec.getLanguageDeclarations();
        final List<EventDefinitionExt> events = new ArrayList<EventDefinitionExt>();
        for (Event e : spec.getEvents()) {
            events.add(convert(e));
        }
        final List<PropertyAndHandlersExt> properties = new ArrayList<PropertyAndHandlersExt>();
        int index = 0;
        for (Property property : spec.getProperties()) {
            properties.add(convert(index, property));
            index++;
        }
        return new RVMonitorSpecExt(pack, 0, 0, isPublic, modifierBitfield,
                name, parameters, inMethod, extensions, declarations, events,
                properties);
    }

    /**
     * Produce the integer bitfield representing the different Java-specific
     * specification modifiers.
     *
     * @param modifierList
     *            A list of modifiers.
     * @return A bitfield with the appropriate bits for each modifier set.
     */
    private static int extractModifierBitfield(final List<String> modifierList) {
        int modifierBitfield = 0;
        if (modifierList.contains("unsynchronized")) {
            modifierBitfield |= SpecModifierSet.UNSYNC;
        }
        if (modifierList.contains("decentralized")) {
            modifierBitfield |= SpecModifierSet.DECENTRL;
        }
        if (modifierList.contains("perthread")) {
            modifierBitfield |= SpecModifierSet.PERTHREAD;
        }
        if (modifierList.contains("suffix")) {
            modifierBitfield |= SpecModifierSet.SUFFIX;
        }
        if (modifierList.contains("full-binding")) {
            modifierBitfield |= SpecModifierSet.FULLBINDING;
        }
        if (modifierList.contains("avoid")) {
            modifierBitfield |= SpecModifierSet.AVOID;
        }
        if (modifierList.contains("enforce")) {
            modifierBitfield |= SpecModifierSet.ENFORCE;
        }
        if (modifierList.contains("connected")) {
            modifierBitfield |= SpecModifierSet.CONNECTED;
        }
        return modifierBitfield;
    }

    /**
     * Convert a specification parameter string into a parameter object.
     *
     * @param paramString
     *            The string with the specification parameters.
     * @return A list of Java specification parameter objects.
     */
    private static List<RVMParameter> convertParameters(final String paramString) {
        try {
            return parseJavaBubble(paramString).RVMParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a language-independent event into a Java event.
     *
     * @param event
     *            The language-independent event object.
     * @return A Java-specific event object.
     */
    private static EventDefinitionExt convert(final Event event) {
        final List<String> modifierList = event.getModifiers();
        final boolean startEvent = modifierList.contains("creation");
        final boolean blockingEvent = modifierList.contains("blocking");
        final String name = event.getName();
        List<RVMParameter> parameters = null;
        try {
            parameters = parseJavaBubble(event.getDefinition())
                    .AdviceParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String block = event.getAction();
        try {
            return new EventDefinitionExt(0, 0, name, parameters, block,
                    startEvent, blockingEvent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a language-independent property into a Java property object.
     *
     * @param index
     *            The index of this property in the specification.
     * @param property
     *            The property to convert.
     * @return A Java-specific property object.
     */
    private static PropertyAndHandlersExt convert(final int index,
            final Property property) {
        final String logicId = property.getName();
        final String propertyName = "defaultProp" + index;
        final String formula = property.getSyntax();
        final PropertyExt propertyExt = new FormulaExt(0, 0, logicId, formula,
                propertyName);

        final List<HandlerExt> handlerList = new ArrayList<HandlerExt>();
        final HashMap<String, String> handlerMap = new HashMap<String, String>();
        for (PropertyHandler handler : property.getHandlers()) {
            HandlerExt converted = convert(handler);
            handlerList.add(converted);
            handlerMap.put(handler.getState().toLowerCase(),
                    converted.getBlockStmt());
        }
        return new PropertyAndHandlersExt(0, 0, propertyExt, handlerMap,
                handlerList);
    }

    /**
     * Convert a language-independent handler into a Java handler object.
     *
     * @param handler
     *            The handler to convert.
     * @return A Java-specific handler object.
     */
    private static HandlerExt convert(final PropertyHandler handler) {
        final String id = handler.getState();
        final String propertyReference = null;
        final String specReference = null;
        final String action = handler.getAction();
        return new HandlerExt(0, 0, id, action, propertyReference,
                specReference);
    }
}
