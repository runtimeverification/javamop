package javamop.parser.rvm.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A logic property in the specification with handlers.
 *
 * @author A. Cody Schuffelen
 */
public class Property {

    private final String name;
    private final String syntax;
    private final List<PropertyHandler> handlers;

    /**
     * Construct the Property out of its component elements.
     *
     * @param name     The logic name of the property (e.g. ere, fsm).
     * @param syntax   The code describing the property.
     * @param handlers Handlers used to respond to states in the property.
     */
    public Property(final String name, final String syntax,
                    final List<PropertyHandler> handlers) {
        this.name = name;
        this.syntax = syntax;
        this.handlers = Collections
                .unmodifiableList(new ArrayList<PropertyHandler>(handlers));
    }

    /**
     * The logic name of the property.
     *
     * @return The name of the logic repository plugin used in the property.
     */
    public String getName() {
        return name;
    }

    /**
     * The expression describing the property.
     *
     * @return The property logic formula.
     */
    public String getSyntax() {
        return syntax;
    }

    /**
     * An unmodifiable list of the handlers for the different states of the
     * property.
     *
     * @return A list of state handlers.
     */
    public List<PropertyHandler> getHandlers() {
        return handlers;
    }
}
