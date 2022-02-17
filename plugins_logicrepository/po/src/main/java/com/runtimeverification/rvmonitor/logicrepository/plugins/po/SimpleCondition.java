package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

import java.util.ArrayList;

public class SimpleCondition extends Condition {
	private String beforeEvent;

	public SimpleCondition(String beforeEvent) {
		this.beforeEvent = beforeEvent;
	}

	public String getBeforeEvent() {
		return beforeEvent;
	}

	public void setBeforeEvent(String beforeEvent) {
		this.beforeEvent = beforeEvent;
	}

	public ArrayList<String> getAllNodes() {
		ArrayList<String> ret = new ArrayList<String>();

		ret.add(beforeEvent);

		return ret;
	}

}
