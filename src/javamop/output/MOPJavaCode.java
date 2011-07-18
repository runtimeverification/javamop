package javamop.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MOPJavaCode {
	String code;
	MOPVariable monitorName = null;

	public MOPJavaCode(String code) {
		this.code = code;
		if(this.code != null)
			this.code = this.code.trim();
	}

	public MOPJavaCode(String code, MOPVariable monitorName) {
		this.code = code;
		if(this.code != null)
			this.code = this.code.trim();
		this.monitorName = monitorName;
	}

	public String rewriteVariables(String input) {
		String ret = input;
		String tagPattern = "\\$(\\w+)\\$";
		Pattern pattern = Pattern.compile(tagPattern);
		Matcher matcher = pattern.matcher(ret);

		while (matcher.find()) {
			String tagStr = matcher.group();
			String varName = tagStr.replaceAll(tagPattern, "$1");
			MOPVariable var = new MOPVariable(varName);

			ret = ret.replaceAll(tagStr.replaceAll("\\$", "\\\\\\$"), var.toString());
		}
		return ret;
	}

	public boolean isEmpty() {
		if (code == null || code.length() == 0)
			return true;
		else
			return false;
	}

	public String toString() {
		String ret = "";

		if (code != null)
			ret += code;

		if (this.monitorName != null)
			ret = ret.replaceAll("\\@MONITORCLASS", monitorName.toString());

		ret = rewriteVariables(ret);

		if (ret.length() != 0 && !ret.endsWith("\n"))
			ret += "\n";

		return ret;
	}
}
