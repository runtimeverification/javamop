package javamop.parser.main_parser;

import com.github.javaparser.JavaToken;

public abstract class TokenBase {
    /**
     * For tracking the >> >>> ambiguity.
     */
    public int realKind;

    /**
     * This is the link to the token that JavaParser presents to the user
     */
    public JavaToken javaToken = null;
}