// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/*
 * Copyright (C) 2007 Julio Vilmar Gesser.
 * 
 * This file is part of Java 1.5 parser and Abstract Syntax Tree.
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 05/10/2006
 */
package javamop.parser.astex;

import java.util.Objects;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import javamop.parser.astex.visitor.DumpVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public abstract class ExtNode extends Node {

    public ExtNode(TokenRange tokenRange) {
    	super(tokenRange);
    }

    /**
     * Iterate through JavaMOP ast classes and output .rvm file
     *
     * @return .rvm file contents as a String
     */
    public String toRVString() {
        DumpVisitor visitor = new DumpVisitor(new DefaultPrinterConfiguration());
        accept(visitor, null);
        return visitor.getSource();
    }

    public int hashCode() {
        DumpVisitor visitor = new DumpVisitor(new DefaultPrinterConfiguration());
        accept(visitor, null);
        return Objects.hash(visitor.getSource());
    }
}
