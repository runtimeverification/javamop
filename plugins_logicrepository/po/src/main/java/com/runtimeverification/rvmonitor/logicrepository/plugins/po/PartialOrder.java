package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

public class PartialOrder {
	private Condition condition;
	private String event;
	private boolean check = false;

	public PartialOrder(Condition condition, String event) {
		this.condition = condition;
		this.event = event;
	}

	public PartialOrder(Condition condition, String event, boolean check) {
		this.condition = condition;
		this.event = event;
		this.check = check;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
	public boolean getCheck(){
		return check;
	}
	
	public void setCheck(boolean check){
		this.check = check;
	}

}
