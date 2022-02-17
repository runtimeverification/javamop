package javamop.parser.rvm.core.ast;

/**
 * A handler to invoke on reaching a specific state in a logic property.
 *
 * @author A. Cody Schuffelen
 */
public class PropertyHandler {

    private final String state;
    private final String action;

    /**
     * Construct a PropertyHandler out of its component elements.
     *
     * @param state  The state to invoke the handler on.
     * @param action The language-specific action to take on entering the state.
     */
    public PropertyHandler(final String state, final String action) {
        this.state = state;
        this.action = action;
    }

    /**
     * The state to apply the action to.
     *
     * @return The String name of the state this handler is related to.
     */
    public String getState() {
        return state;
    }

    /**
     * The language-specific action to apply on reaching the state.
     *
     * @return Language-specific code for the action.
     */
    public String getAction() {
        return action;
    }
}
