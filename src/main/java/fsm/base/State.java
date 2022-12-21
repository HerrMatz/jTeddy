package fsm.base;

import java.util.*;
import java.util.function.Function;

public abstract class State<E extends Enum<E>> {
	protected State<E> parent;
	protected List<State<E>> parallelSubstates;
	protected Set<E> events;
	protected Map<E, Function<Integer, EventConsumption>> transitions;

	public State(Class<E> classEvent) {
		this(null, classEvent);
	}

	public State(State<E> other, Class<E> eventType) {
		if(other != null){
			parent = other.parent;
			parallelSubstates = other.parallelSubstates;
			events = other.events;
			transitions = other.transitions;
		}
		else {
			parent = null;
			parallelSubstates = new ArrayList<>();
			events = EnumSet.allOf(eventType);
			transitions = new HashMap<>();
		}
		init();
	}

	public void init() {
		for(E event : events)
			transitions.put(event, (payload -> EventConsumption.unused));
	}

	public EventConsumption handleEvent(E event) {
		EventConsumption ret = EventConsumption.unused;
		for(State<E> state : parallelSubstates) {
			if(state.parallelSubstates.isEmpty() || (ret = state.handleEvent(event)) != EventConsumption.fullyUsed){
				ret = state.transitions.get(event).apply(0);
			}
		}
		return ret;
	}

	// private EventConsumption distributeEvent(E event,)

	public void exitAction() {

	}

	public void entryAction() {

	}

	// public void enter(State<E> newState, Class<E> t) {
	// 	init(t);
	// 	self.exitAction();
	// 	self = newState;
	// 	self.entryAction();
	// }

	public EventConsumption ENTER(State<E> newState) {
		if(parent != null) {
			parent.parallelSubstates.remove(this);
			parent.parallelSubstates.add(newState);
			newState.parent = parent;
			parent = null;
		}
		exitAction();
		newState.entryAction();
		return EventConsumption.fullyUsed;
	}

	public List<State<E>> getSubstates() {
		return parallelSubstates;
	}

}