// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;


public class CollectUserVarVisitor extends BaseVisitor<List<String>, Object> {

    public List<String> visit(ClassOrInterfaceDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        ret.add(n.getName().asString());

        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                List<String> temp = a.accept(this, arg);

                if (temp != null) {
                    ret.addAll(temp);
                }
            }
        }

        return ret;
    }

    public List<String> visit(EnumDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(EnumConstantDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(AnnotationDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                List<String> temp = a.accept(this, arg);

                if (temp != null)
                    ret.addAll(temp);
            }
        }

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(AnnotationMemberDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                List<String> temp = a.accept(this, arg);

                if (temp != null)
                    ret.addAll(temp);
            }
        }

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(FieldDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                List<String> temp = a.accept(this, arg);

                if (temp != null)
                    ret.addAll(temp);
            }
        }

        if (n.getVariables() != null) {
            for (VariableDeclarator var : n.getVariables()) {
                List<String> temp = var.accept(this, arg);

                if (temp != null)
                    ret.addAll(temp);
            }
        }

        return ret;
    }

    public List<String> visit(VariableDeclarator n, Object arg) {
		return getNames(n.getName() != null, n.getName().accept(this, arg), n);
	}

    public List<String> visit(ConstructorDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(MethodDeclaration n, Object arg) {
        List<String> ret = new ArrayList<>();

        ret.add(n.getName().asString());

        return ret;
    }

    public List<String> visit(MarkerAnnotationExpr n, Object arg) {
		return getNames(n.getName() != null, n.getName().accept(this, arg), n);
    }

    public List<String> visit(SingleMemberAnnotationExpr n, Object arg) {
		return getNames(n.getName() != null, n.getName().accept(this, arg), n);
    }

    public List<String> visit(NormalAnnotationExpr n, Object arg) {
		return getNames(n.getName() != null, n.getName().accept(this, arg), n);
	}

	private List<String> getNames(boolean isNotNull, List<String> results, Node n) {
		List<String> ret = new ArrayList<>();

		if (isNotNull) {
			List<String> temp = results;
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

}
