// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser;

import javamop.parser.rvm.core.ast.*;
import javamop.parser.main_parser.RVParser;
import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.SpecModifierSet;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.*;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;
import javamop.util.MOPException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class with static methods to convert the language-independent syntax into Java-specific
 * MOPSpecFileExt objects.
 * @author A. Cody Schuffelen
 */
final public class JavaParserAdapter {

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
            String str = new String(Files.readAllBytes(Paths.get(file.getPath())));
            return parse(str);
        } catch(Exception e) {
            throw new MOPException(e);
        }
    }

    /**
     * Get the list of raw monitoring code, one for each raw specification.
     * There might be multiple specs in single file, as a result, there might be
     * more than one raw monitoring block in one spec file.
     *
     * @param rawSpecFile which may contain multiple spec.
     * @return The list of the raw monitoring code, sorted by the position in the spec file.
     * @throws IOException If the monitoring code is not extracted successfully.
     */
    public static List<String> getRawMonitoringCode(String rawSpecFile) throws IOException {
        List<String> listOfRawMonitoringCode = new ArrayList<>();
        Pattern rawPattern = Pattern.compile("(?<=\\})\\s*raw\\s*:");
        Matcher matcher = rawPattern.matcher(rawSpecFile);

        while (matcher.find()) {
            int balanceOfParenthesis = 0;

            int indexOfRawKeyword = matcher.start();

            String rawCodeUtilLast = rawSpecFile.substring(indexOfRawKeyword);
            boolean codeIsComplete = false;
            for (int j = 0; j < rawCodeUtilLast.length(); j++) {
                char curC = rawCodeUtilLast.charAt(j);
                if (curC == '{')
                    balanceOfParenthesis++;
                else if (curC == '}')
                    balanceOfParenthesis--;
                else {
                }

                if (balanceOfParenthesis == -1) {
                    //reached the end of the spec, indicating the end of raw code
                    listOfRawMonitoringCode.add(
                            rawCodeUtilLast.substring(0, j) //raw code that contains "raw:"
                                    .replaceAll("\\s*raw\\s*:","") //remove "raw:"
                    );
                    codeIsComplete = true;
                    break;
                }
            }

            if (!codeIsComplete) {
                throw new IOException("Unexpected end of file while reading" +
                        " raw monitoring code.");
            }
        }

        return listOfRawMonitoringCode;
    }

    /**
     * Produce a MOPSpecFileExt by reading a string through the language-independent MOP parser.
     * @param str The string to read from.
     * @return A Java-specific MOP specification object.
     */
    public static MOPSpecFileExt parse(String str) throws MOPException {
        try {
            String specWithComments = str;
            //remove all the one-line comments.
            str = str.replaceAll("//.*[\n\r]", "");
            String originalSpecStr = str;
            //do some pre-processing to extract the raw monitoring code if there's any
            List<String> listOfRawCode = getRawMonitoringCode(str);

            if (listOfRawCode.size() > 0) {
                str = str.replaceAll("(?<=\\})\\s*raw\\s*:", "");
                for (int i = 0; i < listOfRawCode.size(); i++) {
                    String rawCode = listOfRawCode.get(i);
                    str = str.replace(rawCode, "");
                }

                final Reader source = new StringReader(str);
                final MonitorFile spec = RVParser.parse(source);

                List<String> listOfUpdatedRawCode = new ArrayList<>();
                String regex4SplitingSpecs = "(";
                for (int i = 0; i < spec.getSpecifications().size() - 1; i++) {
                    Specification specI = spec.getSpecifications().get(i);
                    String specIName = specI.getName();
                    regex4SplitingSpecs += specIName + "|";
                }

                if (spec.getSpecifications().size() > 0) {
                    regex4SplitingSpecs += spec.getSpecifications().get(spec.getSpecifications()
                                            .size() - 1).getName();
                }

                regex4SplitingSpecs += ")\\s*\\(";
                String[] partitions = originalSpecStr.split(regex4SplitingSpecs);

                for (int i = 0, j = 0; i < spec.getSpecifications().size(); i++) {
                    if (partitions[i+1].contains(listOfRawCode.get(j))) {
                        listOfUpdatedRawCode.add(listOfRawCode.get(j++));
                    } else {
                        listOfUpdatedRawCode.add(null);
                    }
                }

                return convert(spec, listOfUpdatedRawCode);
            } else { //there is no raw property, so do the normal parsing as before
                final Reader source = new StringReader(str);
                final MonitorFile spec = RVParser.parse(source);
                return convert(spec);
            }

        } catch(Exception e) {
            throw new MOPException(e);
        }
    }

    /**
     * Convert a language-independent specification into one with Java-specific information.
     * @param file The specification to convert.
     * @param listOfRawCode
     * @return The Java-specific specification.
     */
    private static MOPSpecFileExt convert(MonitorFile file, List<String> listOfRawCode) throws ParseException {
        final PackageDeclaration filePackage = getPackage(file.getPreamble());
        final List<ImportDeclaration> imports = getImports(file.getPreamble());
        final ArrayList<JavaMOPSpecExt> specs = new ArrayList<JavaMOPSpecExt>();

        int counter = 0;
        for(Specification spec : file.getSpecifications()) {
            specs.add(convert(filePackage, spec,
                    spec.getProperties().size() == 0 ? listOfRawCode.get(counter++) : null
                    //if the size of properties is 0, then it should be a raw spec and it
                    // requires a raw logic plugin.
                                ));
        }
        return new MOPSpecFileExt(0, 0, filePackage, imports, specs);
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
            specs.add(convert(filePackage, spec, null));
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
     * @param rawCode
     * @return The Java-specific specification.
     */
    private static JavaMOPSpecExt convert(final PackageDeclaration pack,
                                          final Specification spec, String rawCode) throws
            ParseException {
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
            inMethod, extensions, declarations, events, properties).setRawLogic(rawCode);
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
