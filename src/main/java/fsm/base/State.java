package fsm.base;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * E = Input alphabet (enum),
 * P = Payload type (will be handed to transitions),
 * C = Context data type
 */
public abstract class State<E extends Enum<E>, P, C> {
	protected State<E, P, C> parent;
	protected List<State<E, P, C>> parallelSubstates; // active substates
	protected List<State<E, P, C>> pausedSubstates; // paused substates (left by explicit exit)
	protected Set<E> events;
	protected Map<E, Function<P, EventConsumption>> transitions;
	protected C contextData;
	private boolean isPaused; // is a substate and its superstate has been exited explicitly
	private boolean pauseActionIsExitAction; // true -> on explicit exit, exit action will be called, otherwise pause
												// action
	private boolean unpauseActionIsEntryAction; // true -> on entry of a superstate, entry action will be called,
												// otherwise unpause action

	/**
	 * @param from             Original state that this new state replaces. If null,
	 *                         the old state will remain intact.
	 * @param eventClass       Class of the event type this state handles
	 * @param makeDefaultEntry
	 */
	protected State(State<E, P, C> other, Class<E> eventClass) {
		// copy relevant data from state
		if (other != null) {
			parent = other.parent;
			parallelSubstates = new ArrayList<>();
			events = other.events;
			transitions = other.transitions;
			pausedSubstates = new ArrayList<>();
			other.copyRelevantDataTo(this);
		}
		// no state to take over from
		else {
			parent = null;
			parallelSubstates = new ArrayList<>();
			pausedSubstates = new ArrayList<>();
			events = EnumSet.allOf(eventClass);
			transitions = new HashMap<>();
			pauseActionIsExitAction = true;
			unpauseActionIsEntryAction = true;
		}
		isPaused = false;

		// initialise transitions, will be filled by actual state constructor
		for (E event : events)
			transitions.put(event, (payload -> EventConsumption.unused));
	}

	/**
	 * Hands down the event to most low level substate. Are multiple parallel
	 * substates present, only the first receives the event. If the substate does
	 * not use the event fully (returns EventConsumption.fullyUsed), it is handed to
	 * the next parallel substate. Are no further parallel substates present, the
	 * event is handed back to the states parent which in turn may attempt to use it
	 * and so on.
	 * 
	 * @param event Event with custom payload
	 * @return EventConsumption.fullyUsed unless otherwise specified in user defined
	 *         transition
	 */
	public EventConsumption handleEvent(BaseEvent<E, P> event) {
		var ret = _handleEvent(event);
		verify();
		return ret;
	}
	public EventConsumption handleEvent(E event) {
		return handleEvent(new BaseEvent<E, P>(event, null));
	}

	/**
	 * Returns the name of the dynamic type
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().toString();
	}

	/**
	 * @return The context data. Shared by all states in the state machine
	 */
	public C getContextData() {
		return contextData;
	}

	/**
	 * @return A list of all parallel substates
	 */
	public List<State<E, P, C>> getSubstates() {
		return parallelSubstates;
	}

	/**
	 * Use 0 for regular states
	 * 
	 * @param nSub Index of requested parallel substate
	 * @return Requested substate
	 */
	public State<E, P, C> getSubstate(int nSub) {
		return parallelSubstates.get(nSub);
	}

	/**
	 * For use in topmost superstate ("context") constructor only. Starts this state
	 * machine by calling initState's entry action
	 * 
	 * @param initState Initial state of the state machine
	 */
	protected void start(State<E, P, C> initState, C data) {
		contextData = data;
		copyRelevantDataTo(initState);
		parallelSubstates.add(initState);
		initState.parent = this;
		initState.entryAction();
	}

	/**
	 * Adds a transition to the current state.
	 * 
	 * @param event Event that triggers the transition
	 * @param func  Callback that will be called upon event arrival
	 */
	protected void TRANSITION(E event, Function<P, EventConsumption> func) {
		transitions.put(event, func);
	}

	/**
	 * For a regular state change (state to state, state to superstate with default
	 * entry, superstate to state with default exit, superstate to superstate with
	 * both default exit and entry)
	 * 
	 * @param newState State that will replace this one
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption ENTER(State<E, P, C> newState) {
		copyRelevantDataTo(newState);
		newState.makeDefaultEntry();
		if (parent != null) {
			parentSwitchSubstate(this, newState, isSuperstate());
		}
		return EventConsumption.fullyUsed;
	}

	/**
	 * For explicit entry of a superstate
	 * @param superState Superstate to enter
	 * @param explicitEntryState Explicit substate of superState to enter
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption ENTER_EXPLICIT(State<E, P, C> superState, State<E, P, C> explicitEntryState) {
		copyRelevantDataTo(superState);
		copyRelevantDataTo(explicitEntryState);
		explicitEntryState.parent = superState;
		explicitEntryState.makeDefaultEntry();
		superState.parallelSubstates.add(explicitEntryState);
		if (parent != null) {
			parentSwitchSubstate(this, superState, isSuperstate());
		}
		return EventConsumption.fullyUsed;
	}

	/**
	 * Appends the given state to this states parallel substates and calls its entry
	 * action. For dynamically adding more paralles superstates. Not defined in UML.
	 * 
	 * @param appendState State to be appended
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption APPEND(State<E, P, C> appendState) {
		return APPEND(List.of(appendState));
	}

	/**
	 * Enters a superstate's shallow history if present, otherwise uses its default
	 * entry.
	 * For superstates that only contain one sub-fsm, the sub-fsm's top level state
	 * default entry will be performed.
	 * For superstates that contain multiple parallel superstates, each superstate's
	 * default entry will be performed.
	 * Thusly, the parallel substates' old state configuration will not be retained.
	 * Calls entry- or unpause action of reactivated states according to
	 * configuration.
	 * Calls entry action of possible deeper default entry states.
	 * States that are discarded because they are too deep in the history will not
	 * have their exit action performed.
	 * 
	 * @param stateWithHistory State whose shallow history to enter
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption ENTER_SHALLOW(State<E, P, C> stateWithHistory) {
		return enterHistory(stateWithHistory, true);
	}

	/**
	 * Enters a superstate's deep history if present, otherwise uses its default
	 * entry.
	 * The entire superstate's old configuration will be reactivated.
	 * Calls entry- or unpause action of reactivated states according to
	 * configuration.
	 * 
	 * @param stateWithHistory State whose deep history to enter
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption ENTER_DEEP(State<E, P, C> stateWithHistory) {
		return enterHistory(stateWithHistory, false);
	}

	/**
	 * Terminates this sub-fsm. Used if a transition would lead to an end state.
	 * Calls all respective exit actions.
	 * 
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption EXIT() {
		// terminate state machine
		if(parent == null) {
			runExitActionRecurse();
			for(int i = 0; i < parallelSubstates.size(); i++) {
				parallelSubstates.set(i, null);
			}
			for(int i = 0; i < pausedSubstates.size(); i++) {
				pausedSubstates.set(i, null);
			}
			return EventConsumption.fullyUsed;
		}
		var exitTo = parent.defaultExit();
		// parent has no default exit because it is running in parallel
		if (exitTo == null) {
			return parent.EXIT();
		}
		// there are still other substates active, don't use default exit
		else if (parent.parallelSubstates.size() > 1) {
			var subs = parent.parallelSubstates;
			subs.set(subs.indexOf(this), null);
			runExitActionRecurse();
			return EventConsumption.fullyUsed;
		}
		return EXIT(exitTo, true);
	}

	/**
	 * Exits the current superstate to the given explicitExitState
	 * 
	 * @param explicitExitState State to enter explicitly
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	protected EventConsumption EXIT(State<E, P, C> explicitExitState) {
		return EXIT(explicitExitState, false);
	}

	/**
	 * Must be overridden in each superstate. Must be overridden only in
	 * superstates.
	 * Presence of default entry state(s) is the criteria for being a considered a
	 * superstate
	 * 
	 * @return List of states that are entered on this states default entry
	 */
	protected List<? extends State<E, P, C>> defaultEntry() {
		return List.of();
	}

	/**
	 * Should be overridden in each superstate. Should be overridden only in
	 * superstates.
	 * 
	 * @return This state's default exit state
	 */
	protected State<E, P, C> defaultExit() {
		return null;
	}

	/**
	 * Access a parent state's member by reflection. Works on all ancestors as they
	 * are identified by the states class.
	 * Rather hacky, should be avoided.
	 * 
	 * @param <T>   Type of the requested member
	 * @param clazz Class of the respective state
	 * @param type  Class of the requested member
	 * @param field Name of the requested member in the given class
	 * @return Reference to the requested member
	 */
	protected <T> T get(Class<? extends State<E, P, C>> clazz, Class<T> type, String field) {
		for (State<E, P, C> state = parent; state != null; state = parent.parent) {
			if (state.getClass().equals(clazz)) {
				try {
					return type.cast(clazz.getField(field).get(state));
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Set if on pausing states pause- or exit action will be called
	 * 
	 * @param b true -> call exit action on pause, otherwise call pause action on
	 *          pause
	 */
	protected void setPauseActionIsExitAction(boolean b) {
		pauseActionIsExitAction = b;
	}

	/**
	 * Set if on unpausing states unpause- or entry action will be called
	 * 
	 * @param b true -> call entry action on unpause, otherwise call unpause action
	 *          on unpause
	 */
	protected void setUnpauseActionIsEntryAction(boolean b) {
		unpauseActionIsEntryAction = b;
	}

	/**
	 * Will be called when this state is entered
	 */
	protected void entryAction() {
	}

	/**
	 * Will be called when this state is exited
	 */
	protected void exitAction() {
	}

	/**
	 * Will be called when this state is paused (left by explicit exit)
	 */
	protected void pauseAction() {
	}

	/**
	 * Will be called when this state is unpaused (entered by deep or shallow
	 * history)
	 */
	protected void unpauseAction() {
	}

	/**
	 * @return true if this is a superstate, otherwise false. A state is a
	 *         superstate if it has one or more default entry states
	 */
	private boolean isSuperstate() {
		return !defaultEntry().isEmpty();
	}

	/**
	 * @see #handleEvent(Event)
	 */
	private EventConsumption _handleEvent(BaseEvent<E, P> event) {
		EventConsumption ret = EventConsumption.unused;
		for (State<E, P, C> state : parallelSubstates) {
			// ret may be altered by recursive calls since they same the share
			// EventConsumption instance
			if (ret == EventConsumption.fullyUsed) {
				break;
			}
			if (state.parallelSubstates.isEmpty() || (ret = state.handleEvent(event)) != EventConsumption.fullyUsed) {
				ret = state.transitions.get(event.event).apply(event.payload);
			}
		}
		// avoid ConcurrentModificationException by setting invalid state references to
		// null and removing them after finishing iteration
		parallelSubstates.removeIf(state -> state == null);
		return ret;
	}

	/**
	 * Basic sanity check. Verifies recursively that all parents are correctly set
	 * in a substate
	 * 
	 * @throws IllegalStateException if a parent is set incorrectly
	 */
	private void verify() throws IllegalStateException {
		for (State<E, P, C> state : parallelSubstates) {
			if (state.parent != this) {
				throw new IllegalStateException("State " + state + " has parent " + state.parent
						+ " but should have " + this);
			}
			state.verify();
		}
	}

	/**
	 * @see #APPEND(State)
	 */
	private EventConsumption APPEND(List<? extends State<E, P, C>> appendStates) {
		parallelSubstates.addAll(appendStates);
		for (var substate : appendStates) {
			substate.parent = this;
			copyRelevantDataTo(substate);
			substate.makeDefaultEntry();
			substate.runEntryActionRecurse();
		}
		return EventConsumption.fullyUsed;
	}

	/**
	 * Enters this state default entry state(s) recursively.
	 * Does not call entry actions.
	 */
	private void makeDefaultEntry() {
		for (State<E, P, C> sub : defaultEntry()) {
			copyRelevantDataTo(sub);
			parallelSubstates.add(sub);
			sub.parent = this;
			sub.makeDefaultEntry();
		}
	}

	/**
	 * Helper function for ENTER_SHALLOW and ENTER_DEEP
	 * 
	 * @param stateWithHistory State whose history to enter
	 * @param shallow          true -> enter shallow history, otherwise enter deep
	 *                         history
	 * @return EventConsumption.fullyUsed for ease of use in lambdas
	 */
	private EventConsumption enterHistory(State<E, P, C> stateWithHistory, boolean shallow) {
		pauseSubstates();
		State<E, P, C> historyState = null;
		// superstate may enter its own history node
		if(getClass().equals(stateWithHistory.getClass())) {
			pause();
			if(shallow) {
				for (var substate : pausedSubstates) {
					substate.pausedSubstates = new ArrayList<>();
				}
			}
			unpause();
			unpauseSubstates();
			if(shallow) {
				parallelSubstates.forEach(s -> s.makeDefaultEntry());
			}
			return EventConsumption.fullyUsed;
		}
		// Check if there is a history present for the requested state
		else if (parent != null) {
			for (var substate : parent.pausedSubstates) {
				if (stateWithHistory.getClass().equals(substate.getClass())) {
					historyState = substate;
					break;
				}
			}
		}
		// history present
		if (historyState != null) {
			parent.pausedSubstates.remove(historyState);
			// shallow -> remove all states below the "shallow level". Don't call their exit
			// actions
			if (shallow) {
				for (var substate : historyState.pausedSubstates) {
					substate.pausedSubstates = new ArrayList<>();
				}
			}
			parentSwitchSubstate(this, historyState, isSuperstate());
			// shallow -> perform default entries of all parallel substates
			if (shallow) {
				for (var substate : historyState.parallelSubstates) {
					substate.APPEND(substate.defaultEntry());
				}
			}
		}
		// no history present, use default entry
		else {
			return ENTER(stateWithHistory);
		}
		return EventConsumption.fullyUsed;
	}

	/**
	 * @see #EXIT(State)
	 */
	private EventConsumption EXIT(State<E, P, C> explicitExitState, boolean execExitAction) {
		copyRelevantDataTo(explicitExitState);
		parent.parentSwitchSubstate(parent, explicitExitState, !execExitAction);
		return EventConsumption.fullyUsed;
	}

	/**
	 * Helper function for swapping an old and a new state
	 * 
	 * @param from          Old state, pause or exit action is run
	 * @param to            New state, unpause or entry action is run
	 * @param pauseOldState true -> run pause action and add to paused substates,
	 *                      otherwise run exit action
	 */
	private void parentSwitchSubstate(State<E, P, C> from, State<E, P, C> to, boolean pauseOldState) {
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

	/**
	 * Pauses all substates recursively and runs their respective pause action
	 */
	private void pauseSubstates() {
		parallelSubstates.forEach(s -> s.pauseSubstates());
		parallelSubstates.forEach(s -> s.pause());
		pausedSubstates = parallelSubstates;
		parallelSubstates = new ArrayList<>();
	}

	/**
	 * Unpauses all substates recursively and runs their respective unpause action
	 */
	private void unpauseSubstates() {
		pausedSubstates.forEach(s -> s.unpause());
		pausedSubstates.forEach(s -> s.unpauseSubstates());
		parallelSubstates = pausedSubstates;
		pausedSubstates = new ArrayList<>();
	}

	/**
	 * Copies data to other state
	 * 
	 * @param other state to copy data to
	 */
	private void copyRelevantDataTo(State<E, P, C> other) {
		if (other != null) {
			other.setPauseActionIsExitAction(pauseActionIsExitAction);
			other.setUnpauseActionIsEntryAction(unpauseActionIsEntryAction);
			other.contextData = contextData;
		}
	}

	/**
	 * Runs all exit actions recursively, starting with the bottom mosts oldest
	 * parallel substate
	 */
	private void runExitActionRecurse() {
		for (var substate : parallelSubstates) {
			substate.runExitActionRecurse();
		}
		runExitAction();
	}

	private void runExitAction() {
		exitAction();
	}

	/**
	 * Runs all entry actions recursively, starting with this state
	 */
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
}