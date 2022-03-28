package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

import java.util.ArrayList;

public abstract class Condition {
	abstract public ArrayList<String> getAllNodes();

	public ArrayList<SimpleCondition> getSimpleConditions() {
		return new ArrayList<SimpleCondition>();
	}

	public ArrayList<BlockCondition> getBlockConditions() {
		return new ArrayList<BlockCondition>();
	}
}
