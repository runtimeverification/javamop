// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.expr.QualifiedNameExpr;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.aspectj.*;


public class CollectUserVarVisitor extends BaseVisitor<List<String>, Object> {

	public List<String> visit(ClassOrInterfaceDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

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
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(EnumConstantDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(AnnotationDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(AnnotationMemberDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(FieldDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

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
		List<String> ret = new ArrayList<String>();

		if (n.getId() != null) {
			List<String> temp = n.getId().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(ConstructorDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());
		
		return ret;
	}

	public List<String> visit(MethodDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());
		
		return ret;
	}

	public List<String> visit(MarkerAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(SingleMemberAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(NormalAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

}
