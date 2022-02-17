/*
 * FSM Coenable Sets
 * 
 * author: Dongyun Jin
 * 
 */

package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.*;

public class FSMCoenables {
	private static State fail;

	static {
		fail = State.get("fail");
	}

	private State startState;
	private ArrayList<Symbol> events;
	private ArrayList<State> states;
	private ArrayList<State> fullstates;
	private ArrayList<State> categories;
	private HashMap<State, HashSet<State>> aliases;
	private HashMap<State, Transition> stateMap;
	private HashMap<State, Transition> fullStateMap;

	private HashMap<State, HashMap<Symbol, HashSet<State>>> inversedStateMap;
	private HashMap<State, HashMap<Symbol, HashSet<HashSet<Symbol>>>> coenables;

	private HashSet<State> reachableStates;

	FSMCoenables(State startState, ArrayList<Symbol> events, ArrayList<State> states, ArrayList<State> categories, HashMap<State, HashSet<State>> aliases,
			HashMap<State, Transition> stateMap) throws LogicException {
		this.startState = startState;
		this.events = events;
		this.states = states;
		this.categories = categories;
		this.aliases = aliases;
		this.stateMap = stateMap;

		this.fullstates = new ArrayList<State>();
		this.fullstates.addAll(this.states);
		if(!this.fullstates.contains(fail))
			this.fullstates.add(fail);

		this.reachableStates = new HashSet<State>();

		this.coenables = new HashMap<State, HashMap<Symbol, HashSet<HashSet<Symbol>>>>();
		for (State category : this.categories) {
			HashMap<Symbol, HashSet<HashSet<Symbol>>> categoryCoenable = new HashMap<Symbol, HashSet<HashSet<Symbol>>>();

			for (Symbol event : this.events) {
				categoryCoenable.put(event, new HashSet<HashSet<Symbol>>());
			}
			this.coenables.put(category, categoryCoenable);
		}

		this.fullStateMap = makeFullStateMap(this.stateMap);
		this.inversedStateMap = inverseStateMap(this.fullStateMap);

		computeRechability();
		computeCoenables();
	}

	private void computeRechability() throws LogicException {
		computeReachability(this.startState);
	}

	private void computeReachability(State state) throws LogicException {
		this.reachableStates.add(state);

		Transition transitions = this.fullStateMap.get(state);

		for (Symbol event : this.events) {
			State destination = transitions.get(event);

			if (!this.reachableStates.contains(destination)) {
				computeReachability(destination);
			}
		}
	}

	private void computeCoenables() throws LogicException {
		HashMap<State, HashSet<HashSet<Symbol>>> eventsSeen = new HashMap<State, HashSet<HashSet<Symbol>>>();
		for (State state : this.fullstates) {
			eventsSeen.put(state, new HashSet<HashSet<Symbol>>());
		}

		for (State category : this.categories) {
			for (State state : this.fullstates) {
				if (isStateInCategory(state, category)) {
					computeCoenables(category, state, new HashSet<Symbol>(), eventsSeen);
				}
			}
		}
	}

	private void computeCoenables(State category, State state, HashSet<Symbol> path, HashMap<State, HashSet<HashSet<Symbol>>> eventsSeen) throws LogicException {
		eventsSeen.get(state).add(path);

		HashMap<Symbol, HashSet<State>> transitions = this.inversedStateMap.get(state);

		if (transitions == null)
			throw new LogicException("inversedStateMap should be full state map");

		for (Symbol event : this.events) {
			HashSet<State> destinations = transitions.get(event);

			if (destinations == null)
				throw new LogicException("inversedStateMap should be full state map");

			for (State destination : destinations) {
				if (this.reachableStates.contains(destination) && path.size() != 0) {
					this.coenables.get(category).get(event).add(path);
				}
				HashSet<Symbol> newPath = new HashSet<Symbol>(path);
				newPath.add(event);
				if (!eventsSeen.get(destination).contains(newPath))
					computeCoenables(category, destination, newPath, eventsSeen);
			}
		}
	}

	/*
	 * ================================================================
	 * auxiliary functions
	 */

	/*
	 * Check if the given state belongs to the given category
	 */
	private boolean isStateInCategory(State state, State category) {
		if (this.aliases.containsKey(category)) {
			HashSet<State> aliasedStates = this.aliases.get(category);
			if (aliasedStates.contains(state))
				return true;
			else
				return false;
		} else {
			return state == category;
		}
	}

	private HashMap<State, HashMap<Symbol, HashSet<State>>> inverseStateMap(HashMap<State, Transition> stateMap) throws LogicException {
		HashMap<State, HashMap<Symbol, HashSet<State>>> ret = new HashMap<State, HashMap<Symbol, HashSet<State>>>();

		for (State state : this.fullstates) {
			HashMap<Symbol, HashSet<State>> transitions = new HashMap<Symbol, HashSet<State>>();
			for (Symbol event : this.events) {
				HashSet<State> destinations = new HashSet<State>();
				transitions.put(event, destinations);
			}
			ret.put(state, transitions);
		}

		for (State state : this.fullstates) {
			Transition transitions = stateMap.get(state);

			if (transitions == null)
				throw new LogicException("input of inverseStateMap should be full state map");

			for (Symbol event : this.events) {
				State destination = transitions.get(event);

				if (destination == null)
					throw new LogicException("input of inverseStateMap should be full state map");

				HashMap<Symbol, HashSet<State>> retTransitions = ret.get(destination);
				HashSet<State> retDestinations = retTransitions.get(event);
				retDestinations.add(state);
			}
		}

		return ret;
	}

	/*
	 * It transforms the given stateMap into a stateMap where transitions are
	 * fully defined.
	 */
	private HashMap<State, Transition> makeFullStateMap(HashMap<State, Transition> stateMap) throws LogicException {
		HashMap<State, Transition> ret = new HashMap<State, Transition>();

		for (State state : stateMap.keySet()) {
			Transition transitions = stateMap.get(state);
			
			Transition fullTransitions = getFullTransition(transitions);
			ret.put(state, fullTransitions);
		}

		for (State state : this.fullstates) {
			if (ret.get(state) == null) {
				Transition transitions = new Transition();
				for (Symbol event : this.events) {
					transitions.put(event, fail);
				}
				ret.put(state, transitions);
			}
		}

		return ret;
	}

	/*
	 * Transform a set of transitions into a map from event to next state. It
	 * removes the default transition and flatten it into normal transitions.
	 */
	private Transition getFullTransition(Transition transitions) throws LogicException {
		Transition fullTransition = new Transition();

		boolean hasDefaultTransition = false;
		State defaultDestination = null;

		for (Symbol event : transitions.keySet()) {
			State destination = transitions.get(event);
			if (destination == null)
				throw new LogicException("No destination in a transition");

			if (event != null) {
				fullTransition.put(event, destination);
			} else {
				if (hasDefaultTransition)
					throw new LogicException("Multiple Default Transitions");
				hasDefaultTransition = true;
				defaultDestination = destination;
			}
		}

		for (Symbol event : this.events) {
			if (fullTransition.get(event) == null) {
				if (hasDefaultTransition) {
					fullTransition.put(event, defaultDestination);
				} else {
					fullTransition.put(event, fail);
				}
			}
		}

		return fullTransition;
	}

	/*
	 * Prints out coenable sets for all categories
	 */
	public String toString() {
		String output = "";
		for (State category : categories) {
			if (coenables.get(category) != null) {
				output += "// " + category + " Coenables\n";
				output += coenables.get(category).toString() + "\n";
			}
		}
		return output;
	}
}
