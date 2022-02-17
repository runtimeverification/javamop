package com.runtimeverification.rvmonitor.java.rvj;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

/**
 * Contains some static functions to verify properties about RVM specification
 * objects.
 */
public class RVMErrorChecker {

    /**
     * Verify some properties about a {@link RVMonitorSpec}.
     *
     * @param rvMonitorSpec
     *            The specification to verify.
     * @throws RVMException
     *             When some expected properties are not satisfied.
     */
    public static void verify(RVMonitorSpec rvMonitorSpec) throws RVMException {
        for (PropertyAndHandlers prop : rvMonitorSpec
                .getPropertiesAndHandlers()) {
            verifyHandlers(prop);
        }
        for (EventDefinition event : rvMonitorSpec.getEvents()) {
            // endProgram cannot have any parameter.
            verifyEndProgramParam(event);

            // endThread cannot have any parameter except one from thread
            // pointcut.
            verifyEndThreadParam(event);
        }

        // there should be only one endProgram event
        verifyUniqueEndProgram(rvMonitorSpec);

        verifySameEventName(rvMonitorSpec);
        verifyGeneralParametric(rvMonitorSpec);

        // check if two endObject pointcuts share the same parameter, which
        // should not happen

    }

    /**
     * Verify every proprety has an associated handler.
     *
     * @param prop
     *            The object containing properties and handlers.
     * @throws RVMException
     *             If a property doesn't have an associated handler.
     */
    public static void verifyHandlers(PropertyAndHandlers prop)
            throws RVMException {
        for (String handlerName : prop.getHandlers().keySet()) {
            if (prop.getLogicProperty(handlerName + " condition") == null) {
                throw new RVMException(handlerName
                        + " is not a supported state in this logic, "
                        + prop.getProperty().getType() + ".");
            }
        }
    }

    /**
     * Verify no two events have the same name and parameter signature.
     *
     * @param rvmSpec
     *            The specification object.
     * @throws RVMException
     *             If two events have the same name and parameter signature.
     */
    public static void verifySameEventName(RVMonitorSpec rvmSpec)
            throws RVMException {
        HashMap<String, RVMParameters> nameToParam = new HashMap<String, RVMParameters>();

        for (EventDefinition event : rvmSpec.getEvents()) {
            if (nameToParam.get(event.getId()) != null) {
                if (event.getParameters()
                        .equals(nameToParam.get(event.getId()))) {
                    String prettyname = rvmSpec.getName() + "." + event.getId();
                    throw new RVMException(
                            "An event that has the same name and signature has been already defined: "
                                    + prettyname);
                }
            } else {
                nameToParam.put(event.getId(), event.getParameters());
            }
        }
    }

    /**
     * Verify there is only one endProgram event.
     *
     * @param rvmSpec
     *            The specification object.
     * @throws RVMException
     *             If there are two endProgram events.
     */
    public static void verifyUniqueEndProgram(RVMonitorSpec rvmSpec)
            throws RVMException {
        boolean found = false;

        for (EventDefinition event : rvmSpec.getEvents()) {
            if (event.isEndProgram()) {
                if (found)
                    throw new RVMException(
                            "There can be only one endProgram event");
                else
                    found = true;
            }
        }
    }

    /**
     * Verify parametric specifications have parameters.
     *
     * @param rvmSpec
     *            The specification object.
     * @throws RVMException
     *             If there is a parametric specification without a parameter.
     */
    public static void verifyGeneralParametric(RVMonitorSpec rvmSpec)
            throws RVMException {
        if (rvmSpec.isGeneral() && rvmSpec.getParameters().size() == 0)
            throw new RVMException(
                    "[Internal Error] It cannot use general parameteric algorithm when there is no parameter");
    }

    /**
     * Verify endProgram events don't have parameters.
     *
     * @param event
     *            The event to verify.
     * @throws RVMException
     *             If there are two endProgram events.
     */
    public static void verifyEndProgramParam(EventDefinition event)
            throws RVMException {
        if (event.isEndProgram() && event.getParameters().size() > 0)
            throw new RVMException(
                    "A endProgram pointcut cannot have any parameter.");
    }

    /**
     * Verify endThread parameters only can include the associated Thread
     * variable.
     *
     * @param event
     *            The event to verify.
     * @throws RVMException
     *             If an endThread event has a parameter besides its associated
     *             thread.
     */
    public static void verifyEndThreadParam(EventDefinition event)
            throws RVMException {
        if (event.isEndThread())
            if (event.getParametersWithoutThreadVar().size() > 0)
                throw new RVMException(
                        "A endThread pointcut cannot have any parameter except one from thread pointcut.");
    }

}
