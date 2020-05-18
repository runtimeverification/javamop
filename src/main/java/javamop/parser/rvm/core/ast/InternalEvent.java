package javamop.parser.rvm.core.ast;

public class InternalEvent {
    private final String name;
    private final String parameters;
    private final String body;

    public InternalEvent(final String name,
                         final String parameters,
                         final String body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() { return this.name; }

    public String getParameters() { return this.parameters; }

    public String getBody() { return this.body; }
}
