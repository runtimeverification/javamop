package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

import java.util.ArrayList;

public class NotCondition extends Condition {
	private Condition condition;

	public NotCondition(Condition condition) {
		this.condition = condition;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setBeforeEvent(Condition condition) {
		this.condition = condition;
	}

	public ArrayList<String> getAllNodes() {
		return condition.getAllNodes();
	}

}
