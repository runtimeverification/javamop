package javamop.output;

import java.util.List;

import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class CoEnableSet extends EnableSet{

	public CoEnableSet(List<EventDefinition> events, MOPParameters specParameters) {
		super(events, specParameters);
	}

	public CoEnableSet(String coenableSetStr, List<EventDefinition> events, MOPParameters specParameters) {
		super(coenableSetStr, events, specParameters);
	}
	
	public CoEnableSet(PropertyAndHandlers prop, List<EventDefinition> events, MOPParameters specParameters){
		this(events, specParameters);
		for (String categoryName : prop.getHandlers().keySet()) {
			String coenableForCategory = prop.getLogicProperty(categoryName.toLowerCase() + " coenables");

			if (coenableForCategory != null)
				add(new EnableSet(coenableForCategory, events, specParameters));
		}
	}

	
}
