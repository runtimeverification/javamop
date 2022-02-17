package com.github.javaparser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;

//TODO: This is a hack!
public class MOPModifierHolder extends ModifierHolder {
    public JavaToken begin;
    public NodeList<Modifier> modifiers;
    public NodeList<AnnotationExpr> annotations;

    public MOPModifierHolder(JavaToken begin, NodeList<Modifier> modifiers, NodeList<AnnotationExpr> annotations) {
        super(begin, modifiers, annotations);
        this.begin = super.begin;
        this.modifiers = super.modifiers;
        this.annotations = super.annotations;
    }
}
