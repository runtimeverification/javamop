package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

import java.util.ArrayList;

public class ANDCondition extends Condition{
	Condition con1, con2;
	
	public ANDCondition(Condition con1, Condition con2){
		this.con1 = con1;
		this.con2 = con2;
	}

	public Condition getCon1() {
		return con1;
	}

	public void setCon1(Condition con1) {
		this.con1 = con1;
	}

	public Condition getCon2() {
		return con2;
	}

	public void setCon2(Condition con2) {
		this.con2 = con2;
	}

	public ArrayList<String> getAllNodes(){
		ArrayList<String> ret = new ArrayList<String>();

		ret.addAll(con1.getAllNodes());
		ret.addAll(con2.getAllNodes());
		
		return ret;
	}

}
