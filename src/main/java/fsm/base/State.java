package fsm.base;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class State<E extends Enum<E>> {
	protected State<E> parent;
	protected List<State<E>> parallelSubstates;
	protected List<State<E>> pausedSubstates;
	protected Set<E> events;
	protected Map<E, Function<Integer, EventConsumption>> transitions;
	private boolean isPaused; // is a substate and its superstate has been exited explicitly
	private boolean pauseActionIsExitAction;
	private boolean unpauseActionIsEntryAction;

	public State(State<E> other, Class<E> eventType) {
		if (other != null) {
			parent = other.parent;
			parallelSubstates = new ArrayList<>();
			events = other.events;
			transitions = other.transitions;
			pausedSubstates = new ArrayList<>();
			other.copyActionConfigTo(this);
		} else {
			parent = null;
			parallelSubstates = new ArrayList<>();
			pausedSubstates = new ArrayList<>();
			events = EnumSet.allOf(eventType);
			transitions = new HashMap<>();
		}
		isPaused = false;
		init();
	}

	private void init() {
		for (E event : events)
			transitions.put(event, (payload -> EventConsumption.unused));
	}

	/**
	 * Hands down the event to most low level substate. Are multiple parallel
	 * substates present, only the first receives the event
	 * 
	 * @param event
	 * @return
	 */
	public EventConsumption handleEvent(E event) {
		var ret = _handleEvent(event);
		verify();
		return ret;
	}

	public EventConsumption _handleEvent(E event) {
		EventConsumption ret = EventConsumption.unused;
		for (State<E> state : parallelSubstates) {
			if (ret == EventConsumption.fullyUsed) { // ret may be altered by recursive calls since they same the share
														// EventConsumption instance
				break;
			}
			if (state.parallelSubstates.isEmpty() || (ret = state.handleEvent(event)) != EventConsumption.fullyUsed) {
				ret = state.transitions.get(event).apply(0);
			}
		}
		parallelSubstates.removeIf(state -> state == null);
		return ret;
	}

	private void verify() throws IllegalStateException {
		for (State<E> state : parallelSubstates) {
			if(state.parent != this) {
				throw new IllegalStateException("State " + state.getName() + " has parent " + state.parent.getName() + " but should have " + getName());
			}
			state.verify();
		}
	}

	public String getName() {
		return getClass().toString();
	}

	protected void TRANSITION(E event, Function<Integer, EventConsumption> func) {
		transitions.put(event, func);
	}

	protected EventConsumption ENTER(State<E> newState) {
		copyActionConfigTo(newState);
		if (parent != null) {
			parentSwitchSubstate(this, newState, this instanceof Superstate<E>);
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption APPEND(State<E> appendState) {
		return APPEND(List.of(appendState));
	}

	protected EventConsumption APPEND(List<State<E>> appendStates) {
		parallelSubstates.addAll(appendStates);
		for (var substate : appendStates) {
			substate.parent = this;
			copyActionConfigTo(substate);
			substate.runEntryActionRecurse();
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption ENTER_SHALLOW(State<E> stateWithHistory) {
		return enterHistory(stateWithHistory, true);
	}

	protected EventConsumption ENTER_DEEP(State<E> stateWithHistory) {
		return enterHistory(stateWithHistory, false);
	}

	private EventConsumption enterHistory(State<E> stateWithHistory, boolean shallow) {
		copyActionConfigTo(stateWithHistory);
		pauseSubstates();
		State<E> historyState = null;
		if (parent != null) {
			for (var substate : parent.pausedSubstates) {
				if (stateWithHistory.getClass().equals(substate.getClass())) {
					historyState = substate;
					break;
				}
			}
		}
		if (historyState != null) {
			parent.pausedSubstates.remove(historyState);
			if (shallow) {
				for (var substate : historyState.pausedSubstates) {
					substate.pausedSubstates = new ArrayList<>();
				}
			}
			parentSwitchSubstate(this, historyState, this instanceof Superstate<E>);
			if (shallow) {
				for (var substate : historyState.parallelSubstates) {
					substate.APPEND(substate.defaultEntry());
				}
			}
		} else {
			return ENTER(stateWithHistory);
		}
		return EventConsumption.fullyUsed;
	}

	protected EventConsumption EXIT() {
		var exitTo = parent.defaultExit();
		if (exitTo == null) {
			return parent.EXIT();
		} else if (parent.parallelSubstates.size() > 1) {
			var subs = parent.parallelSubstates;
			subs.set(subs.indexOf(this), null);
			runExitActionRecurse();
			return EventConsumption.fullyUsed;
		}
		return EXIT(exitTo, true);
	}

	protected EventConsumption EXIT(State<E> explicitExitState) {
		return EXIT(explicitExitState, false);
	}

	private EventConsumption EXIT(State<E> explicitExitState, boolean execExitAction) {
		copyActionConfigTo(explicitExitState);
		parent.parentSwitchSubstate(parent, explicitExitState, !execExitAction);
		return EventConsumption.fullyUsed;
	}

	protected List<State<E>> defaultEntry() {
		return List.of();
	}

	protected State<E> defaultExit() {
		return null;
	}

	private void parentSwitchSubstate(State<E> from, State<E> to, boolean pauseOldState) {
		if (parent != null) {
			var subs = parent.parallelSubstates;
			subs.set(subs.indexOf(from), to);
			if (pauseOldState) {
				parent.pausedSubstates.add(from);
				from.pauseSubstates();
				from.pause();
			} else {
				from.runExitActionRecurse();

			}
		}
		if (to.isPaused) {
			to.unpause();
			to.unpauseSubstates();
		} else {
			to.runEntryActionRecurse();
		}
	}

	private void pauseSubstates() {
		parallelSubstates.forEach(s -> s.pauseSubstates());
		parallelSubstates.forEach(s -> s.pause());
		pausedSubstates = parallelSubstates;
		parallelSubstates = new ArrayList<>();
	}

	private void unpauseSubstates() {
		pausedSubstates.forEach(s -> s.unpause());
		pausedSubstates.forEach(s -> s.unpauseSubstates());
		parallelSubstates = pausedSubstates;
		pausedSubstates = new ArrayList<>();
	}

	public List<State<E>> getSubstates() {
		return parallelSubstates;
	}

	public State<E> getSubstate(int nSub) {
		return parallelSubstates.get(nSub);
	}

	protected <T> T get(Class<? extends State<E>> clazz, Class<T> type, String field) {
		for (State<E> state = parent; state != null; state = parent.parent) {
			if (state.getClass().equals(clazz)) {
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

	// pkg private for access only in Superstate
	void copyActionConfigTo(State<E> other) {
		if (other != null) {
			other.setPauseActionIsExitAction(pauseActionIsExitAction);
			other.setUnpauseActionIsEntryAction(unpauseActionIsEntryAction);
		}
	}

	private void runExitActionRecurse() {
		for (var substate : parallelSubstates) {
			substate.runExitActionRecurse();
		}
		runExitAction();
	}

	private void runExitAction() {
		exitAction();
	}

	private void runEntryActionRecurse() {
		runEntryAction();
		for (var substate : parallelSubstates) {
			substate.runEntryActionRecurse();
		}
	}

	private void runEntryAction() {
		entryAction();
	}

	private void pause() {
		if (pauseActionIsExitAction)
			exitAction();
		else
			pauseAction();
		isPaused = true;
	}

	private void unpause() {
		if (unpauseActionIsEntryAction) {
			entryAction();
		} else {
			unpauseAction();
		}
		isPaused = false;
	}

	protected void setPauseActionIsExitAction(boolean b) {
		pauseActionIsExitAction = b;
	}

	protected void setUnpauseActionIsEntryAction(boolean b) {
		unpauseActionIsEntryAction = b;
	}

	protected void entryAction() {
	}

	protected void exitAction() {
	}

	protected void pauseAction() {
	}

	protected void unpauseAction() {
	}
}