package javamop.output.combinedaspect.indexingtree.reftree;

import java.util.ArrayList;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;

public class RefTree {
	MOPVariable name;

	public String type;

	public ArrayList<JavaMOPSpec> properties = new ArrayList<JavaMOPSpec>();
	public ArrayList<JavaMOPSpec> generalProperties = new ArrayList<JavaMOPSpec>();
	
	public IndexingTree hostIndexingTree = null;

	public RefTree(String aspectName, MOPParameter param) {
		this.type = param.getType().toString();

		String typeStr = type;
		int dim;

		if (typeStr.endsWith("]")) {
			int firstBracket = typeStr.indexOf("[");
			int lastBracket = typeStr.lastIndexOf("[");

			dim = lastBracket - firstBracket + 1;

			typeStr = typeStr.substring(0, firstBracket);

			if (dim > 1)
				typeStr += dim;
		}

		this.name = new MOPVariable(aspectName + "_" + typeStr + "_RefMap");
	}

	public void addProperty(JavaMOPSpec spec) {
		properties.add(spec);
		if(spec.isGeneral())
			generalProperties.add(spec);
	}
	
	public void setHost(IndexingTree indexingTree){
		hostIndexingTree = indexingTree;
	}

	public String get(MOPVariable tempRef, MOPParameter p) {
		String ret = "";
		MOPVariable name;
		
		if(hostIndexingTree == null)
			name = this.name;
		else
			name = hostIndexingTree.getName();
		
		if (generalProperties.size() == 0)
			ret += tempRef + " = " + name + ".getRef(" + p.getName();
		else if (generalProperties.size() == 1)
			ret += tempRef + " = " + name + ".getTagRef(" + p.getName();
		else
			ret += tempRef + " = " + name + ".getMultiTagRef(" + p.getName();
		
		if(properties.size() > 1)
			ret += ", thisJoinPoint.getStaticPart().getId()";
		
		ret += ");\n";
		
		return ret;
	}

	public String getRefNonCreative(MOPVariable tempRef, MOPParameter p) {
		String ret = "";
		MOPVariable name;
		
		if(hostIndexingTree == null)
			name = this.name;
		else
			name = hostIndexingTree.getName();

		if (generalProperties.size() == 0)
			ret += tempRef + " = " + name + ".getRefNonCreative(" + p.getName();
		else if (generalProperties.size() == 1)
			ret += tempRef + " = " + name + ".getTagRefNonCreative(" + p.getName();
		else
			ret += tempRef + " = " + name + ".getMultiTagRefNonCreative(" + p.getName();

		if(properties.size() > 1)
			ret += ", thisJoinPoint.getStaticPart().getId()";
		
		ret += ");\n";

		return ret;
	}

	public boolean isTagging() {
		return generalProperties.size() != 0;
	}

	public int getTagNumber(JavaMOPSpec spec) {
		if (generalProperties.size() <= 1)
			return -1;
		else
			return generalProperties.indexOf(spec);
	}

	public String getResultType() {
		String ret = "";

		if (generalProperties.size() == 0)
			ret = "javamoprt.ref.MOPWeakReference";
		else if (generalProperties.size() == 1)
			ret = "javamoprt.ref.MOPTagWeakReference";
		else
			ret = "javamoprt.ref.MOPMultiTagWeakReference";

		return ret;
	}

	public String getType() {
		String ret = "";
		
		if(hostIndexingTree == null){
			if (generalProperties.size() == 0)
				ret = "javamoprt.map.MOPBasicRefMap";
			else if (generalProperties.size() == 1)
				ret = "javamoprt.map.MOPTagRefMap";
			else
				ret = "javamoprt.map.MOPMultiTagRefMap";
		} else {
			ret = hostIndexingTree.getRefTreeType();
		}

		return ret;
	}
	
	public MOPVariable getName() {
		return name;
	}

	public String toString() {
		String ret = "";

		ret += "static javamoprt.map.MOPRefMap ";
		ret += name;
		ret += " = ";
		if(hostIndexingTree == null){
			if(generalProperties.size() > 1)
				ret += "new " + getType() + "(" + generalProperties.size() + ");\n";
			else
				ret += "new " + getType() + "();\n";
		} else {
			ret += hostIndexingTree.getName() + ";\n";
		}

		return ret;
	}

}
