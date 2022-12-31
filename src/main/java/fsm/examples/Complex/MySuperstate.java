// package fsm.examples.Complex;

// import fsm.base.Superstate;

// public class MySuperstate extends Superstate<Event, Integer> {
	
// 	public MySuperstate(MyState from) {
// 		super(from, Event.class);
// 	}

// 	/**
// 	 * Use for explicit entry
// 	 * @param from Original state that this new state replaces
// 	 * @param sub Explicit entry state to enter
// 	 * @param eventClass Class of the event type this state handles
// 	 */
// 	public MySuperstate(State<Event, Integer> from, State<Event, Integer> sub, Class<E> eventClass) {
// 		this(from, List.of(sub), eventClass);
// 	}

// 	private MySuperstate(State<Event, Integer> from, List<State<Event, Integer>> parallelSubs, Class<E> classEvent) {
// 		this(from, classEvent);
// 		enterSubstates(parallelSubs);
// 	}

// 	public MySuperstate(State<Event, Integer> from, Class<E> classEvent) {
// 		this(from, classEvent);
// 		enterSubstates(defaultEntry());
// 	}

// }
