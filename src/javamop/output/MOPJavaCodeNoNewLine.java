package javamop.output;

import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class MOPJavaCodeNoNewLine extends MOPJavaCode {

	public MOPJavaCodeNoNewLine(String code) {
		super(code);
	}

	public MOPJavaCodeNoNewLine(String code, MOPVariable monitorName) {
		super(code, monitorName);
	}

	public MOPJavaCodeNoNewLine(PropertyAndHandlers prop, String code, MOPVariable monitorName) {
		super(prop, code, monitorName);
	}

	public String toString() {
		String ret = super.toString();
		ret = ret.trim();

		if (ret.length() != 0 && ret.endsWith("\n"))
			ret = ret.substring(0, ret.length() - 1);

		return ret;
	}

}
