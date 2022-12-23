package fsm.base;

import java.util.*;
import java.util.function.Function;

public abstract class State<E extends Enum<E>> {
	protected State<E> parent;
	protected List<State<E>> parallelSubstates;
	protected Set<E> events;
	protected Map<E, Function<Integer, EventConsumption>> transitions;
	private boolean isPaused;	// is a substate and its superstate has been exited explicitly

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
		isPaused = false;
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
		// parallelSubstates.removeIf(e -> e == null);
		return ret;
	}

	public EventConsumption ENTER(State<E> newState) {
		for(State<E> substate : parallelSubstates) {
			substate.pause();
		}
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

	public EventConsumption EXIT() {
		if(parent != null) {
			// parent.parallelSubstates.remove(this);
			for(State<E> substate : parallelSubstates) {
				substate.EXIT();
			}
			parent = null;
		}
		pause();
		exitAction();
		return EventConsumption.fullyUsed;
	}

	public EventConsumption EXIT(State<E> explicitExitState) {
		// TODO save history
		if(parent != null) {
			// parent.parallelSubstates.remove(this);
			if(parent.parent != null){
				// parent.parent.parallelSubstates.add(explicitExitState);
			}
			for(State<E> substate : parallelSubstates) {
				substate.EXIT();
			}
			// parent = this;
			parent = null;
		}
		// pause();
		exitAction();
		explicitExitState.entryAction();
		return EventConsumption.fullyUsed;
	}

	public State<E> defaultExit() {
		return null;
	}

	private void hardExit() {
		if(parent != null) {
			parent.parallelSubstates.remove(this);
			for(State<E> substate : parallelSubstates) {
				substate.hardExit();
			}
			parallelSubstates.clear();
			parent = null;
		}
		exitAction();
	}

	private void pause() {
		isPaused = true;
	}

	public List<State<E>> getSubstates() {
		return parallelSubstates;
	}

	public void exitAction() {}

	public void entryAction() {}

}