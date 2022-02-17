/*
 * FSM Coenable Sets
 *
 * author: Dongyun Jin
 *
 */

package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.*;

/**
 *
 */
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

    /**
     * Construct the Finite State Machine representation and generate the monitor coenable information.
     *
     * @param startState The Finite State Machine initialization state.
     * @param events All the symbols in the Finite State Machine alphabet.
     * @param states All the states in the Finite State Machine.
     * @param categories The Finite State Machine states of user interest.
     * @param aliases A mapping between equivalent states.
     * @param stateMap A mapping from states to their transition mapping.
     */
    public FSMCoenables(State startState, ArrayList<Symbol> events, ArrayList<State> states, ArrayList<State> categories, HashMap<State, HashSet<State>> aliases,
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

    /**
     * Construct the internal rechabable state list with respect to the
     * starting state.
     */
    private void computeRechability() throws LogicException {
        computeReachability(this.startState);
    }

    /**
     * Augment the internal reachable state list with respect to the given
     * state.
     * @param state The state to search for reachability from.
     */
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

    /**
     * Compute all the coenable information accross the entire graph.
     */
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

    /**
     * Compute the coenable information for a particular state.
     * @param category The category the state is in.
     * @param state The state to compute the information for.
     * @param path The symbols already encountered on the current path.
     * @param eventsSeen Storage of already processed information to avoid infinite looping.
     */
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

    /**
     * Check if the given state belongs to the given category.
     * @param state The state to check the category for.
     * @param category The category to check if the state is in.
     * @return Whether the state is in the category.
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

    /**
     * Construct a state transition map where all state transitions are reversed. This means that
     * transitions will map to a set of possible states, as multiple states in the original map
     * can transition to the same state with the same symbol.
     * @param stateMap The original mapping from all states to their outgoing transitions.
     * @return The reverse of {@code stateMap} where destinations now map to a set of sources.
     */
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

    /**
     * Transforms the given stateMap into a stateMap where transitions are
     * fully defined.
     * @param stateMap All outgoing state transitions from all states, possibly missing transitions.
     * @return All outgoing state transitions from all states, with transitions on all symbols.
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

    /**
     * Transform a set of transitions into a map from event to next state. It
     * removes the default transition and flattens it into normal transitions.
     * @param transitions The outgoing transitions from a state. May have missing symbols or a default transition.
     * @return A transition mapping with transitions for every symbol.
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

    /**
     * Prints out coenable sets for all categories.
     * @return A formatted string with the coenable sets.
     */
    @Override
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
    
    /**
     * The model coenable information.
     * @return The calculated model coenable information.
     */
    public HashMap<State, HashMap<Symbol, HashSet<HashSet<Symbol>>>> getCoenables() {
        return coenables;
    }
}
