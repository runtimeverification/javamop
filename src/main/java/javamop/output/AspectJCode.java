// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output;

import javamop.JavaMOPMain;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.util.MOPException;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The top-level generated AspectJ code.
 */
class AspectJCode {
    private final String name;

    private final Package packageDecl;
    private final Imports imports;
    private final String baseAspect;
    private final CombinedAspect aspect;
    private boolean versionedStack = false;
    private final SystemAspect systemAspect;

    /**
     * Construct the AspectJ code.
     *
     * @param name        The name of the aspect.
     * @param mopSpecFile The specification file that will be used to build aspects.
     * @throw MOPException If something goes wrong in generating the aspects.
     */
    public AspectJCode(String name, MOPSpecFile mopSpecFile) throws MOPException, IOException {
        this.name = name;
        packageDecl = new Package(mopSpecFile);
        imports = new Imports(mopSpecFile);

        //init the base aspect
        this.baseAspect = initBaseAspect();

        for (JavaMOPSpec mopSpec : mopSpecFile.getSpecs()) {

            for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
                versionedStack |= prop.getVersionedStack();
            }
        }

        aspect = new CombinedAspect(name, mopSpecFile, versionedStack);

        if (versionedStack) {
            systemAspect = new SystemAspect(name);
        } else {
            systemAspect = null;
        }
    }

    private String initBaseAspect() throws IOException {
        if (JavaMOPMain.options.baseAspect == null) {
            return "aspect BaseAspect {\n" +
                    "    pointcut notwithin() :\n" +
                    "    !within(sun..*) &&\n" +
                    "    !within(java..*) &&\n" +
                    "    !within(javax..*) &&\n" +
                    "    !within(com.sun..*) &&\n" +
                    "    !within(org.dacapo.harness..*) &&\n" +
                    "    !within(org.apache.commons..*) &&\n" +
                    "    !within(org.apache.geronimo..*) &&\n" +
                    "    !within(net.sf.cglib..*) &&\n" +
                    "    !within(mop..*) &&\n" +
                    "    !within(javamoprt..*) &&\n" +
                    "    !within(rvmonitorrt..*) &&\n" +
                    "    !within(com.runtimeverification..*);\n" +
                    "}";
        } else if (!"BaseAspect.aj".equals(JavaMOPMain.options.baseAspect.getName())) {
            throw new IOException("For now, --baseaspect files should be " +
                    "called BaseAspect.aj");
        } else if (!JavaMOPMain.options.baseAspect.exists()) {
            throw new IOException("BaseAspect.aj is not found");
        } else {
            String baseAJ = new String(Files.readAllBytes(
                    JavaMOPMain.options.baseAspect.toPath()));

            return baseAJ.replaceAll("public(\\s)+aspect(\\s)+BaseAspect", "aspect BaseAspect");
        }
    }

    /**
     * Generate the AspectJ code that complements the generated RV-Monitor monitoring code.
     *
     * @return The AspectJ/Java source code.
     */
    @Override
    public String toString() {
        String ret = "";
        ret += packageDecl;
        ret += "\n";
        ret += imports.toString().replaceAll("import javamoprt.*", "");

        ret += "\n";
        
        if(!JavaMOPMain.options.emop) {
            ret += this.baseAspect + "\n\n";
        }

        // The order of these two is really important.
        if (systemAspect != null) {
            ret += "aspect " + name + "OrderAspect {\n";
            ret += "declare precedence : ";
            ret += systemAspect.getSystemAspectName() + "";
            ret += ", ";
            ret += systemAspect.getSystemAspectName() + "2";
            ret += ", ";
            ret += aspect.getAspectName();
            ret += ";\n";

            ret += "}\n";
            ret += "\n";
        }

        ret += aspect.toString();

        if (systemAspect != null)
            ret += "\n" + systemAspect;

        return ret;
    }
}
