package com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This interface defines all the operations that a formatter should implement.
 * Each formatter is supposed to generate good-looking code of String type from
 * keywords, identifiers, operators, and so on, which are fed into the formatter
 * one by one.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface ICodeFormatter {
    public enum State {
        NEWLINE, COMMENT, KEYWORD, OPERATOR, OPERATORSEMITIGHT, OPERATORTIGHT, LITERAL, IDENTIFIER,
    }

    public String getCode();

    public boolean needsPrintVariableDescription();

    public void addImport(CodeType type);

    public void newline();

    public void push();

    public void pop();

    public void openBlock();

    public void closeBlock();

    public void endOfStatement();

    public void comment(String cmt);

    public void type(CodeType type);

    public void keyword(String kw);

    public void operator(String op);

    public void literal(String lt);

    public void identifier(String id);

    public void legacyExpr(String raw);

    public void legacyStmt(String raw);
}
