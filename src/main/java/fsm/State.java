package fsm;

import java.util.*;
import java.util.function.Function;

public abstract class State {
	// StateTree substates;
	State self;
	State parent;
	List<State> parallelSubstates;
	Map<Event, Function<Integer, EventConsumption>> transitions;

	public State() {
		self = this;
		parent = null;
		parallelSubstates = new ArrayList<>();
		transitions = new HashMap<>();
		// init();
	}

	public State(State other) {
		self = other.self;
		parent = other.parent;
		parallelSubstates = other.parallelSubstates;
		transitions = other.transitions;
	}

	public void init() {
		for(Event event : Event.values())
			transitions.put(event, (payload -> EventConsumption.unused));
	}

	public EventConsumption handleEvent(Event event) {
		EventConsumption ret = self.transitions.get(event).apply(0);
		if(ret != EventConsumption.fullyUsed) {
			for(State substate : parallelSubstates) {
				ret = substate.transitions.get(event).apply(0);
				if(ret == EventConsumption.fullyUsed) {
					return EventConsumption.fullyUsed;
				}
			}
		}
		return ret;
	}

	public void exitAction() {

	}

	public void entryAction() {

	}

	public void enter(State newState) {
		init();
		self.exitAction();
		self = newState;
		self.entryAction();
	}

	public EventConsumption ENTER(State newState) {
		init();
		self.exitAction();
		self = newState;
		self.entryAction();
		return EventConsumption.fullyUsed;
	}

	public State getState() {
		return self;
	}

	public List<State> getSubstates() {
		return parallelSubstates;
	}

	public State copy() {
		return null;
	}


}