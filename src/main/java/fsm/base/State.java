package fsm.base;

import java.util.*;
import java.util.function.Function;

public abstract class State<E extends Enum<E>> {
	protected State<E> parent;
	protected List<State<E>> parallelSubstates;
	protected List<State<E>> pausedSubstates;
	protected Set<E> events;
	protected Map<E, Function<Integer, EventConsumption>> transitions;
	private boolean isPaused;	// is a substate and its superstate has been exited explicitly
	private boolean ranEntryAction;

	public State(Class<E> classEvent) {
		this(null, classEvent);
	}

	public State(State<E> other, Class<E> eventType) {
		if(other != null) {
			parent = other.parent;
			parallelSubstates = new ArrayList<>();
			events = other.events;
			transitions = other.transitions;
			pausedSubstates = new ArrayList<>();
		}
		else {
			parent = null;
			parallelSubstates = new ArrayList<>();
			pausedSubstates = new ArrayList<>();
			events = EnumSet.allOf(eventType);
			transitions = new HashMap<>();
		}
		isPaused = false;
		init();
	}

	protected void init() {
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

	protected EventConsumption ENTER(State<E> newState) {
		for(var substate : parallelSubstates) {
			substate.runExitAction();
		}
		runExitAction();
		if(parent != null) {
			if(this instanceof Superstate<E>) {
				pause();
				parent.pausedSubstates.add(this);
			}
			parent.parallelSubstates.remove(this);
			parent.parallelSubstates.add(newState);
			// newState.parent = parent;
			// parent = null;
		}
		// pauseSubstates();
		newState.runEntryAction();
		for(var substate : newState.parallelSubstates) {
			substate.runEntryAction();
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption ENTER_DEEP(State<E> superstate) {
		boolean deepHistoryFound = false;
		if(parent != null) {
			for(var substate : parent.pausedSubstates) {
				if(superstate.getClass().equals(substate.getClass())) {
					parent.unpauseSubstates();
					deepHistoryFound = true;
					break;
				}
			}
		}
		if(!deepHistoryFound) {
			return ENTER(superstate);
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption EXIT() {
		var ret = EXIT(parent.defaultExit(), true);
		pausedSubstates.clear();
		return ret;
	}

	protected EventConsumption EXIT(State<E> explicitExitState) {
		return EXIT(explicitExitState, false);
	}

	private EventConsumption EXIT(State<E> explicitExitState, boolean runExAc) {
		runExitAction();
		if(parent != null) {
			parent.runExitAction();
			if(parent.parent != null) {
				// parent.parentSwitchSubstate(parent, explicitExitState);
				var pp = parent.parent;
				var list = pp.parallelSubstates;
				list.set(list.indexOf(parent), explicitExitState);
				pp.pausedSubstates.add(parent);
				// parent.parent.pauseSubstates();
			}
		}

		explicitExitState.runEntryAction();
		return EventConsumption.fullyUsed;
	}

	protected State<E> defaultExit() {
		return null;
	}

	private void parentSwitchSubstate(State<E> from, State<E> to) {
		if(parent != null) {
			var list = parent.parallelSubstates;
			list.set(list.indexOf(from), to);
			from.runExitAction();
			to.parent = from.parent;
			from.parent = null;
		}
	}

	private void hardExit() {
		if(parent != null) {
			for(State<E> substate : parallelSubstates) {
				substate.hardExit();
			}
			parallelSubstates.clear();
			parent = null;
		}
		runExitAction();
	}

	private void pause() {
		isPaused = true;
		pauseAction();
	}

	private void pauseSubstates() {
		parallelSubstates.forEach(s -> s.pause());
		pausedSubstates = parallelSubstates;
		parallelSubstates = new ArrayList<>();
	}

	private void unpause() {
		isPaused = false;
		pauseAction();
	}

	private void unpauseSubstates() {
		pausedSubstates.forEach(s -> s.unpause());
		parallelSubstates = pausedSubstates;
		pausedSubstates = new ArrayList<>();
	}

	public List<State<E>> getSubstates() {
		return parallelSubstates;
	}

	protected <T> T get(Class<? extends State<E>> clazz, Class<T> type, String field) {
		for(State<E> state = parent; state != null; state = parent.parent) {
			if(state.getClass().equals(clazz)) {
				try {
					return type.cast(clazz.getField(field).get(state));
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private void runExitAction() {
		exitAction();
	}
	protected void exitAction() {}

	private void runEntryAction() {
		if(!ranEntryAction)
			entryAction();
		ranEntryAction = true;
	}
	protected void entryAction() {}

	protected void pauseAction() {}

	protected void unpauseAction() {}

}