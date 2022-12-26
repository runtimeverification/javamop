package com.github.javaparser;

import static com.github.javaparser.GeneratedJavaParserConstants.EOF;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;

//TODO: This is a hack!
public abstract class GeneratedParserBase extends GeneratedJavaParserBase {

    public GeneratedParserBase() {
        super();
    }

    public TokenRange tokenRange() {
        return super.tokenRange();
    }

    public TokenRange range(JavaToken begin, JavaToken end) {
        return super.range(begin, end);
    }

    public TokenRange range(Node begin, JavaToken end) {
        return super.range(begin, end);
    }

    public TokenRange range(Node begin, Node end) {
        return super.range(begin, end);
    }

    public <T extends Node> NodeList<T> emptyList() {
        return new NodeList<>();
    }

    /** TODO: This code is copied from JavaParser; is there a better way to reuse? */
    public TokenRange recover(int recoveryTokenType, javamop.parser.main_parser.ParseException p) {
        JavaToken begin = null;
        if (p.currentToken != null) {
            begin = token();
        }
        Token t;
        do {
            t = getNextToken();
        } while (t.kind != recoveryTokenType && t.kind != EOF);

        JavaToken end = token();

        TokenRange tokenRange = null;
        if (begin != null && end != null) {
            tokenRange = range(begin, end);
        }

        problems.add(new Problem(p.getMessage(), tokenRange, p));
        return tokenRange;
    }

    public JavaToken orIfInvalid(JavaToken firstChoice, JavaToken secondChoice) {
        return super.orIfInvalid(firstChoice, secondChoice);
    }

    public JavaToken orIfInvalid(JavaToken firstChoice, Node secondChoice) {
        return super.orIfInvalid(firstChoice, secondChoice);
    }

    public Type juggleArrayType(Type partialType, List<ArrayType.ArrayBracketPair> additionalBrackets) {
        return super.juggleArrayType(partialType, additionalBrackets);
    }

    public Expression generateLambda(Expression ret, Statement lambdaBody) {
        return super.generateLambda(ret, lambdaBody);
    }

    public ArrayCreationExpr juggleArrayCreation(TokenRange range, List<TokenRange> levelRanges, Type type,
                                                 NodeList<Expression> dimensions,
                                                 List<NodeList<AnnotationExpr>> arrayAnnotations,
                                                 ArrayInitializerExpr arrayInitializerExpr) {
        return super.juggleArrayCreation(range, levelRanges, type, dimensions, arrayAnnotations, arrayInitializerExpr);
    }

    public <T extends Node> NodeList<T> addWhenNotNull(NodeList<T> list, T obj) {
        return super.addWhenNotNull(list, obj);
    }

    public <T extends Node> NodeList<T> add(NodeList<T> list, T obj) {
        return super.add(list, obj);
    }

    public <T> List<T> add(List<T> list, T obj) {
        return super.add(list, obj);
    }

    public JavaToken nodeListBegin(NodeList<?> l) {
        return super.nodeListBegin(l);
    }

    public <T extends Node> NodeList<T> prepend(NodeList<T> list, T obj) {
        return super.prepend(list, obj);
    }

    @Override
    GeneratedJavaParserTokenManager getTokenSource() {
        return null;
    }

    @Override
    void ReInit(Provider provider) {

    }

    /* Makes the parser keep a list of tokens */
    void setStoreTokens(boolean storeTokens) {
        this.storeTokens = storeTokens;
    }
}
