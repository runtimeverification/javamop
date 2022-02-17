package com.runtimeverification.rvmonitor.c.rvc;

import java.util.HashMap;

public interface CSpecification {

    /**
     * A string containing the includes, or the preamble of the specification
     * file.
     * 
     * @return A string with all the #include statements.
     */
    public String getIncludes();

    /**
     * The name of the specification, as a string.
     * 
     * @return The name of the specification.
     */
    public String getSpecName();

    /**
     * A mapping from event names to actions to take on the events happening.
     * 
     * @return A mapping from event names to action code.
     */
    public HashMap<String, String> getEvents();

    /**
     * A mapping from event names to Strings with the complete parameters of the
     * events.
     * 
     * @return A mapping from event names to event parameters.
     */
    public HashMap<String, String> getParameters();

    /**
     * A mapping from event names to Strings with the parametric parameters of
     * the events. These are the complete parameters with an additional
     * "void* key" parameter at the end.
     * 
     * @return A mapping from event names to parametric parameters.
     */
    public HashMap<String, String> getPParameters();

    /**
     * A mapping from logic state names to the action code to run on entering
     * that state.
     * 
     * @return A mapping from states to actions.
     */
    public HashMap<String, String> getHandlers();

    /**
     * The declarations made in the specification before the events.
     * 
     * @return The specification declarations.
     */
    public String getDeclarations();

    /**
     * The type of logic formalism describing the properties to evaluate.
     * 
     * @return The logic formalism used in the specification.
     */
    public String getFormalism();

    /**
     * The formula of the logic formalism explicitly describing the properties.
     * 
     * @return The logic formula.
     */
    public String getFormula();
}
