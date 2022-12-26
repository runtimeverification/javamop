package com.runtimeverification.rvmonitor.logicrepository.plugins.po;

import java.io.ByteArrayOutputStream;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.po.parser.POParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.po.parser.ParseException;

public class POPlugin extends LogicPlugin {

	public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {

		String logic = logicInputXML.getProperty().getLogic();
		logic = logic.toUpperCase();

		if (!logic.equals("PO")) {
			throw new LogicException("incorrect logic type: " + logic);
		}

		String poStr = logicInputXML.getProperty().getFormula();

		PartialOrders partialOrders;
		try {
			partialOrders = POParser.parse(poStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new LogicException(e.getMessage());
		}

		LogicRepositoryType logicOutputXML = logicInputXML;
		logicOutputXML.getMessage().add("done");

		return logicOutputXML;
	}

/*	private boolean checkValid(PartialOrders partialOrders) {
		HashSet<String> nodes = new HashSet<String>();

		for (PartialOrder po : partialOrders.getOrders()) {
			nodes.addAll(po.getCondition().getAllNodes());
			nodes.add(po.getEvent());
		}

		return checkValid(partialOrders, nodes);
	}

	private boolean checkValid(PartialOrders partialOrders, HashSet<String> nodes) {
		for (String node : nodes) {
			HashSet<String> reachableNodes = reachability(partialOrders, node);
			if (reachableNodes.contains(node))
				return false;
		}
		return true;
	}

	private HashSet<String> reachability(PartialOrders partialOrders, String node) {
		HashSet<String> ret = new HashSet<String>();

		for (PartialOrder po : partialOrders.getOrders()) {
			if (po.getBefore().equals(node))
				ret.add(spo.getAfter());
		}
		for (BlockPartialOrder bpo : partialOrders.getBlockOrders()) {
			if (bpo.getBefore().equals(node)) {
				ret.add(bpo.getAfter());
				ret.add(bpo.getBlock());
			}
		}

		int lastsize;
		do {
			lastsize = ret.size();

			for (String node2 : ret) {
				for (SimplePartialOrder spo : partialOrders.getSimpleOrders()) {
					if (spo.getBefore().equals(node2))
						ret.add(spo.getAfter());
				}
				for (BlockPartialOrder bpo : partialOrders.getBlockOrders()) {
					if (bpo.getBefore().equals(node2)) {
						ret.add(bpo.getAfter());
						ret.add(bpo.getBlock());
					}
				}
			}
		} while (lastsize != ret.size());

		return ret;
	}
*/
	static protected POPlugin plugin = new POPlugin();

	public static void main(String[] args) {

		try {
			// Parse Input
			LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);

			// use plugin main function
			if (plugin == null) {
				throw new LogicException("Each plugin should initiate plugin field.");
			}
			LogicRepositoryType logicOutputXML = plugin.process(logicInputData.getXML());

			if (logicOutputXML == null) {
				throw new LogicException("no output from the plugin.");
			}

			ByteArrayOutputStream logicOutput = new LogicRepositoryData(logicOutputXML).getOutputStream();
			System.out.println(logicOutput);
		} catch (LogicException e) {
			System.out.println(e);
		}

	}
}
