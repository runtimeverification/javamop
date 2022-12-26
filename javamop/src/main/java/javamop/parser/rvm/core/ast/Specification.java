package javamop.parser.rvm.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A RVM property specification.
 *
 * @author A. Cody Schuffelen
 */
public class Specification {

    private final String preDeclarations;
    private final List<String> languageModifiers;
    private final String name;
    private final String languageParameters;
    private final String languageDeclarations;
    private final List<Event> events;
    private final List<Property> properties;

    /**
     * Construct the specification out of its children elements.
     *
     * @param languageModifiers    Words before the name directing behavior of rv-monitor.
     * @param name                 The name of the specification.
     * @param languageParameters   Parameters used to parameterize the monitor.
     * @param languageDeclarations Language-specific declarations used in the monitoring code.
     * @param events               The events to monitor in the code.
     * @param properties           Properties and handlers on the sequence of events.
     */
    public Specification(final String preDeclarations,
                         final List<String> languageModifiers, final String name,
                         final String languageParameters, final String languageDeclarations,
                         final List<Event> events, final List<Property> properties) {
        this.preDeclarations = preDeclarations;
        this.languageModifiers = Collections
                .unmodifiableList(new ArrayList<String>(languageModifiers));
        this.name = name;
        this.languageParameters = languageParameters;
        this.languageDeclarations = languageDeclarations;
        this.events = Collections
                .unmodifiableList(new ArrayList<javamop.parser.rvm.core.ast.Event>(events));
        this.properties = Collections.unmodifiableList(new ArrayList<Property>(
                properties));
    }

    /**
     * An unmodifiable list of words used to affect the code generator.
     *
     * @return Words affecting the code generator.
     */
    public List<String> getLanguageModifiers() {
        return languageModifiers;
    }

    /**
     * The name of the specification/monitor.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Variables used to parameterize the monitor, if any.
     *
     * @return The monitor parameters.
     */
    public String getLanguageParameters() {
        return languageParameters;
    }

    /**
     * Language-specific declarations used inside the monitor.
     *
     * @return Declarations used by the monitor written in the target language.
     */
    public String getLanguageDeclarations() {
        return languageDeclarations;
    }

    /**
     * An unmodifiable list with the events to monitor.
     *
     * @return The events being monitored by the generated monitor program.
     */
    public List<javamop.parser.rvm.core.ast.Event> getEvents() {
        return events;
    }

    /**
     * An unmodifiable list with the logic properties and their handlers.
     *
     * @return The properties with their handlers of the specification.
     */
    public List<Property> getProperties() {
        return properties;
    }

}
