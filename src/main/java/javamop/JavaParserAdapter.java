// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javamop.MOPException;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.PackageDeclaration;

import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.body.ModifierSet;

import javamop.parser.ast.expr.NameExpr;
import javamop.parser.ast.expr.QualifiedNameExpr;

import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.SpecModifierSet;

import javamop.parser.ast.stmt.BlockStmt;

import javamop.parser.astex.MOPSpecFileExt;

import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.ExtendedSpec;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.HandlerExt;
import javamop.parser.astex.mopspec.PropertyExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;

import javamop.parser.main_parser.ParseException;
import javamop.parser.main_parser.JavaMOPParser;

import com.runtimeverification.rvmonitor.core.ast.Event;
import com.runtimeverification.rvmonitor.core.ast.MonitorFile;
import com.runtimeverification.rvmonitor.core.ast.Property;
import com.runtimeverification.rvmonitor.core.ast.PropertyHandler;
import com.runtimeverification.rvmonitor.core.ast.Specification;

import com.runtimeverification.rvmonitor.core.parser.RVParser;

/**
 * A class with static methods to convert the language-independent syntax into Java-specific
 * MOPSpecFileExt objects.
 * @author A. Cody Schuffelen
 */
public final class JavaParserAdapter {

    /**
     * Private constructor to prevent instantiation.
     */
    private JavaParserAdapter() {

    }

    /**
     * Produce a MOPSpecFileExt by reading a file through the language-independent MOP parser.
     * @param file The file to read from.
     * @return A Java-specific MOP specification object.
     */
    public static MOPSpecFileExt parse(File file) throws MOPException {
        try {
            final Reader source = new InputStreamReader(new FileInputStream(file));
            final MonitorFile spec = RVParser.parse(source);
            return convert(spec);
        } catch(Exception e) {
            throw new MOPException(e);
        }
    }

    /**
     * Produce a MOPSpecFileExt by reading a string through the language-independent MOP parser.
     * @param str The string to read from.
     * @return A Java-specific MOP specification object.
     */
    public static MOPSpecFileExt parse(String str) throws MOPException {
        try {
            final Reader source = new StringReader(str);
            final MonitorFile spec = RVParser.parse(source);
            return convert(spec);
        } catch(Exception e) {
            throw new MOPException(e);
        }
    }

    /**
     * Convert a language-independent specification into one with Java-specific information.
     * @param file The specification to convert.
     * @return The Java-specific specification.
     */
    private static MOPSpecFileExt convert(MonitorFile file) throws ParseException {
        final PackageDeclaration filePackage = getPackage(file.getPreamble());
        final List<ImportDeclaration> imports = getImports(file.getPreamble());
        final ArrayList<JavaMOPSpecExt> specs = new ArrayList<JavaMOPSpecExt>();
        for(Specification spec : file.getSpecifications()) {
            specs.add(convert(filePackage, spec));
        }
        return new MOPSpecFileExt(0, 0, filePackage, imports, specs);
    }

    private static JavaMOPParser parseJavaBubble(String bubble) {
        return new JavaMOPParser(new StringReader(bubble));
    }

    /**
     * Extract the package from the package statement in the preamble.
     * @param preamble The beginning of the specification file.
     * @return The package the class should be in.
     */
    private static PackageDeclaration getPackage(String preamble) {
        try {
            return parseJavaBubble(preamble).PackageDeclaration();
        } catch(Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Extract the imports from the import statements in the preamble.
     * @param preamble The beginning of the specification file.
     * @return The package the class should be in.
     */
    private static List<ImportDeclaration> getImports(final String preamble) {
        final ArrayList<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        JavaMOPParser parser = parseJavaBubble(preamble);
        try {
            // Parse the package declaration if it is there.
            parser.PackageDeclaration();
        } catch(Exception e) {
            // Reset if it isn't.
            parser = parseJavaBubble(preamble);
        }
        // Parse any import declarations present.
        while(true) {
            try {
                imports.add(parser.ImportDeclaration());
            } catch(Exception e) {
                //e.printStackTrace();
                break;
            }
        }
        return imports;
    }

    /**
     * Convert a {@link Specification} into a {@link JavaMOPSpecExt}.
     * @param pack The package declaration of the file the specification is in.
     * @param spec The specification to convert.
     * @return The Java-specific specification.
     */
    private static JavaMOPSpecExt convert(final PackageDeclaration pack,
            final Specification spec) throws ParseException {
        final List<String> modifierList = spec.getLanguageModifiers();
        final boolean isPublic = modifierList.contains("public");
        final int modifierBitfield = extractModifierBitfield(modifierList);
        final String name = spec.getName();
        final List<MOPParameter> parameters = convertParameters(spec.getLanguageParameters());
        final String inMethod = null;
        final List<ExtendedSpec> extensions = null;
        final List<BodyDeclaration> declarations =
            convertDeclarations(spec.getLanguageDeclarations());
        final List<EventDefinitionExt> events = new ArrayList<EventDefinitionExt>();
        for(Event e : spec.getEvents()) {
            events.add(convert(e));
        }
        final List<PropertyAndHandlersExt> properties = new ArrayList<PropertyAndHandlersExt>();
        int index = 0;
        for(Property property : spec.getProperties()) {
            properties.add(convert(index, property));
            index++;
        }
        return new JavaMOPSpecExt(pack, 0, 0, isPublic, modifierBitfield, name, parameters,
            inMethod, extensions, declarations, events, properties);
    }

    /**
     * Produce the integer bitfield representing the different Java-specific specification
     * modifiers.
     * @param modifierList A list of modifiers.
     * @return A bitfield with the appropriate bits for each modifier set.
     */
    private static int extractModifierBitfield(final List<String> modifierList) {
        int modifierBitfield = 0;
        if(modifierList.contains("unsynchronized")) {
            modifierBitfield |= SpecModifierSet.UNSYNC;
        }
        if(modifierList.contains("decentralized")) {
            modifierBitfield |= SpecModifierSet.DECENTRL;
        }
        if(modifierList.contains("perthread")) {
            modifierBitfield |= SpecModifierSet.PERTHREAD;
        }
        if(modifierList.contains("suffix")) {
            modifierBitfield |= SpecModifierSet.SUFFIX;
        }
        if(modifierList.contains("full-binding")) {
            modifierBitfield |= SpecModifierSet.FULLBINDING;
        }
        if(modifierList.contains("avoid")) {
            modifierBitfield |= SpecModifierSet.AVOID;
        }
        if(modifierList.contains("enforce")) {
            modifierBitfield |= SpecModifierSet.ENFORCE;
        }
        if(modifierList.contains("connected")) {
            modifierBitfield |= SpecModifierSet.CONNECTED;
        }
        return modifierBitfield;
    }

    /**
     * Convert a specification parameter string into a parameter object.
     * @param paramString The string witht he specification parameters.
     * @return A list of Java specification parameter objects.
     */
    private static List<MOPParameter> convertParameters(final String paramString) {
        try {
            return parseJavaBubble(paramString).MOPParameters();
        } catch(Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a {@link String} of declarations into a list of Java {@link BodyDeclaration}
     * elements.
     * @param declarations A language-specific bubble with declarations.
     * @return A list of Java declaration objects.
     */
    private static List<BodyDeclaration> convertDeclarations(final String declarations) {
        try {
            return parseJavaBubble(declarations).ClassOrInterfaceBody(false);
        } catch(Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a language-independent event into a Java event.
     * @param event The language-independent event object.
     * @return A Java-specific event object.
     */
    private static EventDefinitionExt convert(final Event event) throws ParseException {
        // This syntax is pretty complex, just reconstruct it and defer it to the existing parser
        // for now.
        final StringBuilder eventString = new StringBuilder();
        for(String modifier : event.getModifiers()) {
            eventString.append(modifier).append(" ");
        }
        eventString.append("event ").append(event.getName());
        for(String modifier : event.getDefinitionModifiers()) {
            eventString.append(" ").append(modifier);
        }
        eventString.append(event.getDefinition()).append(event.getPointcut());
        eventString.append(event.getAction());
        return parseJavaBubble(eventString.toString()).Event();
    }

    /**
     * Convert a language-independent property into a Java property object.
     * @param index The index of this property in the specification.
     * @param property The property to convert.
     * @return A Java-specific property object.
     */
    private static PropertyAndHandlersExt convert(final int index, final Property property) {
        final String logicId = property.getName();
        final String propertyName = "defaultProp" + index;
        final String formula = property.getSyntax();
        final PropertyExt propertyExt = new FormulaExt(0, 0, logicId, formula, propertyName);

        final List<HandlerExt> handlerList = new ArrayList<HandlerExt>();
        final HashMap<String, BlockStmt> handlerMap = new HashMap<String, BlockStmt>();
        for(PropertyHandler handler : property.getHandlers()) {
            HandlerExt converted = convert(handler);
            handlerList.add(converted);
            handlerMap.put(handler.getState().toLowerCase(), converted.getBlockStmt());
        }
        return new PropertyAndHandlersExt(0, 0, propertyExt, handlerMap, handlerList);
    }

    /**
     * Convert a language-independent handler into a Java handler object.
     * @param handler The handler to convert.
     * @return A Java-specific handler object.
     */
    private static HandlerExt convert(final PropertyHandler handler) {
        final String id = handler.getState();
        final String propertyReference = null;
        final String specReference = null;
        BlockStmt action = null;
        try {
            action = parseJavaBubble(handler.getAction()).Block();
        } catch(Exception e) {
            //e.printStackTrace();
        }
        return new HandlerExt(0, 0, id, action, propertyReference, specReference);
    }
}
