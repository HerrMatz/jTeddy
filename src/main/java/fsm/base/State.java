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
	private boolean pauseActionIsExitAction;
	private boolean unpauseActionIsEntryAction;

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
			pauseActionIsExitAction = other.pauseActionIsExitAction;
			unpauseActionIsEntryAction = other.unpauseActionIsEntryAction;
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

	protected void TRANSITION(E event, Function<Integer, EventConsumption> func) {
		transitions.put(event, func);
	}

	protected EventConsumption ENTER(State<E> newState) {
		newState.setPauseActionIsExitAction(pauseActionIsExitAction);
		newState.setUnpauseActionIsEntryAction(unpauseActionIsEntryAction);
		for(var substate : parallelSubstates) {
			// substate.runExitAction();
			substate.pause();
		}
		// runExitAction();
		if(parent != null) {
			parentSwitchSubstate(this, newState, this instanceof Superstate<E>);
		}
		// pauseSubstates();
		newState.runEntryAction();
		for(var substate : newState.parallelSubstates) {
			substate.runEntryAction();
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption ENTER_DEEP(State<E> stateWithHistory) {
		stateWithHistory.setPauseActionIsExitAction(pauseActionIsExitAction);
		stateWithHistory.setUnpauseActionIsEntryAction(unpauseActionIsEntryAction);
		for(var substate : parallelSubstates) {
			// substate.runExitAction();
			substate.pause();
		}
		runExitAction();
		boolean deepHistoryFound = false;
		if(parent != null) {
			for(var substate : parent.pausedSubstates) {
				if(stateWithHistory.getClass().equals(substate.getClass())) {
					parent.unpauseSubstates();
					deepHistoryFound = true;
					break;
				}
			}
		}
		if(!deepHistoryFound) {
			return ENTER(stateWithHistory);
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption EXIT() {
		return EXIT(parent.defaultExit(), true);
	}

	protected EventConsumption EXIT(State<E> explicitExitState) {
		return EXIT(explicitExitState, false);
	}

	private EventConsumption EXIT(State<E> explicitExitState, boolean execExitAction) {
		explicitExitState.setPauseActionIsExitAction(pauseActionIsExitAction);
		explicitExitState.setUnpauseActionIsEntryAction(unpauseActionIsEntryAction);
		runExitAction();
		parent.parentSwitchSubstate(parent, explicitExitState, !execExitAction);
		explicitExitState.runEntryAction();
		return EventConsumption.fullyUsed;
	}

	protected State<E> defaultExit() {
		return null;
	}

	private void parentSwitchSubstate(State<E> from, State<E> to, boolean pauseOldState) {
		if(parent != null) {
			var list = parent.parallelSubstates;
			list.set(list.indexOf(from), to);
			if(pauseOldState) {
				parent.pausedSubstates.add(from);
				from.pause();
			}
			else {
				from.runExitAction();
			}
		}
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

	private void runEntryAction() {
		if(!ranEntryAction)
			entryAction();
		ranEntryAction = true;
	}

	private void pause() {
		if(pauseActionIsExitAction)
			exitAction();
		else
			pauseAction();
		isPaused = true;
		// parallelSubstates.forEach(s -> s.pause());
	}

	private void unpause() {
		if(unpauseActionIsEntryAction) {
			entryAction();
		}
		else {
			unpauseAction();
		}
		isPaused = false;
		parallelSubstates.forEach(s -> s.unpause());
	}

	protected void setPauseActionIsExitAction(boolean b) {
		pauseActionIsExitAction = b;
	}

	protected void setUnpauseActionIsEntryAction(boolean b) {
		unpauseActionIsEntryAction = b;
	}
	protected boolean getPauseActionIsExitAction() {
		return pauseActionIsExitAction;
	}

	protected boolean getUnpauseActionIsEntryAction() {
		return unpauseActionIsEntryAction;
	}

	protected void entryAction() {}
	protected void exitAction() {}
	protected void pauseAction() {}
	protected void unpauseAction() {}
}