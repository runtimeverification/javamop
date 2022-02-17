package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

import java.util.ArrayList;

public class PartialOrders {
    String name;
    ArrayList<PartialOrder> orders;

    public PartialOrders() {
        this.name = null;
        this.orders = new ArrayList<PartialOrder>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PartialOrder> getOrders() {
        return orders;
    }

    public void add(PartialOrder po) {
        orders.add(po);
    }

}
