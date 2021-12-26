package javamop.parser.main_parser;

import com.github.javaparser.JavaToken;

abstract class TokenBase {
    /**
     * For tracking the >> >>> ambiguity.
     */
    public int realKind;

    /**
     * This is the link to the token that JavaParser presents to the user
     */
    JavaToken javaToken = null;
}